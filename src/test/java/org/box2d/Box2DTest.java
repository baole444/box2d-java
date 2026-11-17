package org.box2d;

import org.box2d.dynamics.World;
import org.box2d.internal.b2CosSin;
import org.box2d.math.CosSin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The deterministic trigonometry is a Java port, so these tests compare it bit-for-bit against the
 * native Box2D functions (through the internal bindings), then check Box2D's own accuracy bounds.
 */
class Box2DTest {
    // Box2D's test_math.c tolerances
    private static final float atanTolerance = 0.00004f;
    private static final float cosSinTolerance = 0.002f;

    private static float nativeAtan2(float y, float x) {
        return org.box2d.internal.Box2D.b2Atan2(y, x);
    }

    private static CosSin nativeCosSin(Arena arena, float radians) {
        MemorySegment cs = org.box2d.internal.Box2D.b2ComputeCosSin(arena, radians);
        return new CosSin(b2CosSin.cosine(cs), b2CosSin.sine(cs));
    }

    private static void assertAtan2Parity(float y, float x) {
        assertEquals(nativeAtan2(y, x), Box2D.atan2(y, x), () -> "atan2(" + y + ", " + x + ") expected native result");
    }

    private static void assertCosSinParity(Arena arena, float radians) {
        assertEquals(nativeCosSin(arena, radians), Box2D.computeCosSin(radians), () -> "computeCosSin(" + radians + ") expected native result");
    }

    @Test
    @DisplayName("logStream receives Box2D log messages and discards them when null")
    void testLogStream() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        World world = new World();
        try {
            assertNull(Box2D.logStream(), "logging expected off by default");
            world.restitutionIterations(-1);
            assertEquals(0, bytes.size(), "no stream expected nothing printed");

            Box2D.logStream(stream);
            assertSame(stream, Box2D.logStream(), "stream expected stored");
            world.restitutionIterations(-1);
            String log = bytes.toString(StandardCharsets.UTF_8);
            assertTrue(log.contains("invalid input"), () -> "rejected input expected logged, got: " + log);
            assertTrue(log.contains("b2World_SetRestitutionIterations"), () -> "log expected to name the function, got: " + log);
            assertTrue(log.endsWith(System.lineSeparator()) && !log.endsWith(System.lineSeparator() + System.lineSeparator()), () -> "one message expected per line, got: " + log);

            Box2D.logStream(null);
            int size = bytes.size();
            world.restitutionIterations(-1);
            assertEquals(size, bytes.size(), "null stream expected to discard messages");
        } finally {
            Box2D.logStream(null);
            world.destroy();
        }
    }

    @Test
    @DisplayName("atan2 matches native b2Atan2 bit for bit on a grid")
    void testAtan2GridParity() {
        for (int i = -200; i <= 200; i++) {
            for (int j = -200; j <= 200; j++) assertAtan2Parity(i * 0.01f, j * 0.01f);
        }
    }

    @Test
    @DisplayName("atan2 matches native b2Atan2 bit for bit on random and special inputs")
    void testAtan2RandomParity() {
        Random random = new Random(42);
        for (int i = 0; i < 100_000; i++) {
            float scale = (float) Math.pow(10.0, random.nextInt(13) - 6);
            assertAtan2Parity((random.nextFloat() * 2.0f - 1.0f) * scale, (random.nextFloat() * 2.0f - 1.0f) * scale);
        }
        float[] special = {0.0f, -0.0f, 1.0f, -1.0f, Float.MIN_VALUE, -Float.MIN_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NaN};
        for (float y : special) {
            for (float x : special) assertAtan2Parity(y, x);
        }
    }

    @Test
    @DisplayName("computeCosSin matches native b2ComputeCosSin bit for bit")
    void testCosSinParity() {
        try (Arena arena = Arena.ofConfined()) {
            for (int i = -10_000; i <= 10_000; i++) assertCosSinParity(arena, Box2D.Pi * i * 0.001f);
            Random random = new Random(42);
            for (int i = 0; i < 100_000; i++) assertCosSinParity(arena, (random.nextFloat() * 2.0f - 1.0f) * 9999.0f);
            float[] special = {0.0f, -0.0f, Box2D.Pi, -Box2D.Pi, 0.5f * Box2D.Pi, -0.5f * Box2D.Pi, Float.MIN_VALUE, Float.NaN};
            for (float radians : special) assertCosSinParity(arena, radians);
        }
    }

    @Test
    @DisplayName("atan2 stays within Box2D's accuracy bound and returns 0 at the origin")
    void testAtan2Accuracy() {
        for (int i = -100; i <= 100; i++) {
            for (int j = -100; j <= 100; j++) {
                float y = i * 0.01f;
                float x = j * 0.01f;
                assertEquals((float) StrictMath.atan2(y, x), Box2D.atan2(y, x), atanTolerance, () -> "atan2(" + y + ", " + x + ")");
            }
        }
        assertEquals(0.0f, Box2D.atan2(0.0f, 0.0f), "atan2(0, 0) expected 0");
    }

    @Test
    @DisplayName("computeCosSin stays within Box2D's accuracy bound and is normalised")
    void testCosSinAccuracy() {
        for (int i = -1000; i < 1000; i++) {
            float angle = Box2D.Pi * i * 0.01f;
            CosSin cs = Box2D.computeCosSin(angle);
            assertEquals((float) StrictMath.cos(angle), cs.cosine(), cosSinTolerance, () -> "cosine of " + angle);
            assertEquals((float) StrictMath.sin(angle), cs.sine(), cosSinTolerance, () -> "sine of " + angle);
            assertEquals(1.0f, cs.cosine() * cs.cosine() + cs.sine() * cs.sine(), 0.0001f, "pair expected normalised");
        }
        assertEquals(new CosSin(1.0f, 0.0f), Box2D.computeCosSin(0.0f), "angle 0 expected exact identity");
    }

    @Test
    @DisplayName("unwindAngle maps angles into [-pi, pi] and clamps huge angles")
    void testUnwindAngle() {
        for (int i = -1000; i < 1000; i++) {
            float angle = Box2D.Pi * i * 0.01f;
            float unwound = Box2D.unwindAngle(angle);
            assertTrue(-Box2D.Pi <= unwound && unwound <= Box2D.Pi, () -> angle + " unwound to " + unwound);
        }
        assertEquals(0.5f, Box2D.unwindAngle(0.5f), "angle in range expected unchanged");
        assertEquals(0.5f, Box2D.unwindAngle(0.5f + 4.0f * Box2D.Pi), 0.00001f, "two turns expected removed");
        assertEquals(Box2D.unwindAngle(1.0e6f), Box2D.unwindAngle(2.0e6f), "huge angle expected clamped");
        assertEquals(Box2D.unwindAngle(-1.0e6f), Box2D.unwindAngle(-2.0e6f), "huge negative angle expected clamped");
    }
}
