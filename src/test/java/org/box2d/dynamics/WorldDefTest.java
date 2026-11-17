package org.box2d.dynamics;

import org.box2d.math.Vec2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorldDefTest {
    private static final float delta = 0.0001f;

    @Test
    @DisplayName("WorldDef default seeds the Box2D defaults")
    void testDefaults() {
        WorldDef def = new WorldDef();
        assertEquals(0.0f, def.gravity().x(), delta, "default gravity x expected 0");
        assertEquals(-10.0f, def.gravity().y(), delta, "default gravity y expected -10");
        assertEquals(1.0f, def.restitutionThreshold(), delta, "default restitution threshold expected 1");
        assertEquals(1.0f, def.hitEventThreshold(), delta, "default hit event threshold expected 1");
        assertEquals(30.0f, def.contactHertz(), delta, "default contact hertz expected 30");
        assertEquals(10.0f, def.contactDampingRatio(), delta, "default contact damping ratio expected 10");
        assertEquals(3.0f, def.maxContactPushSpeed(), delta, "default max contact push speed expected 3");
        assertEquals(400.0f, def.maximumLinearSpeed(), delta, "default maximum linear speed expected 400");
        assertTrue(def.sleepingEnabled(), "sleeping expected enabled by default");
        assertTrue(def.continuousEnabled(), "continuous expected enabled by default");
        assertNull(def.userData(), "user data expected null by default");
    }

    @Test
    @DisplayName("WorldDef fields round-trip through the native struct")
    void testRoundTrip() {
        Object data = new Object();
        WorldDef def = new WorldDef()
                .gravity(1.0f, 2.0f)
                .restitutionThreshold(3.0f)
                .hitEventThreshold(4.0f)
                .contactHertz(5.0f)
                .contactDampingRatio(6.0f)
                .maxContactPushSpeed(7.0f)
                .maximumLinearSpeed(8.0f)
                .sleepingEnabled(false)
                .continuousEnabled(false)
                .userData(data);
        assertEquals(new Vec2(1.0f, 2.0f), def.gravity(), "gravity expected (1, 2)");
        assertEquals(3.0f, def.restitutionThreshold(), delta, "restitution threshold expected 3");
        assertEquals(4.0f, def.hitEventThreshold(), delta, "hit event threshold expected 4");
        assertEquals(5.0f, def.contactHertz(), delta, "contact hertz expected 5");
        assertEquals(6.0f, def.contactDampingRatio(), delta, "contact damping ratio expected 6");
        assertEquals(7.0f, def.maxContactPushSpeed(), delta, "max contact push speed expected 7");
        assertEquals(8.0f, def.maximumLinearSpeed(), delta, "maximum linear speed expected 8");
        assertFalse(def.sleepingEnabled(), "sleeping expected disabled");
        assertFalse(def.continuousEnabled(), "continuous expected disabled");
        assertSame(data, def.userData(), "user data expected the same object");

        def.gravity(new Vec2(-3.0f, 4.0f));
        assertEquals(new Vec2(-3.0f, 4.0f), def.gravity(), "gravity expected (-3, 4)");
    }
}
