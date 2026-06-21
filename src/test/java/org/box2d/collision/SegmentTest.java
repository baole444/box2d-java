package org.box2d.collision;

import org.box2d.math.Transform;
import org.box2d.math.Vec2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SegmentTest {
    private static final float delta = 0.0001f;

    @Test
    @DisplayName("Segment constructor create segment with values")
    void testConstructorRoundTrip() {
        Segment segment = new Segment(new Vec2(-1.0f, 0.0f), new Vec2(1.0f, 2.0f));
        assertEquals(new Vec2(-1.0f, 0.0f), segment.point1(), "point1 expected (-1, 0)");
        assertEquals(new Vec2(1.0f, 2.0f), segment.point2(), "point2 expected (1, 2)");

        Segment fromFloats = new Segment(1.0f, 2.0f, 3.0f, 4.0f);
        assertEquals(new Vec2(1.0f, 2.0f), fromFloats.point1(), "point1 expected (1, 2)");
        assertEquals(new Vec2(3.0f, 4.0f), fromFloats.point2(), "point2 expected (3, 4)");
    }

    @Test
    @DisplayName("Segment endpoint accessors write through to the segment")
    void testEndpointViewWritesThrough() {
        Segment segment = new Segment();
        segment.point1().set(1.0f, 1.0f);
        segment.point2().set(2.0f, 2.0f);
        assertEquals(new Vec2(1.0f, 1.0f), segment.point1(), "point1 expected mutation visible");
        assertEquals(new Vec2(2.0f, 2.0f), segment.point2(), "point2 expected mutation visible");
    }

    @Test
    @DisplayName("Segment length")
    void testLength() {
        Segment segment = new Segment(0.0f, 0.0f, 3.0f, 4.0f);
        assertEquals(5.0f, segment.length(), delta, "length expected 5.0");
    }

    @Test
    @DisplayName("Segment compute AABB")
    void testComputeAABB() {
        Segment segment = new Segment(-1.0f, 0.0f, 1.0f, 0.0f);
        AABB box = segment.computeAABB(new Transform());
        assertEquals(-1.0f, box.lowerBound().x(), delta, "lower x expected -1.0");
        assertEquals(0.0f, box.lowerBound().y(), delta, "lower y expected 0.0");
        assertEquals(1.0f, box.upperBound().x(), delta, "upper x expected 1.0");
        assertEquals(0.0f, box.upperBound().y(), delta, "upper y expected 0.0");
    }

    @Test
    @DisplayName("Segment copy independent")
    void testCopyIndependent() {
        Segment og = new Segment(-1.0f, 0.0f, 1.0f, 0.0f);
        Segment copy = og.copy();
        assertEquals(og, copy, "copy expected equal to original");

        copy.point1().set(5.0f, 5.0f);
        assertEquals(-1.0f, og.point1().x(), "original point1 expected unchanged");
    }

    @Test
    @DisplayName("Segment equals and hashCode")
    void testEqualsHashCode() {
        Segment a = new Segment(-1.0f, 0.0f, 1.0f, 0.0f);
        Segment b = new Segment(-1.0f, 0.0f, 1.0f, 0.0f);
        Segment c = new Segment(-1.0f, 0.0f, 2.0f, 0.0f);

        assertEquals(a, b, "equal segments expected equal");
        assertEquals(a.hashCode(), b.hashCode(), "equal segments expected same hash code");
        assertNotEquals(a, c, "different segments expected not equal");
        assertNotEquals(null, a, "segment and null expected not equal");
    }
}
