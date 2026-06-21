package org.box2d.collision;

import org.box2d.math.Transform;
import org.box2d.math.Vec2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CapsuleTest {
    private static final float delta = 0.0001f;

    @Test
    @DisplayName("Capsule constructor create capsule with values")
    void testConstructorRoundTrip() {
        Capsule capsule = new Capsule(new Vec2(-1.0f, 0.0f), new Vec2(1.0f, 0.0f), 0.5f);
        assertEquals(new Vec2(-1.0f, 0.0f), capsule.center1(), "center1 expected (-1, 0)");
        assertEquals(new Vec2(1.0f, 0.0f), capsule.center2(), "center2 expected (1, 0)");
        assertEquals(0.5f, capsule.radius(), "radius expected 0.5");

        Capsule fromFloats = new Capsule(1.0f, 2.0f, 3.0f, 4.0f, 1.5f);
        assertEquals(new Vec2(1.0f, 2.0f), fromFloats.center1(), "center1 expected (1, 2)");
        assertEquals(new Vec2(3.0f, 4.0f), fromFloats.center2(), "center2 expected (3, 4)");
        assertEquals(1.5f, fromFloats.radius(), "radius expected 1.5");
    }

    @Test
    @DisplayName("Capsule center accessors write through to the segment")
    void testCenterViewWritesThrough() {
        Capsule capsule = new Capsule();
        capsule.center1().set(1.0f, 1.0f);
        capsule.center2().set(2.0f, 2.0f);
        assertEquals(new Vec2(1.0f, 1.0f), capsule.center1(), "center1 expected mutation visible");
        assertEquals(new Vec2(2.0f, 2.0f), capsule.center2(), "center2 expected mutation visible");
    }

    @Test
    @DisplayName("Capsule vertical factory fits a width x height bounding box")
    void testVertical() {
        Capsule capsule = Capsule.vertical(2.0f, 6.0f);
        assertEquals(1.0f, capsule.radius(), delta, "radius expected width/2");
        AABB box = capsule.computeAABB(new Transform());
        assertEquals(-1.0f, box.lowerBound().x(), delta, "lower x expected -width/2");
        assertEquals(-3.0f, box.lowerBound().y(), delta, "lower y expected -height/2");
        assertEquals(1.0f, box.upperBound().x(), delta, "upper x expected width/2");
        assertEquals(3.0f, box.upperBound().y(), delta, "upper y expected height/2");
    }

    @Test
    @DisplayName("Capsule horizontal factory fits a width x height bounding box")
    void testHorizontal() {
        Capsule capsule = Capsule.horizontal(6.0f, 2.0f);
        assertEquals(1.0f, capsule.radius(), delta, "radius expected height/2");
        AABB box = capsule.computeAABB(new Transform());
        assertEquals(-3.0f, box.lowerBound().x(), delta, "lower x expected -width/2");
        assertEquals(-1.0f, box.lowerBound().y(), delta, "lower y expected -height/2");
        assertEquals(3.0f, box.upperBound().x(), delta, "upper x expected width/2");
        assertEquals(1.0f, box.upperBound().y(), delta, "upper y expected height/2");
    }

    @Test
    @DisplayName("Capsule with equal width and height degenerates to a circle")
    void testSizeFactoryDegenerate() {
        Capsule capsule = Capsule.vertical(2.0f, 2.0f);
        assertEquals(1.0f, capsule.radius(), delta, "radius expected width/2");
        assertEquals(0.0f, capsule.center1().distance(capsule.center2()), delta, "centers expected coincident (a circle)");
    }

    @Test
    @DisplayName("Capsule test point in local frame")
    void testPointInCapsule() {
        Capsule capsule = new Capsule(new Vec2(-1.0f, 0.0f), new Vec2(1.0f, 0.0f), 0.5f);
        assertTrue(capsule.pointInCapsule(new Vec2(0.0f, 0.0f)), "center expected inside");
        assertFalse(capsule.pointInCapsule(new Vec2(0.0f, 5.0f)), "far point expected outside");
    }

    @Test
    @DisplayName("Capsule compute mass")
    void testComputeMass() {
        Capsule capsule = new Capsule(new Vec2(-1.0f, 0.0f), new Vec2(1.0f, 0.0f), 0.5f);
        MassData data = capsule.computeMass(1.0f);
        assertTrue(data.mass() > 0.0f, "mass expected positive");
        assertEquals(0.0f, data.center().x(), delta, "center of mass x expected at origin");
        assertEquals(0.0f, data.center().y(), delta, "center of mass y expected at origin");
    }

    @Test
    @DisplayName("Capsule compute AABB")
    void testComputeAABB() {
        Capsule capsule = new Capsule(new Vec2(-1.0f, 0.0f), new Vec2(1.0f, 0.0f), 0.5f);
        AABB box = capsule.computeAABB(new Transform());
        assertEquals(-1.5f, box.lowerBound().x(), delta, "lower x expected -1.5");
        assertEquals(-0.5f, box.lowerBound().y(), delta, "lower y expected -0.5");
        assertEquals(1.5f, box.upperBound().x(), delta, "upper x expected 1.5");
        assertEquals(0.5f, box.upperBound().y(), delta, "upper y expected 0.5");
    }

    @Test
    @DisplayName("Capsule copy independent")
    void testCopyIndependent() {
        Capsule og = new Capsule(new Vec2(-1.0f, 0.0f), new Vec2(1.0f, 0.0f), 0.5f);
        Capsule copy = og.copy();
        assertEquals(og, copy, "copy expected equal to original");

        copy.radius(99.0f);
        copy.center1().set(5.0f, 5.0f);
        assertEquals(0.5f, og.radius(), "original radius expected unchanged");
        assertEquals(-1.0f, og.center1().x(), "original center1 expected unchanged");
    }

    @Test
    @DisplayName("Capsule equals and hashCode")
    void testEqualsHashCode() {
        Capsule a = new Capsule(new Vec2(-1.0f, 0.0f), new Vec2(1.0f, 0.0f), 0.5f);
        Capsule b = new Capsule(new Vec2(-1.0f, 0.0f), new Vec2(1.0f, 0.0f), 0.5f);
        Capsule c = new Capsule(new Vec2(-1.0f, 0.0f), new Vec2(1.0f, 0.0f), 0.9f);

        assertEquals(a, b, "equal capsules expected equal");
        assertEquals(a.hashCode(), b.hashCode(), "equal capsules expected same hash code");
        assertNotEquals(a, c, "different capsules expected not equal");
        assertNotEquals(null, a, "capsule and null expected not equal");
    }
}
