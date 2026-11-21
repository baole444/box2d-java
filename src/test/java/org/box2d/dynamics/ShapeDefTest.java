package org.box2d.dynamics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShapeDefTest {
    private static final float delta = 0.0001f;

    @Test
    @DisplayName("ShapeDef default seeds the Box2D defaults, with every event flag off")
    void testDefaults() {
        ShapeDef def = new ShapeDef();
        assertNull(def.userData(), "default user data expected null");
        assertEquals(new SurfaceMaterial(), def.material(), "default material expected Box2D's default material");
        assertEquals(1.0f, def.density(), delta, "default density expected 1");
        assertEquals(new Filter(), def.filter(), "default filter expected Box2D's default filter");
        assertFalse(def.enableCustomFiltering(), "custom filtering expected off by default");
        assertFalse(def.sensor(), "sensor expected off by default");
        assertFalse(def.sensorEventsEnabled(), "sensor events expected off by default");
        assertFalse(def.contactEventsEnabled(), "contact events expected off by default");
        assertFalse(def.hitEventsEnabled(), "hit events expected off by default");
        assertFalse(def.preSolveEventsEnabled(), "pre-solve events expected off by default");
        assertTrue(def.invokeContactCreation(), "contact creation expected on by default");
        assertTrue(def.updateBodyMass(), "body mass update expected on by default");
    }

    @Test
    @DisplayName("ShapeDef fields round-trip through the native struct")
    void testRoundTrip() {
        Object data = new Object();
        ShapeDef def = new ShapeDef()
                .userData(data)
                .density(2.5f)
                .enableCustomFiltering(true)
                .sensor(true)
                .sensorEventsEnabled(true)
                .contactEventsEnabled(true)
                .hitEventsEnabled(true)
                .preSolveEventsEnabled(true)
                .invokeContactCreation(false)
                .updateBodyMass(false);
        assertSame(data, def.userData(), "user data expected to round-trip");
        assertEquals(2.5f, def.density(), delta, "density expected to round-trip");
        assertTrue(def.enableCustomFiltering(), "custom filtering expected to round-trip");
        assertTrue(def.sensor(), "sensor expected to round-trip");
        assertTrue(def.sensorEventsEnabled(), "sensor events expected to round-trip");
        assertTrue(def.contactEventsEnabled(), "contact events expected to round-trip");
        assertTrue(def.hitEventsEnabled(), "hit events expected to round-trip");
        assertTrue(def.preSolveEventsEnabled(), "pre-solve events expected to round-trip");
        assertFalse(def.invokeContactCreation(), "contact creation expected to round-trip");
        assertFalse(def.updateBodyMass(), "body mass update expected to round-trip");
    }

    @Test
    @DisplayName("ShapeDef material() and filter() are views, their setters copy")
    void testNestedStructs() {
        ShapeDef def = new ShapeDef();
        def.material().friction(0.2f).userMaterialId(-1L);
        def.filter().categoryBits(0x4L);
        assertEquals(0.2f, def.material().friction(), delta, "material view expected to write through");
        assertEquals(-1L, def.material().userMaterialId(), "user material expected to keep all 64 bits");
        assertEquals(0x4L, def.filter().categoryBits(), "filter view expected to write through");

        SurfaceMaterial material = new SurfaceMaterial().restitution(0.5f);
        Filter filter = new Filter(0x2L, 0x3L, -1);
        def.material(material).filter(filter);
        material.restitution(0.9f);
        filter.groupIndex(5);
        assertEquals(0.5f, def.material().restitution(), delta, "material setter expected to copy");
        assertEquals(new Filter(0x2L, 0x3L, -1), def.filter(), "filter setter expected to copy");
    }
}
