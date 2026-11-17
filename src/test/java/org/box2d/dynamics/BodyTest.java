package org.box2d.dynamics;

import org.box2d.collision.MassData;
import org.box2d.math.Rot;
import org.box2d.math.Transform;
import org.box2d.math.Vec2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BodyTest {
    private static final float delta = 0.0001f;
    // Angles go through Box2D's deterministic cosine/sine, accurate to about 0.002
    private static final float angleDelta = 0.002f;
    private World world;

    @BeforeEach
    void setUp() {
        world = new World();
    }

    @AfterEach
    void tearDown() {
        world.destroy();
    }

    private Body dynamicBody() {
        return world.createBody(new BodyDef().type(BodyType.DynamicBody));
    }

    @Test
    @DisplayName("Body is valid on create and invalid after destroy")
    void testLifecycle() {
        Body body = dynamicBody();
        assertTrue(body.isValid(), "new body expected valid");
        assertSame(world, body.world(), "body expected to return its world");
        body.destroy();
        assertFalse(body.isValid(), "destroyed body expected invalid");
        body.destroy();
        assertThrows(IllegalStateException.class, body::position, "use after destroy expected to throw");
    }

    @Test
    @DisplayName("Destroying the world invalidates its bodies")
    void testWorldDestroyInvalidatesBodies() {
        World other = new World();
        Body body = other.createBody(new BodyDef());
        other.destroy();
        assertFalse(body.isValid(), "body expected invalid after its world is destroyed");
        assertThrows(IllegalStateException.class, body::position, "use after world destroy expected to throw");
    }

    @Test
    @DisplayName("A body from a destroyed world stays invalid after a new world reuses the slot")
    void testStaleWorldSlot() {
        World other = new World();
        Body stale = other.createBody(new BodyDef());
        other.destroy();
        World reused = new World();
        try {
            Body fresh = reused.createBody(new BodyDef());
            assertTrue(fresh.isValid(), "body in the new world expected valid");
            assertFalse(stale.isValid(), "body from the destroyed world expected to stay invalid");
            assertNotEquals(stale, fresh, "bodies from different worlds expected not equal");
        } finally {
            reused.destroy();
        }
    }

    @Test
    @DisplayName("Body equality is by full id")
    void testEquality() {
        Body body = dynamicBody();
        Body rebuilt = new Body(world, body.id());
        assertEquals(body, rebuilt, "wrapper rebuilt from the same id expected equal");
        assertEquals(body.hashCode(), rebuilt.hashCode(), "equal bodies expected the same hash code");

        body.destroy();
        Body recycled = dynamicBody();
        assertNotEquals(body, recycled, "body created in a recycled slot expected not equal");
    }

    @Test
    @DisplayName("Body rejects an id from another world")
    void testWrongWorld() {
        World other = new World();
        try {
            Body body = other.createBody(new BodyDef());
            assertThrows(IllegalArgumentException.class, () -> new Body(world, body.id()), "id from another world expected to be rejected");
        } finally {
            other.destroy();
        }
    }

    @Test
    @DisplayName("Body is created from the definition's values")
    void testCreateFromDef() {
        BodyDef def = new BodyDef()
                .type(BodyType.KinematicBody)
                .position(1.0f, 2.0f)
                .rotation((float) Math.PI / 2.0f)
                .linearVelocity(new Vec2(3.0f, 0.0f))
                .angularVelocity(1.5f)
                .bullet(true)
                .motionLocks(new MotionLocks(false, false, true));
        Body body = world.createBody(def);
        assertEquals(BodyType.KinematicBody, body.type(), "type expected kinematic");
        assertEquals(new Vec2(1.0f, 2.0f), body.position(), "position expected (1, 2)");
        assertEquals((float) Math.PI / 2.0f, body.rotation().angle(), angleDelta, "rotation expected 90 degrees");
        assertEquals(3.0f, body.linearVelocity().x(), delta, "linear velocity x expected 3");
        assertEquals(1.5f, body.angularVelocity(), delta, "angular velocity expected 1.5");
        assertTrue(body.bullet(), "bullet expected on");
        assertEquals(new MotionLocks(false, false, true), body.motionLocks(), "rotation expected locked");
    }

    @Test
    @DisplayName("Body state round-trips")
    void testStateRoundTrip() {
        Body body = dynamicBody()
                .type(BodyType.KinematicBody)
                .transform(new Vec2(4.0f, 5.0f), new Rot((float) Math.PI))
                .linearVelocity(new Vec2(1.0f, -1.0f))
                .angularVelocity(2.0f)
                .linearDamping(0.3f)
                .angularDamping(0.4f)
                .gravityScale(0.5f)
                .sleepThreshold(0.2f)
                .sleepingEnabled(false)
                .safetyFactor(0.1f)
                .contactRecyclingEnabled(false)
                .bullet(true);
        assertEquals(BodyType.KinematicBody, body.type(), "type expected kinematic");
        assertEquals(new Vec2(4.0f, 5.0f), body.position(), "position expected (4, 5)");
        assertEquals(-1.0f, body.rotation().c(), delta, "rotation expected 180 degrees");
        Transform transform = body.transform();
        assertEquals(4.0f, transform.x(), delta, "transform x expected 4");
        assertEquals(5.0f, transform.y(), delta, "transform y expected 5");
        assertEquals(new Vec2(1.0f, -1.0f), body.linearVelocity(), "linear velocity expected (1, -1)");
        assertEquals(2.0f, body.angularVelocity(), delta, "angular velocity expected 2");
        assertEquals(0.3f, body.linearDamping(), delta, "linear damping expected 0.3");
        assertEquals(0.4f, body.angularDamping(), delta, "angular damping expected 0.4");
        assertEquals(0.5f, body.gravityScale(), delta, "gravity scale expected 0.5");
        assertEquals(0.2f, body.sleepThreshold(), delta, "sleep threshold expected 0.2");
        assertFalse(body.sleepingEnabled(), "sleep expected disabled");
        assertEquals(0.1f, body.safetyFactor(), delta, "safety factor expected 0.1");
        assertFalse(body.contactRecyclingEnabled(), "contact recycling expected disabled");
        assertTrue(body.bullet(), "bullet expected on");

        body.motionLocks(new MotionLocks(true, true, false));
        assertEquals(new MotionLocks(true, true, false), body.motionLocks(), "translation expected locked");
    }

    @Test
    @DisplayName("Body enable, disable and awake")
    void testEnableAndAwake() {
        Body body = dynamicBody();
        assertTrue(body.enabled(), "body expected enabled");
        body.disable();
        assertFalse(body.enabled(), "body expected disabled");
        body.enable();
        assertTrue(body.enabled(), "body expected enabled again");

        assertTrue(body.awake(), "body expected awake");
        body.awake(false);
        assertFalse(body.awake(), "body expected asleep");
        body.awake(true);
        assertTrue(body.awake(), "body expected awake again");
    }

    @Test
    @DisplayName("Body mass data round-trips")
    void testMassData() {
        Body body = dynamicBody();
        body.massData(new MassData(2.0f, new Vec2(0.5f, 0.0f), 3.0f));
        assertEquals(2.0f, body.mass(), delta, "mass expected 2");
        MassData massData = body.massData();
        assertEquals(2.0f, massData.mass(), delta, "mass data mass expected 2");
        assertEquals(0.5f, massData.center().x(), delta, "mass data centre x expected 0.5");
        assertEquals(0.5f, body.localCenter().x(), delta, "local centre x expected 0.5");
        assertEquals(0.5f, body.worldCenter().x(), delta, "world centre x expected 0.5");
        assertEquals(3.0f, massData.rotationalInertia(), delta, "rotational inertia expected 3");
    }

    @Test
    @DisplayName("Body name round-trips and truncates to 10 UTF-8 bytes")
    void testName() {
        Body body = world.createBody(new BodyDef().name("crate"));
        assertEquals("crate", body.name(), "name expected from the def");
        body.name("barrel");
        assertEquals("barrel", body.name(), "name expected to be replaced");
        body.name(null);
        assertNull(body.name(), "name expected cleared");

        String longName = "a".repeat(20);
        body.name(longName);
        assertEquals("a".repeat(10), body.name(), "long name expected truncated to 10 bytes");

        String split = "b".repeat(9) + "é";
        body.name(split);
        assertEquals("b".repeat(9), body.name(), "multi-byte character expected not to be split");

        Body fromDef = world.createBody(new BodyDef().name(split));
        assertEquals("b".repeat(9), fromDef.name(), "def name expected truncated the same way");
    }

    @Test
    @DisplayName("Body userData set, get, clear and release on destroy")
    void testUserData() {
        Object a = new Object();
        Body body = world.createBody(new BodyDef().userData(a));
        assertSame(a, body.userData(), "user data expected from the def");
        Object b = new Object();
        body.userData(b);
        assertSame(b, body.userData(), "user data expected to be replaced");
        assertEquals(1, world.userDataTable().size(), "replacing expected to release the old slot");
        body.userData(null);
        assertNull(body.userData(), "user data expected cleared");
        assertEquals(0, world.userDataTable().size(), "clearing expected to release the slot");

        body.userData(a);
        body.destroy();
        assertEquals(0, world.userDataTable().size(), "destroying the body expected to release its slot");
    }

    @Test
    @DisplayName("Creating and destroying many bodies keeps the user data table bounded")
    void testTableBounded() {
        for (int i = 0; i < 500; i++) world.createBody(new BodyDef().userData(i)).destroy();
        assertEquals(0, world.userDataTable().size(), "no live slot expected");
        assertEquals(1, world.userDataTable().slotCount(), "one slot expected to be reused");
    }

    @Test
    @DisplayName("Body clearForces removes forces applied before the step")
    void testClearForces() {
        world.gravity(new Vec2(0.0f, 0.0f));
        Body body = dynamicBody();
        body.massData(new MassData(1.0f, new Vec2(), 1.0f));
        assertSame(body, body.applyForceToCenter(new Vec2(100.0f, 0.0f), true).applyTorque(10.0f, true).clearForces(), "clearForces expected to return this");
        world.step(1.0f / 60.0f, 4);
        assertEquals(new Vec2(0.0f, 0.0f), body.linearVelocity(), "cleared force expected no linear velocity");
        assertEquals(0.0f, body.angularVelocity(), "cleared torque expected no angular velocity");

        body.applyForceToCenter(new Vec2(100.0f, 0.0f), true);
        world.step(1.0f / 60.0f, 4);
        assertTrue(body.linearVelocity().x() > 0.0f, "uncleared force expected to accelerate the body");
    }

    @Test
    @DisplayName("Body wakeTouching returns this and is a no-op without shapes")
    void testWakeTouching() {
        Body body = world.createBody(new BodyDef().type(BodyType.StaticBody));
        assertSame(body, body.wakeTouching(), "wakeTouching expected to return this");
        assertTrue(body.isValid(), "body expected still valid");
    }

    @Test
    @DisplayName("Canary: a dynamic body falls under gravity")
    void testFallsUnderGravity() {
        Body body = world.createBody(new BodyDef().type(BodyType.DynamicBody).position(0.0f, 10.0f));
        body.massData(new MassData(1.0f, new Vec2(), 1.0f));
        for (int i = 0; i < 60; i++) world.step(1.0f / 60.0f, 4);
        assertTrue(body.position().y() < 10.0f, "body expected to fall");
        assertTrue(body.linearVelocity().y() < 0.0f, "body expected to move downward");
    }
}
