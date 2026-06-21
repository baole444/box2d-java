package org.box2d.dynamics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FilterTest {
    @Test
    @DisplayName("Filter default seeds the Box2D defaults")
    void testDefaultConstructor() {
        Filter filter = new Filter();
        assertEquals(1L, filter.categoryBits(), "default category bits expected 1");
        assertEquals(-1L, filter.maskBits(), "default mask bits expected all set");
        assertEquals(0, filter.groupIndex(), "default group index expected 0");
    }

    @Test
    @DisplayName("Filter constructor create filter with values")
    void testConstructorRoundTrip() {
        Filter filter = new Filter(0x2L, 0x6L, -3);
        assertEquals(0x2L, filter.categoryBits(), "category bits expected 0x2");
        assertEquals(0x6L, filter.maskBits(), "mask bits expected 0x6");
        assertEquals(-3, filter.groupIndex(), "group index expected -3");
    }

    @Test
    @DisplayName("Filter set and builder pattern")
    void testSet() {
        Filter filter = new Filter().set(0x4L, 0x8L, 5);
        assertEquals(0x4L, filter.categoryBits(), "category bits expected 0x4");
        assertEquals(0x8L, filter.maskBits(), "mask bits expected 0x8");
        assertEquals(5, filter.groupIndex(), "group index expected 5");

        filter.categoryBits(0x10L).maskBits(0x20L).groupIndex(7);
        assertEquals(0x10L, filter.categoryBits(), "category bits expected 0x10");
        assertEquals(0x20L, filter.maskBits(), "mask bits expected 0x20");
        assertEquals(7, filter.groupIndex(), "group index expected 7");
    }

    @Test
    @DisplayName("Filter copy independent")
    void testCopyIndependent() {
        Filter og = new Filter(0x2L, 0x6L, 1);
        Filter copy = og.copy();
        assertEquals(og, copy, "copy expected equal to original");

        copy.categoryBits(0xFFL);
        assertEquals(0x2L, og.categoryBits(), "original category bits expected unchanged");
    }

    @Test
    @DisplayName("Filter equals and hashCode")
    void testEqualsHashCode() {
        Filter a = new Filter(0x2L, 0x6L, 1);
        Filter b = new Filter(0x2L, 0x6L, 1);
        Filter c = new Filter(0x2L, 0x6L, 2);

        assertEquals(a, b, "equal filters expected equal");
        assertEquals(a.hashCode(), b.hashCode(), "equal filters expected same hash code");
        assertNotEquals(a, c, "different filters expected not equal");
        assertNotEquals(null, a, "filter and null expected not equal");
    }

    @Test
    @DisplayName("Filter memory segment access")
    void testMemSeg() {
        assertNotNull(new Filter().segment(), "segment expected not null");
    }
}
