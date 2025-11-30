package math;

import org.box2d.math.Vec2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Vec2Test {
    static final float delta = 0.0001f;

    @Test
    @DisplayName("Vec2 default constructor create zero vector")
    void testDefaultConstructor() {
        Vec2 v = new Vec2();
        assertEquals(0.0f, v.x(), "x expected 0.0");
        assertEquals(0.0f, v.y(), "y expected 0.0");
    }

    @Test
    @DisplayName("Vec2 constructor create vector with value")
    void testConstructor() {
        Vec2 v = new Vec2(2.4f, 1.5f);
        assertEquals(2.4f, v.x(), "x expected 2.4");
        assertEquals(1.5f, v.y(), "y expected 1.5");
    }

    @Test
    @DisplayName("Vec2 copy constructor create identical vector")
    void testCopyConstructor() {
        Vec2 og = new Vec2(5.4f, 8.3f);
        Vec2 copy = new Vec2(og);

        assertEquals(og.x(), copy.x(), "x expected matching values");
        assertEquals(og.y(), copy.y(), "y expected matching values");

        copy.setX(6.2f);
        assertEquals(5.4f, og.x(), "original x expected unchanged value");
    }

    @Test
    @DisplayName("Vec2 setter modify vector's values")
    void testSetters() {
        Vec2 v = new Vec2();

        v.setX(3.0f);
        assertEquals(3.0f, v.x(), "x expected updated value");

        v.setY(2.0f);
        assertEquals(2.0f, v.y(), "y expected updated value");

        v.set(4.0f, 8.0f);
        assertEquals(4.0f, v.x(), "x expected 4.0");
        assertEquals(8.0f, v.y(), "y expected 8.0");
    }

    @Test
    @DisplayName("Vec2 to zero modify vector to (0, 0)")
    void testToZero() {
        Vec2 v = new Vec2(40.4f, 32.6f);
        v.toZero();
        assertEquals(0.0f, v.x(), "x expected 0");
        assertEquals(0.0f, v.y(), "y expected 0");
    }

    @Test
    @DisplayName("Vec2 negate operation")
    void testNegate() {
        Vec2 v = new Vec2(2.0f, -7.0f);
        v.negate();
        assertEquals(-2.0f, v.x(), "x expected negation");
        assertEquals(7.0f, v.y(), "y expected negation");
    }

    @Test
    @DisplayName("Vec2 vector addition")
    void testVectorAdd() {
        Vec2 v1 = new Vec2(1.0f, 2.0f);
        Vec2 v2 = new Vec2(3.0f, 4.0f);
        v1.add(v2);
        assertEquals(4.0f, v1.x(), "x expected 4.0");
        assertEquals(6.0f, v1.y(), "x expected 6.0");
    }

    @Test
    @DisplayName("Vec2 scalar addition")
    void testScalarAdd() {
        Vec2 v = new Vec2(1.0f, 2.0f);
        v.add(0.5f, 4.7f);
        assertEquals(1.5f, v.x(), "x expected 1.5");
        assertEquals(6.7f, v.y(), "y expected 6.7");
    }

    @Test
    @DisplayName("Vec2 vector subtraction")
    void testVectorSub() {
        Vec2 v1 = new Vec2(6.0f, 9.0f);
        Vec2 v2 = new Vec2(7.0f, 4.0f);
        v1.sub(v2);
        assertEquals(-1.0f, v1.x(), "x expected -1.0");
        assertEquals(5.0f, v1.y(), "y expected 5.0");
    }

    @Test
    @DisplayName("Vec2 scalar subtraction")
    void testScalarSub() {
        Vec2 v = new Vec2(40.0f, 24.0f);
        v.sub(14.3f, 25.6f);
        assertEquals(25.7f, v.x(), delta, "x expect 25.7");
        assertEquals(-1.6f, v.y(), delta, "y expected -1.6");
    }

    @Test
    @DisplayName("Vec2 scalar multiplication")
    void testScalarMul() {
        Vec2 v = new Vec2(2.0f, 5.0f);
        v.mul(4.5f);
        assertEquals(9.0f, v.x(), "x expected 9.0");
        assertEquals(22.5f, v.y(), "y expected 22.5f");

        v.set(4.0f, 2.0f);
        v.mul(3.0f, 5.0f);
        assertEquals(12.0f, v.x(), "x expected 12.0");
        assertEquals(10.0f, v.y(), "y expected 10.0");
    }

    @Test
    @DisplayName("Vec2 component-wise multiplication")
    void testVectorMul() {
        Vec2 v1 = new Vec2(2.4f, 3.6f);
        Vec2 v2 = new Vec2(1.3f, 6.5f);
        v1.mul(v2);
        assertEquals(3.12f, v1.x(), delta, "x expected 3.12");
        assertEquals(23.4f, v1.y(), delta, "y expected 23.4");
    }

    @Test
    @DisplayName("Vec2 scalar division")
    void testScalarDiv() {
        Vec2 v = new Vec2(6.6f, 9.3f);
        v.div(3.0f);
        assertEquals(2.2f, v.x(), delta, "x expected 2.2");
        assertEquals(3.1f, v.y(), delta, "y expected 3.1");

        v.set(12.0f, 24.0f);
        v.div(4.0f, 3.0f);
        assertEquals(3.0f, v.x(), "x expected 3.0");
        assertEquals(8.0f, v.y(), "y expected 8.0");
    }

    @Test
    @DisplayName("Vec2 component-wise division")
    void testVectorDiv() {
        Vec2 v1 = new Vec2(20.0f, 60.0f);
        Vec2 v2 = new Vec2(5.0f, 10.0f);
        v1.div(v2);
        assertEquals(4.0f, v1.x(), "x expected 4.0");
        assertEquals(6.0f, v1.y(), "v expected 6.0");
    }

    @Test
    @DisplayName("Vec2 dot product")
    void testDotProduct() {
        Vec2 v1 = new Vec2(3.0f, 4.0f);
        Vec2 v2 = new Vec2(2.0f, 1.0f);
        float dot = v1.dot(v2);
        assertEquals(10.0f, dot, "Dot product expected 10.0");
    }

    @Test
    @DisplayName("Vec2 cross product (scalar)")
    void testCrossProduct() {
        Vec2 v1 = new Vec2(3.0f, 4.0f);
        Vec2 v2 = new Vec2(2.0f, 1.0f);
        float cross = v1.cross(v2);
        assertEquals(-5.0f, cross, "Cross product expected -5.0");
    }

    @Test
    @DisplayName("Vec2 cross product with scalar")
    void testScalarCrossProduct() {
        Vec2 v = new Vec2(3.0f, 4.0f);
        Vec2 result = v.cross(2.0f);

        assertEquals(8.0f, result.x(), "x expected 8.0");
        assertEquals(-6.0f, result.y(), "y expected -6.0");
    }

    @Test
    @DisplayName("Vec2 length")
    void testLength() {
        Vec2 v = new Vec2(3.0f, 4.0f);
        assertEquals(5.0f, v.length(), delta, "Length expected 5.0");
    }

    @Test
    @DisplayName("Vec2 length squared")
    void testLengthSquared() {
        Vec2 v = new Vec2(3.0f, 4.0f);
        assertEquals(25.0f, v.lengthSquared(), "Length squared expected 25.0");
    }

    @Test
    @DisplayName("Vec2 normalize create unit vector")
    void testNormalize() {
        Vec2 v = new Vec2(3.0f, 4.0f);
        v.normalize();
        assertEquals(1.0f, v.length(), delta, "Normalized vector length expected 1.0");
        assertEquals(0.6f, v.x(), delta, "x expected 0.6");
        assertEquals(0.8f, v.y(), delta, "y expected 0.8");
    }

    @Test
    @DisplayName("Vec2 handle normalize zero vector")
    void testZeroNormalize() {
        Vec2 v = new Vec2();
        v.normalize();
        assertEquals(0.0f, v.x(), "x expected 0.0");
        assertEquals(0.0f, v.y(), "y expected 0.0");
    }

    @Test
    @DisplayName("Vec2 normalize to desired length")
    void testNormalizeLength() {
        Vec2 v = new Vec2(2.0f, 1.0f);
        v.normalize(10.0f);
        assertEquals(10.0f, v.length(), delta, "Normalized to length expected 10.0");
    }

    @Test
    @DisplayName("Vec2 distance")
    void testDistance() {
        Vec2 v1 = new Vec2();
        Vec2 v2 = new Vec2(3.0f, 4.0f);
        assertEquals(5.0f, v1.distance(v2), delta, "Distance expected 5.0");
    }

    @Test
    @DisplayName("Vec2 distance squared")
    void testDistanceSquared() {
        Vec2 v1 = new Vec2();
        Vec2 v2 = new Vec2(3.0f, 4.0f);
        assertEquals(25.0f, v1.distanceSquared(v2), "Distance squared expected 25.0");
    }

    @Test
    @DisplayName("Vec2 copy independent")
    void testCopyIndependent() {
        Vec2 og = new Vec2(1.0f, 2.0f);
        Vec2 copy = og.copy();

        assertEquals(og.x(), copy.x(), "x expected matching values");
        assertEquals(og.y(), copy.y(), "y expected matching values");

        copy.setX(7.0f);
        assertEquals(1.0f, og.x(), "original x expected unchanged value");
    }

    @Test
    @DisplayName("Vec2 valid check")
    void testValid() {
        Vec2 v = new Vec2(1.0f, 4.0f);
        assertTrue(v.isValid(), "Normal vector expected valid");

        v = new Vec2(Float.NaN, 4.0f);
        assertFalse(v.isValid(), "NaN vector expected invalid");
        v.set(3.0f, Float.NaN);
        assertFalse(v.isValid(), "NaN vector expected invalid");

        v = new Vec2(Float.POSITIVE_INFINITY, 4.0f);
        assertFalse(v.isValid(), "Infinite vector expected invalid");
        v.set(3.0f, Float.NEGATIVE_INFINITY);
        assertFalse(v.isValid(), "Infinite vector expected invalid");
    }

    @Test
    @DisplayName("Vec2 builder pattern")
    void testBuilder() {
        Vec2 v = new Vec2(1.0f, 1.0f)
                .add(2.0f, 3.0f)
                .mul(2.0f)
                .sub(1.0f, 1.0f);

        assertEquals(5.0f, v.x(), "Final x expected 5.0");
        assertEquals(7.0f, v.y(), "Final y expected 7.0");
    }

    @Test
    @DisplayName("Vec2 memory segment access")
    void testMemSeg() {
        Vec2 v = new Vec2(4.0f, 6.0f);
        assertNotNull(v.segment(), "Segment expected not null");
    }

    @Test
    @DisplayName("Vec2 hashCode")
    void testHashCode() {
        Vec2 v1 = new Vec2(15.0f, 56.0f);
        Vec2 v2 = new Vec2(15.0f, 56.0f);
        assertEquals(v1.hashCode(), v2.hashCode(), "Equal vectors expected same hash code");
    }

    @Test
    @DisplayName("Vec2 equals")
    void testEquals() {
        Vec2 v1 = new Vec2(2.0f, 4.0f);
        Vec2 v2 = new Vec2(2.0f, 4.0f);
        Vec2 v3 = new Vec2(1.0f, 2.0f);

        assertEquals(v1, v2, "Equals vector value expected equal");
        assertNotEquals(v1, v3, "Different vector value expected not equal");
        assertEquals(v1, v1, "The same vector reference expected equal");
        assertNotEquals(null, v1, "Vector and null expected not equal");
    }
}
