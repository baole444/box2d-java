package math;

import org.box2d.math.Rot;
import org.box2d.math.Vec2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RotTest {
    private static final float delta = 0.0001f;

    @Test
    @DisplayName("Rot default constructor create identity rotation")
    void testDefaultConstructor() {
        Rot r = new Rot();
        assertEquals(1.0f, r.c(), "c expected 1.0");
        assertEquals(0.0f, r.s(), "s expected 0.0");
        assertTrue(r.isIdentity(), "rotation expected identity");
    }

    @Test
    @DisplayName("Rot constructor create rotation with value")
    void testConstructor() {
        float angle = (float) Math.PI / 2.0f;
        Rot r = new Rot(angle);
        assertEquals(0.0f, r.c(), delta, "c expected 0.0");
        assertEquals(1.0f, r.s(), delta, "s expected 1.0");

        r = new Rot(0.6f, 0.8f);
        assertEquals(0.6f, r.c(), delta, "c expected 0.6");
        assertEquals(0.8f, r.s(), delta, "s expected 0.8");
    }

    @Test
    @DisplayName("Rot copy constructor create identical rotation")
    void testCopyConstructor() {
        Rot og = new Rot((float) Math.PI / 4.0f);
        Rot copy = new Rot(og);
        float ogC = og.c();
        assertEquals(og.c(), copy.c(), delta, "c expected matching values");
        assertEquals(og.s(), copy.s(), delta, "s expected matching values");

        copy.setC(1.0f);
        assertEquals(ogC, og.c(), delta, "original c expected unchanged value");
    }

    @Test
    @DisplayName("Rot angle")
    void testAngle() {
        Rot r = new Rot();
        r.setAngle((float) Math.PI);
        assertEquals(-1.0f, r.c(), delta, "c expected -1.0");
        assertEquals(0.0f, r.s(), delta, "s expected 0.0");

        float angle = (float) Math.PI / 3.0f;
        r.setAngle(angle);
        assertEquals(angle, r.angle(), delta, "angle expected matching value");
        angle = (float) - Math.PI / 2.0f;
        r.setAngle(angle);
        assertEquals(angle, r.angle(), delta, "angle expected matching value");
    }

    @Test
    @DisplayName("Rot direction vector")
    void testRotDirectionVector() {
        Rot r = new Rot((float) Math.PI / 4.0f);
        Vec2 unitV = r.xAxis();
        assertEquals(r.c(), unitV.x(), delta, "x expected c");
        assertEquals(r.s(), unitV.y(), delta, "y expected s");

        unitV = r.yAxis();
        assertEquals(-r.s(), unitV.x(), delta, "x expected -s");
        assertEquals(r.c(), unitV.y(), delta, "y expected c");
    }

    @Test
    @DisplayName("Rot identity")
    void testIdentityRot() {
        Rot r = new Rot((float) Math.PI / 2.0f);
        assertFalse(r.isIdentity(), "Rotation expected not identity");

        r.toIdentity();
        assertEquals(1.0f, r.c(), delta, "c expected 1.0");
        assertEquals(0.0f, r.s(), delta, "s expected 0.0");
        assertTrue(r.isIdentity(), "Rotation expected identity");

        r = new Rot();
        assertTrue(r.isIdentity(), "Rotation expected identity");
    }

    @Test
    @DisplayName("Rot copy independent")
    void testCopyIndependent() {
        Rot og = new Rot((float) Math.PI / 3.0f);
        Rot copy = og.copy();

        assertEquals(og.c(), copy.c(), delta, "c expected matching values");
        assertEquals(og.s(), copy.s(), delta, "s expected matching values");

        copy.setAngle((float) Math.PI / 2.0f);
        assertNotEquals(og.c(), copy.c(), delta, "original c expected unchanged value");
    }

    @Test
    @DisplayName("Rot valid check")
    void testValid() {
        Rot r = new Rot((float) Math.PI / 4.0f);
        assertTrue(r.isValid(), "Normal rotation expected valid");

        r.set(2.0f, 2.0f);
        assertFalse(r.isValid(), "Not normalized rotation expected invalid");
    }

    @Test
    @DisplayName("Rot from radians and degrees")
    void testFromRadDeg() {
        Rot r1 = Rot.fromDegrees(90.0f);
        assertEquals(0.0f, r1.c(), delta, "c expected 0.0");
        assertEquals(1.0f, r1.s(), delta, "s expected 1.0");
        r1 = Rot.fromDegrees(45.0f);
        assertEquals((float) Math.toRadians(45.0f), r1.angle(), delta, "angle expected PI/4");

        float rad = (float) Math.PI / 6.0f;
        Rot r2 = Rot.fromRadians(rad);
        assertEquals(rad, r2.angle(), delta, "angle expected matching value");
    }

    @Test
    @DisplayName("Rot builder pattern")
    void testBuilder() {
        Rot r = new Rot()
                .setAngle((float) Math.PI / 2.0f)
                .set(0.0f, 1.0f);

        assertEquals(0.0f, r.c(), delta, "c expected 0.0");
        assertEquals(1.0f, r.s(), delta, "s expected 1.0");
    }

    @Test
    @DisplayName("Rot memory segment access")
    void testMemSeg() {
        Rot r = new Rot(0.5f, 0.3f);
        assertNotNull(r.segment(), "Segment expected not null");
    }

    @Test
    @DisplayName("Rot hashCode")
    void testHashCode() {
        float rad = (float) Math.PI / 4.0f;
        Rot r1 = new Rot(rad);
        Rot r2 = new Rot(rad);
        assertEquals(r1.hashCode(), r2.hashCode(), "Equal rotations expected same hash code");
    }

    @Test
    @DisplayName("Rot equals")
    void testEquals() {
        float rad = (float) Math.PI / 4.0f;
        Rot r1 = new Rot(rad);
        Rot r2 = new Rot(rad);
        Rot r3 = new Rot((float) Math.PI / 2.0f);

        assertEquals(r1, r2, "Equal rotations expected equal");
        assertNotEquals(r1, r3, "Different rotation value expected not equal");
        assertEquals(r1, r1, "Same reference expected equal");
        assertNotEquals(null, r1, "Rotation and null expected not equal");
    }
}
