package org.box2d.collision;

import org.box2d.math.Vec2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AABBTest {
    private static final float delta = 0.0001f;

    @Test
    @DisplayName("AABB default constructor create box at origin")
    void testDefaultConstructor() {
        AABB box = new AABB();
        assertEquals(0.0f, box.lowerBound().x(), "lower x expected 0.0");
        assertEquals(0.0f, box.lowerBound().y(), "lower y expected 0.0");
        assertEquals(0.0f, box.upperBound().x(), "upper x expected 0.0");
        assertEquals(0.0f, box.upperBound().y(), "upper y expected 0.0");
    }

    @Test
    @DisplayName("AABB constructor create box with values")
    void testConstructorRoundTrip() {
        AABB box = new AABB(-1.0f, -2.0f, 3.0f, 4.0f);
        assertEquals(-1.0f, box.lowerBound().x(), "lower x expected -1.0");
        assertEquals(-2.0f, box.lowerBound().y(), "lower y expected -2.0");
        assertEquals(3.0f, box.upperBound().x(), "upper x expected 3.0");
        assertEquals(4.0f, box.upperBound().y(), "upper y expected 4.0");

        AABB fromVec = new AABB(new Vec2(1.0f, 1.0f), new Vec2(2.0f, 2.0f));
        assertEquals(new Vec2(1.0f, 1.0f), fromVec.lowerBound(), "lower expected (1, 1)");
        assertEquals(new Vec2(2.0f, 2.0f), fromVec.upperBound(), "upper expected (2, 2)");
    }

    @Test
    @DisplayName("AABB bound accessors write through to the segment")
    void testBoundViewWritesThrough() {
        AABB box = new AABB(0.0f, 0.0f, 1.0f, 1.0f);
        box.lowerBound().set(-5.0f, -6.0f);
        assertEquals(-5.0f, box.lowerBound().x(), "lower x expected mutation visible");
        assertEquals(-6.0f, box.lowerBound().y(), "lower y expected mutation visible");
    }

    @Test
    @DisplayName("AABB center")
    void testCenter() {
        AABB box = new AABB(-2.0f, -4.0f, 4.0f, 8.0f);
        Vec2 center = box.center();
        assertEquals(1.0f, center.x(), delta, "center x expected 1.0");
        assertEquals(2.0f, center.y(), delta, "center y expected 2.0");
    }

    @Test
    @DisplayName("AABB extents")
    void testExtents() {
        AABB box = new AABB(-2.0f, -4.0f, 4.0f, 8.0f);
        Vec2 extents = box.extents();
        assertEquals(3.0f, extents.x(), delta, "extent x expected 3.0");
        assertEquals(6.0f, extents.y(), delta, "extent y expected 6.0");
    }

    @Test
    @DisplayName("AABB combine grows to the union")
    void testCombine() {
        AABB box = new AABB(0.0f, 0.0f, 1.0f, 1.0f);
        box.combine(new AABB(-1.0f, -1.0f, 0.5f, 2.0f));
        assertEquals(-1.0f, box.lowerBound().x(), "lower x expected -1.0");
        assertEquals(-1.0f, box.lowerBound().y(), "lower y expected -1.0");
        assertEquals(1.0f, box.upperBound().x(), "upper x expected 1.0");
        assertEquals(2.0f, box.upperBound().y(), "upper y expected 2.0");
    }

    @Test
    @DisplayName("AABB contains")
    void testContains() {
        AABB outer = new AABB(-2.0f, -2.0f, 2.0f, 2.0f);
        assertTrue(outer.contains(new AABB(-1.0f, -1.0f, 1.0f, 1.0f)), "inner box expected contained");
        assertFalse(outer.contains(new AABB(-3.0f, 0.0f, 0.0f, 0.0f)), "overhanging box expected not contained");
    }

    @Test
    @DisplayName("AABB overlaps")
    void testOverlaps() {
        AABB box = new AABB(0.0f, 0.0f, 2.0f, 2.0f);
        assertTrue(box.overlaps(new AABB(1.0f, 1.0f, 3.0f, 3.0f)), "intersecting box expected overlap");
        assertFalse(box.overlaps(new AABB(3.0f, 3.0f, 4.0f, 4.0f)), "disjoint box expected no overlap");
    }

    @Test
    @DisplayName("AABB valid check")
    void testValid() {
        assertTrue(new AABB(-1.0f, -1.0f, 1.0f, 1.0f).isValid(), "normal box expected valid");
        assertFalse(new AABB(1.0f, 1.0f, -1.0f, -1.0f).isValid(), "inverted box expected invalid");
        assertFalse(new AABB(Float.NaN, 0.0f, 1.0f, 1.0f).isValid(), "NaN box expected invalid");
    }

    @Test
    @DisplayName("AABB copy independent")
    void testCopyIndependent() {
        AABB og = new AABB(0.0f, 0.0f, 1.0f, 1.0f);
        AABB copy = og.copy();
        assertEquals(og, copy, "copy expected equal to original");

        copy.upperBound().set(5.0f, 5.0f);
        assertEquals(1.0f, og.upperBound().x(), "original upper x expected unchanged");
    }

    @Test
    @DisplayName("AABB equals and hashCode")
    void testEqualsHashCode() {
        AABB a = new AABB(0.0f, 0.0f, 1.0f, 1.0f);
        AABB b = new AABB(0.0f, 0.0f, 1.0f, 1.0f);
        AABB c = new AABB(0.0f, 0.0f, 2.0f, 2.0f);

        assertEquals(a, b, "equal boxes expected equal");
        assertEquals(a.hashCode(), b.hashCode(), "equal boxes expected same hash code");
        assertNotEquals(a, c, "different boxes expected not equal");
        assertNotEquals(null, a, "box and null expected not equal");
    }

    @Test
    @DisplayName("AABB memory segment access")
    void testMemSeg() {
        assertNotNull(new AABB().segment(), "segment expected not null");
    }
}
