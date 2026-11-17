package org.box2d.dynamics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MotionLocksTest {
    @Test
    @DisplayName("MotionLocks default to every lock released")
    void testDefault() {
        MotionLocks locks = new MotionLocks();
        assertFalse(locks.linearX(), "linearX expected released");
        assertFalse(locks.linearY(), "linearY expected released");
        assertFalse(locks.angularZ(), "angularZ expected released");
    }

    @Test
    @DisplayName("MotionLocks fields round-trip through the native struct")
    void testRoundTrip() {
        MotionLocks locks = new MotionLocks(true, false, true);
        assertTrue(locks.linearX(), "linearX expected locked");
        assertFalse(locks.linearY(), "linearY expected released");
        assertTrue(locks.angularZ(), "angularZ expected locked");

        locks.linearX(false).linearY(true).angularZ(false);
        assertFalse(locks.linearX(), "linearX expected released");
        assertTrue(locks.linearY(), "linearY expected locked");
        assertFalse(locks.angularZ(), "angularZ expected released");
    }

    @Test
    @DisplayName("MotionLocks copy is independent and equal by value")
    void testCopyAndEquality() {
        MotionLocks locks = new MotionLocks(false, false, true);
        MotionLocks copy = locks.copy();
        assertEquals(locks, copy, "copy expected equal");
        assertEquals(locks.hashCode(), copy.hashCode(), "copy expected the same hash code");

        copy.angularZ(false);
        assertTrue(locks.angularZ(), "changing the copy expected not to affect the original");
        assertNotEquals(locks, copy, "changed copy expected not equal");
    }
}
