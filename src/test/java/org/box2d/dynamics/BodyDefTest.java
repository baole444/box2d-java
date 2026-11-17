package org.box2d.dynamics;

import org.box2d.math.Rot;
import org.box2d.math.Vec2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BodyDefTest {
    private static final float delta = 0.0001f;
    // Angles go through Box2D's deterministic cosine/sine, accurate to about 0.002
    private static final float angleDelta = 0.002f;

    @Test
    @DisplayName("BodyDef default seeds the Box2D defaults")
    void testDefaults() {
        BodyDef def = new BodyDef();
        assertEquals(BodyType.StaticBody, def.type(), "default type expected static");
        assertEquals(0.0f, def.position().x(), delta, "default position x expected 0");
        assertEquals(0.0f, def.position().y(), delta, "default position y expected 0");
        assertEquals(1.0f, def.rotation().c(), delta, "default rotation expected identity (c)");
        assertEquals(0.0f, def.rotation().s(), delta, "default rotation expected identity (s)");
        assertEquals(0.0f, def.linearVelocity().x(), delta, "default linear velocity x expected 0");
        assertEquals(0.0f, def.linearVelocity().y(), delta, "default linear velocity y expected 0");
        assertEquals(0.0f, def.angularVelocity(), delta, "default angular velocity expected 0");
        assertEquals(0.0f, def.linearDamping(), delta, "default linear damping expected 0");
        assertEquals(0.0f, def.angularDamping(), delta, "default angular damping expected 0");
        assertEquals(1.0f, def.gravityScale(), delta, "default gravity scale expected 1");
        assertEquals(0.05f, def.sleepThreshold(), delta, "default sleep threshold expected 0.05");
        assertNull(def.name(), "default name expected null");
        assertNull(def.userData(), "default user data expected null");
        assertTrue(def.sleepingEnabled(), "sleep expected enabled by default");
        assertTrue(def.awake(), "body expected awake by default");
        assertEquals(new MotionLocks(), def.motionLocks(), "motion locks expected all released by default");
        assertEquals(0.5f, def.safetyFactor(), delta, "default safety factor expected 0.5");
        assertTrue(def.contactRecyclingEnabled(), "contact recycling expected enabled by default");
        assertFalse(def.bullet(), "bullet expected off by default");
        assertTrue(def.enabled(), "body expected enabled by default");
        assertFalse(def.allowFastRotation(), "fast rotation expected off by default");
    }

    @Test
    @DisplayName("BodyDef fields round-trip through the native struct")
    void testRoundTrip() {
        Object data = new Object();
        BodyDef def = new BodyDef()
                .type(BodyType.DynamicBody)
                .position(1.0f, 2.0f)
                .rotation(new Rot((float) Math.PI / 2.0f))
                .linearVelocity(new Vec2(3.0f, 4.0f))
                .angularVelocity(5.0f)
                .linearDamping(0.5f)
                .angularDamping(0.25f)
                .gravityScale(2.0f)
                .sleepThreshold(0.1f)
                .name("crate")
                .userData(data)
                .sleepingEnabled(false)
                .awake(false)
                .motionLocks(new MotionLocks(false, false, true))
                .safetyFactor(0.25f)
                .contactRecyclingEnabled(false)
                .bullet(true)
                .enabled(false)
                .allowFastRotation(true);
        assertEquals(BodyType.DynamicBody, def.type(), "type expected dynamic");
        assertEquals(new Vec2(1.0f, 2.0f), def.position(), "position expected (1, 2)");
        assertEquals(0.0f, def.rotation().c(), delta, "rotation expected 90 degrees (c)");
        assertEquals(1.0f, def.rotation().s(), delta, "rotation expected 90 degrees (s)");
        assertEquals(new Vec2(3.0f, 4.0f), def.linearVelocity(), "linear velocity expected (3, 4)");
        assertEquals(5.0f, def.angularVelocity(), delta, "angular velocity expected 5");
        assertEquals(0.5f, def.linearDamping(), delta, "linear damping expected 0.5");
        assertEquals(0.25f, def.angularDamping(), delta, "angular damping expected 0.25");
        assertEquals(2.0f, def.gravityScale(), delta, "gravity scale expected 2");
        assertEquals(0.1f, def.sleepThreshold(), delta, "sleep threshold expected 0.1");
        assertEquals("crate", def.name(), "name expected crate");
        assertSame(data, def.userData(), "user data expected the same object");
        assertFalse(def.sleepingEnabled(), "sleep expected disabled");
        assertFalse(def.awake(), "body expected asleep");
        assertEquals(new MotionLocks(false, false, true), def.motionLocks(), "rotation expected locked");
        assertEquals(0.25f, def.safetyFactor(), delta, "safety factor expected 0.25");
        assertFalse(def.contactRecyclingEnabled(), "contact recycling expected disabled");
        def.motionLocks().linearX(true);
        assertTrue(def.motionLocks().linearX(), "motion locks expected a view that writes through to the def");
        assertTrue(def.bullet(), "bullet expected on");
        assertFalse(def.enabled(), "body expected disabled");
        assertTrue(def.allowFastRotation(), "fast rotation expected allowed");

        def.rotation((float) Math.PI / 3.0f);
        assertEquals((float) Math.PI / 3.0f, def.rotation().angle(), angleDelta, "rotation expected from the angle");
    }

    @Test
    @DisplayName("BodyDef can be reused: each body gets the def's values at its creation")
    void testReuse() {
        Object first = new Object();
        BodyDef def = new BodyDef().name("first").userData(first);
        World world = new World();
        try {
            Body a = world.createBody(def);
            def.name(null).userData(null);
            Body b = world.createBody(def);
            assertEquals("first", a.name(), "first body expected the first name");
            assertSame(first, a.userData(), "first body expected the first user data");
            assertNull(b.name(), "second body expected no name");
            assertNull(b.userData(), "second body expected no user data");
        } finally {
            world.destroy();
        }
    }
}
