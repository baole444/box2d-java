package org.box2d.dynamics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CapacityTest {
    @Test
    @DisplayName("Capacity defaults to every count at 0")
    void testDefault() {
        Capacity capacity = new Capacity();
        assertEquals(0, capacity.staticShapeCount(), "static shape count expected 0");
        assertEquals(0, capacity.dynamicShapeCount(), "dynamic shape count expected 0");
        assertEquals(0, capacity.staticBodyCount(), "static body count expected 0");
        assertEquals(0, capacity.dynamicBodyCount(), "dynamic body count expected 0");
        assertEquals(0, capacity.contactCount(), "contact count expected 0");
        assertEquals(0, capacity.arenaByteCount(), "arena byte count expected 0");
    }

    @Test
    @DisplayName("Capacity fields round-trip through the native struct")
    void testRoundTrip() {
        Capacity capacity = new Capacity(1, 2, 3, 4, 5, 6);
        assertEquals(1, capacity.staticShapeCount(), "static shape count expected 1");
        assertEquals(2, capacity.dynamicShapeCount(), "dynamic shape count expected 2");
        assertEquals(3, capacity.staticBodyCount(), "static body count expected 3");
        assertEquals(4, capacity.dynamicBodyCount(), "dynamic body count expected 4");
        assertEquals(5, capacity.contactCount(), "contact count expected 5");
        assertEquals(6, capacity.arenaByteCount(), "arena byte count expected 6");

        capacity.staticShapeCount(10).dynamicShapeCount(20).staticBodyCount(30)
                .dynamicBodyCount(40).contactCount(50).arenaByteCount(60);
        assertEquals(new Capacity(10, 20, 30, 40, 50, 60), capacity, "setters expected to update every count");

        capacity.set(6, 5, 4, 3, 2, 1);
        assertEquals(new Capacity(6, 5, 4, 3, 2, 1), capacity, "set expected to update every count");
    }

    @Test
    @DisplayName("Capacity copy is independent and equal by value")
    void testCopyAndEquality() {
        Capacity capacity = new Capacity(1, 2, 3, 4, 5, 6);
        Capacity copy = capacity.copy();
        assertEquals(capacity, copy, "copy expected equal");
        assertEquals(capacity.hashCode(), copy.hashCode(), "copy expected the same hash code");
        assertEquals(capacity, new Capacity(capacity), "copy constructor expected equal");

        copy.arenaByteCount(100);
        assertEquals(6, capacity.arenaByteCount(), "changing the copy expected not to affect the original");
        assertNotEquals(capacity, copy, "changed copy expected not equal");
    }
}
