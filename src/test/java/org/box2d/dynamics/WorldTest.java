package org.box2d.dynamics;

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
