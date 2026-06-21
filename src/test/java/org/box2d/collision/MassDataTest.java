package org.box2d.collision;

import org.box2d.math.Vec2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MassDataTest {
    @Test
    @DisplayName("MassData default constructor is zeroed")
    void testDefaultConstructor() {
        MassData data = new MassData();
        assertEquals(0.0f, data.mass(), "mass expected 0.0");
        assertEquals(0.0f, data.center().x(), "center x expected 0.0");
        assertEquals(0.0f, data.center().y(), "center y expected 0.0");
        assertEquals(0.0f, data.rotationalInertia(), "rotational inertia expected 0.0");
    }

    @Test
    @DisplayName("MassData constructor create mass data with values")
    void testConstructorRoundTrip() {
        MassData data = new MassData(5.0f, new Vec2(1.0f, 2.0f), 7.0f);
        assertEquals(5.0f, data.mass(), "mass expected 5.0");
        assertEquals(new Vec2(1.0f, 2.0f), data.center(), "center expected (1, 2)");
        assertEquals(7.0f, data.rotationalInertia(), "rotational inertia expected 7.0");

        MassData fromFloats = new MassData(2.0f, 3.0f, 4.0f, 8.0f);
        assertEquals(new Vec2(3.0f, 4.0f), fromFloats.center(), "center expected (3, 4)");
    }

    @Test
    @DisplayName("MassData center accessor writes through to the segment")
    void testCenterViewWritesThrough() {
        MassData data = new MassData(1.0f, new Vec2(), 1.0f);
        data.center().set(9.0f, 8.0f);
        assertEquals(9.0f, data.center().x(), "center x expected mutation visible");
        assertEquals(8.0f, data.center().y(), "center y expected mutation visible");
    }

    @Test
    @DisplayName("MassData builder pattern")
    void testBuilder() {
        MassData data = new MassData().mass(3.0f).center(1.0f, 1.0f).rotationalInertia(2.0f);
        assertEquals(3.0f, data.mass(), "mass expected 3.0");
        assertEquals(new Vec2(1.0f, 1.0f), data.center(), "center expected (1, 1)");
        assertEquals(2.0f, data.rotationalInertia(), "rotational inertia expected 2.0");
    }

    @Test
    @DisplayName("MassData copy independent")
    void testCopyIndependent() {
        MassData og = new MassData(1.0f, new Vec2(1.0f, 1.0f), 1.0f);
        MassData copy = og.copy();
        assertEquals(og, copy, "copy expected equal to original");

        copy.mass(99.0f);
        copy.center().set(5.0f, 5.0f);
        assertEquals(1.0f, og.mass(), "original mass expected unchanged");
        assertEquals(1.0f, og.center().x(), "original center expected unchanged");
    }

    @Test
    @DisplayName("MassData equals and hashCode")
    void testEqualsHashCode() {
        MassData a = new MassData(1.0f, new Vec2(1.0f, 2.0f), 3.0f);
        MassData b = new MassData(1.0f, new Vec2(1.0f, 2.0f), 3.0f);
        MassData c = new MassData(2.0f, new Vec2(1.0f, 2.0f), 3.0f);

        assertEquals(a, b, "equal mass data expected equal");
        assertEquals(a.hashCode(), b.hashCode(), "equal mass data expected same hash code");
        assertNotEquals(a, c, "different mass data expected not equal");
        assertNotEquals(null, a, "mass data and null expected not equal");
    }

    @Test
    @DisplayName("MassData memory segment access")
    void testMemSeg() {
        assertNotNull(new MassData().segment(), "segment expected not null");
    }
}
