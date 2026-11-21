package org.box2d.dynamics;

import org.box2d.collision.AABB;
import org.box2d.collision.MassData;
import org.box2d.collision.Polygon;
import org.box2d.math.Vec2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorldTest {
    private static final float delta = 0.0001f;
    private World world;

    @BeforeEach
    void setUp() {
        world = new World();
    }

    @AfterEach
    void tearDown() {
        world.destroy();
    }

    @Test
    @DisplayName("World is valid on create and invalid after destroy")
    void testLifecycle() {
        World other = new World();
        assertTrue(other.isValid(), "new world expected valid");
        other.destroy();
        assertFalse(other.isValid(), "destroyed world expected invalid");
        other.destroy();
        assertFalse(other.isValid(), "destroying twice expected to be ignored");
        assertThrows(IllegalStateException.class, other::gravity, "use after destroy expected to throw");
        assertThrows(IllegalStateException.class, () -> other.step(1.0f / 60.0f, 4), "step after destroy expected to throw");
    }

    @Test
    @DisplayName("World is created from the definition's values")
    void testCreateFromDef() {
        Object data = new Object();
        WorldDef def = new WorldDef().gravity(1.0f, -2.0f).hitEventThreshold(3.0f).sleepingEnabled(false).userData(data);
        World other = new World(def);
        try {
            assertEquals(new Vec2(1.0f, -2.0f), other.gravity(), "gravity expected from the def");
            assertEquals(3.0f, other.hitEventThreshold(), delta, "hit event threshold expected from the def");
            assertFalse(other.sleepingEnabled(), "sleeping expected disabled from the def");
            assertSame(data, other.userData(), "user data expected from the def");
        } finally {
            other.destroy();
        }
    }

    @Test
    @DisplayName("World settings round-trip")
    void testSettingsRoundTrip() {
        world.gravity(new Vec2(0.0f, -9.8f))
                .sleepingEnabled(false)
                .continuousEnabled(false)
                .warmStartingEnabled(false)
                .restitutionThreshold(2.0f)
                .hitEventThreshold(3.0f)
                .maxLinearSpeed(50.0f)
                .setContactTuning(20.0f, 5.0f, 2.0f)
                .contactRecycleDistance(0.0f);
        assertEquals(new Vec2(0.0f, -9.8f), world.gravity(), "gravity expected (0, -9.8)");
        assertFalse(world.sleepingEnabled(), "sleeping expected disabled");
        assertFalse(world.continuousEnabled(), "continuous expected disabled");
        assertFalse(world.warmStartingEnabled(), "warm starting expected disabled");
        assertEquals(2.0f, world.restitutionThreshold(), delta, "restitution threshold expected 2");
        assertEquals(3.0f, world.hitEventThreshold(), delta, "hit event threshold expected 3");
        assertEquals(50.0f, world.maxLinearSpeed(), delta, "max linear speed expected 50");
        assertEquals(20.0f, world.contactHertz(), delta, "contact hertz expected 20");
        assertEquals(5.0f, world.contactDampingRatio(), delta, "contact damping ratio expected 5");
        assertEquals(2.0f, world.contactSpeed(), delta, "contact speed expected 2");
        assertEquals(0.0f, world.contactRecycleDistance(), delta, "contact recycle distance expected 0");
    }

    @Test
    @DisplayName("World restitution iterations round-trip, clamp to the maximum and reject negatives")
    void testRestitutionIterations() {
        assertEquals(2, world.restitutionIterations(), "default restitution iterations expected 2");
        world.restitutionIterations(0);
        assertEquals(0, world.restitutionIterations(), "restitution iterations expected 0");
        world.restitutionIterations(5);
        assertEquals(5, world.restitutionIterations(), "restitution iterations expected 5");
        world.restitutionIterations(-1);
        assertEquals(5, world.restitutionIterations(), "negative restitution iterations expected rejected");
        world.restitutionIterations(World.MaxRestitutionIterations + 10);
        assertEquals(World.MaxRestitutionIterations, world.restitutionIterations(), "restitution iterations expected clamped to the maximum");

        World other = new World(new WorldDef().restitutionIterations(100));
        try {
            assertEquals(World.MaxRestitutionIterations, other.restitutionIterations(), "def value expected clamped to the maximum");
        } finally {
            other.destroy();
        }
    }

    @Test
    @DisplayName("World created from an invalid definition is invalid")
    void testInvalidDef() {
        assertFalse(new World(new WorldDef().restitutionIterations(-1)).isValid(), "negative restitution iterations expected invalid world");
        assertFalse(new World(new WorldDef().gravity(Float.NaN, 0.0f)).isValid(), "non-finite gravity expected invalid world");
        assertFalse(new World(new WorldDef().maxLinearSpeed(0.0f)).isValid(), "non-positive max linear speed expected invalid world");
    }

    @Test
    @DisplayName("World max capacity records the peak counts and feeds back into a definition")
    void testMaxCapacity() {
        assertEquals(new Capacity(), world.maxCapacity(), "fresh world expected zero max capacity");
        world.createBody(new BodyDef().type(BodyType.StaticBody));
        for (int i = 0; i < 3; i++) world.createBody(new BodyDef().type(BodyType.DynamicBody));
        world.step(1.0f / 60.0f, 4);
        Capacity capacity = world.maxCapacity();
        assertEquals(1, capacity.staticBodyCount(), "static body count expected 1");
        assertEquals(3, capacity.dynamicBodyCount(), "dynamic body count expected 3");

        World other = new World(new WorldDef().capacity(capacity));
        try {
            assertTrue(other.isValid(), "world from a recorded capacity expected valid");
        } finally {
            other.destroy();
        }
    }

    @Test
    @DisplayName("World bounds contain its shapes")
    void testBoundsWithShapes() {
        Body ground = world.createBody(new BodyDef().position(5.0f, 0.0f));
        ground.createPolygonShape(new ShapeDef(), Polygon.box(2.0f, 1.0f));
        AABB bounds = world.bounds();
        assertTrue(bounds.lowerBound().x() <= 3.0f && bounds.lowerBound().y() <= -1.0f, "world bounds expected to reach the shape's lower corner");
        assertTrue(bounds.upperBound().x() >= 7.0f && bounds.upperBound().y() >= 1.0f, "world bounds expected to reach the shape's upper corner");
    }

    @Test
    @DisplayName("World bounds of a world without shapes is the zero box")
    void testEmptyBounds() {
        world.createBody(new BodyDef().type(BodyType.DynamicBody).position(5.0f, 5.0f));
        assertEquals(new AABB(), world.bounds(), "world without shapes expected the zero box");
    }

    @Test
    @DisplayName("World state hash matches for identical worlds and changes when they diverge")
    void testStateHash() {
        World other = new World();
        try {
            Body a = world.createBody(new BodyDef().type(BodyType.DynamicBody).position(1.0f, 2.0f));
            Body b = other.createBody(new BodyDef().type(BodyType.DynamicBody).position(1.0f, 2.0f));
            a.massData(new MassData(1.0f, new Vec2(), 1.0f));
            b.massData(new MassData(1.0f, new Vec2(), 1.0f));
            for (int i = 0; i < 10; i++) {
                world.step(1.0f / 60.0f, 4);
                other.step(1.0f / 60.0f, 4);
            }
            assertEquals(world.stateHash(), other.stateHash(), "identical worlds expected the same hash");

            long before = other.stateHash();
            b.applyLinearImpulseToCenter(new Vec2(1.0f, 0.0f), true);
            world.step(1.0f / 60.0f, 4);
            other.step(1.0f / 60.0f, 4);
            assertNotEquals(world.stateHash(), other.stateHash(), "diverged worlds expected different hashes");
            assertNotEquals(before, other.stateHash(), "stepped world expected a new hash");
        } finally {
            other.destroy();
        }
    }

    @Test
    @DisplayName("World gravity() returns a copy")
    void testGravityCopy() {
        Vec2 gravity = world.gravity();
        gravity.set(5.0f, 5.0f);
        assertEquals(-10.0f, world.gravity().y(), delta, "changing the returned vector expected not to affect the world");
    }

    @Test
    @DisplayName("World isLocked is false outside step")
    void testIsLocked() {
        assertFalse(world.isLocked(), "world expected unlocked before step");
        world.step(1.0f / 60.0f, 4);
        assertFalse(world.isLocked(), "world expected unlocked after step");
    }

    @Test
    @DisplayName("World userData set, get and clear")
    void testUserData() {
        assertNull(world.userData(), "user data expected null by default");
        Object a = new Object();
        world.userData(a);
        assertSame(a, world.userData(), "user data expected the same object");
        Object b = new Object();
        world.userData(b);
        assertSame(b, world.userData(), "user data expected to be replaced");
        assertEquals(1, world.userDataTable().size(), "replacing expected to release the old slot");
        world.userData(null);
        assertNull(world.userData(), "user data expected cleared");
        assertEquals(0, world.userDataTable().size(), "clearing expected to release the slot");
    }

    @Test
    @DisplayName("World destroy drops the user data table")
    void testDestroyDropsTable() {
        World other = new World(new WorldDef().userData("world"));
        other.createBody(new BodyDef().userData("body"));
        assertEquals(2, other.userDataTable().size(), "world and body user data expected registered");
        other.destroy();
        assertEquals(0, other.userDataTable().size(), "table expected empty after destroy");
    }
}
