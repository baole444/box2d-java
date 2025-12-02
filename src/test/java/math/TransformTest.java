package math;

import org.box2d.math.Rot;
import org.box2d.math.Transform;
import org.box2d.math.Vec2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TransformTest {
    private static final float delta = 0.0001f;

    @Test
    @DisplayName("Transform default constructor creates identity transform")
    void testDefaultConstructor() {
        Transform t = new Transform();
        assertEquals(0.0f, t.x(), delta, "x expected 0.0");
        assertEquals(0.0f, t.y(), delta, "y expected 0.0");
        assertEquals(0.0f, t.angle(), delta, "angle expected 0.0");
    }

    @Test
    @DisplayName("Transform constructor creates transform with values")
    void testConstructor() {
        Vec2 pos = new Vec2(2.0f, 3.0f);
        Transform t = new Transform(pos);
        assertEquals(2.0f, t.x(), delta, "x expected 2.0");
        assertEquals(3.0f, t.y(), delta, "y expected 3.0");
        assertEquals(0.0f, t.angle(), delta, "angle expected 0.0");

        float angle = (float) Math.PI / 4.0f;
        t = new Transform(pos, angle);
        assertEquals(2.0f, t.x(), delta, "x expected 2.0");
        assertEquals(3.0f, t.y(), delta, "y expected 3.0");
        assertEquals(angle, t.angle(), delta, "angle expected PI/4");

        t = new Transform(1.5f, 2.5f, angle);
        assertEquals(1.5f, t.x(), delta, "x expected 1.5");
        assertEquals(2.5f, t.y(), delta, "y expected 2.5");
        assertEquals(angle, t.angle(), delta, "angle expected PI/4");
    }

    @Test
    @DisplayName("Transform copy constructor creates identical transform")
    void testCopyConstructor() {
        Transform og = new Transform(5.0f, 3.0f, (float) Math.PI / 3.0f);
        Transform copy = new Transform(og);

        assertEquals(og.x(), copy.x(), delta, "x expected matching values");
        assertEquals(og.y(), copy.y(), delta, "y expected matching values");
        assertEquals(og.angle(), copy.angle(), delta, "angle expected matching values");

        copy.setX(10.0f);
        assertEquals(5.0f, og.x(), delta, "original x expected unchanged value");
    }

    @Test
    @DisplayName("Transform position returns wrapper")
    void testPosition() {
        Transform t = new Transform(5.0f, 7.0f, 0.0f);
        Vec2 pos = t.position();
        assertEquals(5.0f, pos.x(), delta, "x expected 5.0");
        assertEquals(7.0f, pos.y(), delta, "y expected 7.0");

        pos.set(10.0f, 11.0f);
        assertEquals(10.0f, t.x(), delta, "x expected updated to 10.0");
        assertEquals(11.0f, t.y(), delta, "y expected updated to 11.0");
    }

    @Test
    @DisplayName("Transform rotation returns wrapper")
    void testRotation() {
        Transform t = new Transform(0.0f, 0.0f, (float) Math.PI / 4.0f);
        Rot rot = t.rotation();
        assertEquals((float) Math.PI / 4.0f, rot.angle(), delta, "angle expected PI/4");

        rot.setAngle((float) Math.PI / 2.0f);
        assertEquals((float) Math.PI / 2.0f, t.angle(), delta, "angle expected updated to PI/2");
    }

    @Test
    @DisplayName("Transform copy independent")
    void testCopyIndependent() {
        Transform og = new Transform(3.0f, 4.0f, (float) Math.PI / 3.0f);
        Transform copy = og.copy();

        assertEquals(og.x(), copy.x(), delta, "x expected matching values");
        assertEquals(og.y(), copy.y(), delta, "y expected matching values");
        assertEquals(og.angle(), copy.angle(), delta, "angle expected matching values");

        copy.setPosition(10.0f, 10.0f);
        assertEquals(3.0f, og.x(), delta, "original x expected unchanged value");
        assertEquals(4.0f, og.y(), delta, "original y expected unchanged value");
    }

    @Test
    @DisplayName("Transform builder pattern")
    void testBuilder() {
        Transform t = new Transform()
                .setPosition(1.0f, 2.0f)
                .setRotation((float) Math.PI / 6.0f)
                .setX(5.0f);

        assertEquals(5.0f, t.x(), delta, "x expected 5.0");
        assertEquals(2.0f, t.y(), delta, "y expected 2.0");
        assertEquals((float) Math.PI / 6.0f, t.angle(), delta, "angle expected PI/6");
    }

    @Test
    @DisplayName("Transform memory segment access")
    void testMemSeg() {
        Transform t = new Transform(2.0f, 3.0f, (float) Math.PI / 6.0f);
        assertNotNull(t.segment(), "Segment expected not null");
    }

    @Test
    @DisplayName("Transform hashCode")
    void testHashCode() {
        Transform t1 = new Transform(5.0f, 3.0f, (float) Math.PI / 4.0f);
        Transform t2 = new Transform(5.0f, 3.0f, (float) Math.PI / 4.0f);
        assertEquals(t1.hashCode(), t2.hashCode(), "Equal transforms expected same hash code");
    }

    @Test
    @DisplayName("Transform equals")
    void testEquals() {
        Transform t1 = new Transform(2.0f, 4.0f, (float) Math.PI / 3.0f);
        Transform t2 = new Transform(2.0f, 4.0f, (float) Math.PI / 3.0f);
        Transform t3 = new Transform(1.0f, 2.0f, (float) Math.PI / 6.0f);

        assertEquals(t1, t2, "Equal transforms expected equal");
        assertNotEquals(t1, t3, "Different transforms expected not equal");
        assertEquals(t1, t1, "Same reference expected equal");
        assertNotEquals(null, t1, "Transform and null expected not equal");
    }
}
