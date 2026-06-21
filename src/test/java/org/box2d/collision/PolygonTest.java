package org.box2d.collision;

import org.box2d.math.Rot;
import org.box2d.math.Transform;
import org.box2d.math.Vec2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class PolygonTest {
    private static final float delta = 0.0001f;

    @Test
    @DisplayName("Polygon box factory")
    void testBox() {
        Polygon box = Polygon.box(1.0f, 2.0f);
        assertEquals(4, box.count(), "box expected 4 vertices");
        assertEquals(0.0f, box.radius(), "box expected zero rounding radius");
        assertEquals(new Vec2(0.0f, 0.0f), box.centroid(), "box centroid expected at origin");
    }

    @Test
    @DisplayName("Polygon square factory")
    void testSquare() {
        Polygon square = Polygon.square(2.0f);
        assertEquals(4, square.count(), "square expected 4 vertices");
        AABB box = square.computeAABB(new Transform());
        assertEquals(-2.0f, box.lowerBound().x(), delta, "lower x expected -2.0");
        assertEquals(2.0f, box.upperBound().y(), delta, "upper y expected 2.0");
    }

    @Test
    @DisplayName("Polygon offset box centroid follows the center")
    void testOffsetBox() {
        Polygon box = Polygon.box(1.0f, 1.0f, new Vec2(5.0f, 5.0f), new Rot());
        assertEquals(5.0f, box.centroid().x(), delta, "centroid x expected 5.0");
        assertEquals(5.0f, box.centroid().y(), delta, "centroid y expected 5.0");
    }

    @Test
    @DisplayName("Polygon vertex access and bounds checking")
    void testVertexAccess() {
        Polygon box = Polygon.box(1.0f, 1.0f);
        for (int i = 0; i < box.count(); i++) {
            Vec2 vertex = box.vertex(i);
            assertEquals(1.0f, Math.abs(vertex.x()), delta, "box vertex x expected at +/-1");
            assertEquals(1.0f, Math.abs(vertex.y()), delta, "box vertex y expected at +/-1");
        }
        assertThrows(IndexOutOfBoundsException.class, () -> box.vertex(box.count()), "out-of-range index expected to throw");
        assertThrows(IndexOutOfBoundsException.class, () -> box.vertex(-1), "negative index expected to throw");
    }

    @Test
    @DisplayName("Polygon from vertices builds a convex hull")
    void testFromVertices() {
        Polygon triangle = Polygon.fromVertices(new Vec2(0.0f, 0.0f), new Vec2(2.0f, 0.0f), new Vec2(0.0f, 2.0f));
        assertEquals(3, triangle.count(), "triangle expected 3 vertices");
    }

    @Test
    @DisplayName("Polygon from vertices rejects invalid point counts")
    void testFromVerticesInvalidCount() {
        assertThrows(IllegalArgumentException.class, () -> Polygon.fromVertices(new Vec2(0.0f, 0.0f), new Vec2(1.0f, 0.0f)), "fewer than 3 points expected to throw");
    }

    @Test
    @DisplayName("Polygon compute mass")
    void testComputeMass() {
        Polygon box = Polygon.box(1.0f, 1.0f);
        MassData data = box.computeMass(2.0f);
        assertEquals(8.0f, data.mass(), delta, "mass expected density * area (2 * 4)");
        assertEquals(0.0f, data.center().x(), delta, "center of mass x expected at origin");
        assertEquals(0.0f, data.center().y(), delta, "center of mass y expected at origin");
    }

    @Test
    @DisplayName("Polygon compute AABB")
    void testComputeAABB() {
        Polygon box = Polygon.box(1.0f, 2.0f);
        AABB aabb = box.computeAABB(new Transform());
        assertEquals(-1.0f, aabb.lowerBound().x(), delta, "lower x expected -1.0");
        assertEquals(-2.0f, aabb.lowerBound().y(), delta, "lower y expected -2.0");
        assertEquals(1.0f, aabb.upperBound().x(), delta, "upper x expected 1.0");
        assertEquals(2.0f, aabb.upperBound().y(), delta, "upper y expected 2.0");
    }

    @Test
    @DisplayName("Polygon test point in local frame")
    void testPointInPolygon() {
        Polygon box = Polygon.box(1.0f, 1.0f);
        assertTrue(box.pointInPolygon(new Vec2(0.0f, 0.0f)), "center expected inside");
        assertFalse(box.pointInPolygon(new Vec2(5.0f, 5.0f)), "far point expected outside");
    }

    @Test
    @DisplayName("Polygon copy is an independent allocation")
    void testCopyIndependent() {
        Polygon og = Polygon.box(1.0f, 2.0f);
        Polygon copy = og.copy();
        assertEquals(og, copy, "copy expected equal to original");
        assertNotSame(og.segment(), copy.segment(), "copy expected a separate segment");
    }

    @Test
    @DisplayName("Polygon equals and hashCode")
    void testEqualsHashCode() {
        Polygon a = Polygon.box(1.0f, 1.0f);
        Polygon b = Polygon.box(1.0f, 1.0f);
        Polygon c = Polygon.box(2.0f, 2.0f);

        assertEquals(a, b, "equal polygons expected equal");
        assertEquals(a.hashCode(), b.hashCode(), "equal polygons expected same hash code");
        assertNotEquals(a, c, "different polygons expected not equal");
        assertNotEquals(null, a, "polygon and null expected not equal");
    }
}
