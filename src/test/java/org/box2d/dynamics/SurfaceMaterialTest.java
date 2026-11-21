package org.box2d.dynamics;

import org.box2d.internal.b2SurfaceMaterial;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SurfaceMaterialTest {
    private static final float delta = 0.0001f;

    @Test
    @DisplayName("SurfaceMaterial default seeds the Box2D defaults")
    void testDefaults() {
        SurfaceMaterial material = new SurfaceMaterial();
        assertEquals(0.6f, material.friction(), delta, "default friction expected 0.6");
        assertEquals(0.0f, material.restitution(), delta, "default restitution expected 0");
        assertEquals(0.0f, material.rollingResistance(), delta, "default rolling resistance expected 0");
        assertEquals(0.0f, material.tangentSpeed(), delta, "default tangent speed expected 0");
        assertEquals(0L, material.userMaterialId(), "default user material expected 0");
        assertEquals(0, material.customColor(), "default custom colour expected 0");
    }

    @Test
    @DisplayName("SurfaceMaterial fields round-trip, including a full 64-bit user material")
    void testRoundTrip() {
        long id = 0xFEDCBA9876543210L;
        SurfaceMaterial material = new SurfaceMaterial()
                .friction(0.25f)
                .restitution(0.5f)
                .rollingResistance(0.1f)
                .tangentSpeed(-2.0f)
                .userMaterialId(id)
                .customColor(0xFF8800);
        assertEquals(0.25f, material.friction(), delta, "friction expected to round-trip");
        assertEquals(0.5f, material.restitution(), delta, "restitution expected to round-trip");
        assertEquals(0.1f, material.rollingResistance(), delta, "rolling resistance expected to round-trip");
        assertEquals(-2.0f, material.tangentSpeed(), delta, "tangent speed expected to round-trip");
        assertEquals(id, material.userMaterialId(), "user material expected to keep all 64 bits");
        assertEquals(0xFF8800, material.customColor(), "custom colour expected to round-trip");
    }

    @Test
    @DisplayName("SurfaceMaterial copy is independent, a wrapped segment writes through")
    void testOwnership() {
        SurfaceMaterial original = new SurfaceMaterial().friction(0.3f);
        SurfaceMaterial copy = original.copy();
        assertEquals(original, copy, "copy expected equal to the original");
        copy.friction(0.9f);
        assertEquals(0.3f, original.friction(), delta, "changing the copy expected not to change the original");

        MemorySegment segment = Arena.ofAuto().allocate(b2SurfaceMaterial.layout());
        SurfaceMaterial view = new SurfaceMaterial(segment);
        view.restitution(0.75f);
        assertEquals(0.75f, b2SurfaceMaterial.restitution(segment), delta, "view expected to write through to the segment");
    }

    @Test
    @DisplayName("SurfaceMaterial equality and hash code are by value")
    void testEquality() {
        SurfaceMaterial a = new SurfaceMaterial().userMaterialId(7L);
        SurfaceMaterial b = new SurfaceMaterial().userMaterialId(7L);
        assertEquals(a, b, "materials with the same values expected equal");
        assertEquals(a.hashCode(), b.hashCode(), "equal materials expected the same hash code");
        assertNotEquals(a, b.userMaterialId(8L), "materials with different values expected not equal");
        assertNotEquals(null, a, "material expected not equal to null");
    }
}
