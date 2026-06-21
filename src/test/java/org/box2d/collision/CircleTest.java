package org.box2d.collision;

import org.box2d.math.Transform;
import org.box2d.math.Vec2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CircleTest {
    private static final float delta = 0.0001f;

    @Test
    @DisplayName("Circle constructor create circle with values")
    void testConstructorRoundTrip() {
        Circle circle = new Circle(new Vec2(1.0f, 2.0f), 3.0f);
        assertEquals(new Vec2(1.0f, 2.0f), circle.center(), "center expected (1, 2)");
        assertEquals(3.0f, circle.radius(), "radius expected 3.0");

        Circle fromFloats = new Circle(4.0f, 5.0f, 6.0f);
        assertEquals(new Vec2(4.0f, 5.0f), fromFloats.center(), "center expected (4, 5)");
        assertEquals(6.0f, fromFloats.radius(), "radius expected 6.0");

        assertEquals(2.0f, new Circle(2.0f).radius(), "radius-only constructor expected radius 2.0");
    }

    @Test
    @DisplayName("Circle center accessor writes through to the segment")
    void testCenterViewWritesThrough() {
        Circle circle = new Circle(1.0f);
        circle.center().set(7.0f, 8.0f);
        assertEquals(new Vec2(7.0f, 8.0f), circle.center(), "center expected mutation visible");
    }

    @Test
    @DisplayName("Circle test point in local frame")
    void testTestPoint() {
        Circle circle = new Circle(2.0f);
        assertTrue(circle.pointInCircle(new Vec2(0.0f, 0.0f)), "center expected inside");
        assertTrue(circle.pointInCircle(new Vec2(1.0f, 0.0f)), "near point expected inside");
        assertFalse(circle.pointInCircle(new Vec2(10.0f, 10.0f)), "far point expected outside");
    }

    @Test
    @DisplayName("Circle compute mass")
    void testComputeMass() {
        Circle circle = new Circle(2.0f);
        MassData data = circle.computeMass(1.0f);
        assertEquals((float) (Math.PI * 4.0), data.mass(), delta, "mass expected density * pi * r^2");
        assertEquals(new Vec2(0.0f, 0.0f), data.center(), "center of mass expected at origin");
        assertTrue(data.rotationalInertia() > 0.0f, "rotational inertia expected positive");
    }

    @Test
    @DisplayName("Circle compute AABB")
    void testComputeAABB() {
        Circle circle = new Circle(2.0f);
        AABB box = circle.computeAABB(new Transform());
        assertEquals(-2.0f, box.lowerBound().x(), delta, "lower x expected -2.0");
        assertEquals(-2.0f, box.lowerBound().y(), delta, "lower y expected -2.0");
        assertEquals(2.0f, box.upperBound().x(), delta, "upper x expected 2.0");
        assertEquals(2.0f, box.upperBound().y(), delta, "upper y expected 2.0");
    }

    @Test
    @DisplayName("Circle copy independent")
    void testCopyIndependent() {
        Circle og = new Circle(new Vec2(1.0f, 1.0f), 1.0f);
        Circle copy = og.copy();
        assertEquals(og, copy, "copy expected equal to original");

        copy.radius(99.0f);
        copy.center().set(5.0f, 5.0f);
        assertEquals(1.0f, og.radius(), "original radius expected unchanged");
        assertEquals(1.0f, og.center().x(), "original center expected unchanged");
    }

    @Test
    @DisplayName("Circle equals and hashCode")
    void testEqualsHashCode() {
        Circle a = new Circle(new Vec2(1.0f, 2.0f), 3.0f);
        Circle b = new Circle(new Vec2(1.0f, 2.0f), 3.0f);
        Circle c = new Circle(new Vec2(1.0f, 2.0f), 4.0f);

        assertEquals(a, b, "equal circles expected equal");
        assertEquals(a.hashCode(), b.hashCode(), "equal circles expected same hash code");
        assertNotEquals(a, c, "different circles expected not equal");
        assertNotEquals(null, a, "circle and null expected not equal");
    }
}
