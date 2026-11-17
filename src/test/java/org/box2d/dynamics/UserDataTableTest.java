package org.box2d.dynamics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDataTableTest {
    @Test
    @DisplayName("UserDataTable register, resolve and release")
    void testRegisterResolveRelease() {
        UserDataTable table = new UserDataTable();
        Object a = new Object();
        long handle = table.register(a);
        assertNotEquals(0L, handle, "handle expected non-zero");
        assertSame(a, table.resolve(handle), "handle expected to resolve to the object");
        assertEquals(1, table.size(), "one live slot expected");

        table.release(handle);
        assertNull(table.resolve(handle), "released handle expected to resolve to null");
        assertEquals(0, table.size(), "no live slot expected");
        table.release(handle);
        assertEquals(0, table.size(), "releasing twice expected to be ignored");
    }

    @Test
    @DisplayName("UserDataTable reuses released slots with a new generation")
    void testSlotReuse() {
        UserDataTable table = new UserDataTable();
        long first = table.register("a");
        table.release(first);
        long second = table.register("b");
        assertEquals((int) first, (int) second, "slot expected to be reused");
        assertNotEquals(first, second, "generation expected to change");
        assertNull(table.resolve(first), "stale handle expected to resolve to null");
        assertEquals("b", table.resolve(second), "new handle expected to resolve to the new object");
    }

    @Test
    @DisplayName("UserDataTable grows past its initial capacity")
    void testGrowth() {
        UserDataTable table = new UserDataTable();
        long[] handles = new long[100];
        for (int i = 0; i < handles.length; i++) handles[i] = table.register(i);
        for (int i = 0; i < handles.length; i++) assertEquals(i, table.resolve(handles[i]), "slot " + i);
    }

    @Test
    @DisplayName("UserDataTable clear invalidates every handle")
    void testClear() {
        UserDataTable table = new UserDataTable();
        long handle = table.register("a");
        table.clear();
        assertNull(table.resolve(handle), "handle expected to resolve to null after clear");
        assertEquals(0, table.size(), "no live slot expected after clear");
        long next = table.register("b");
        assertNull(table.resolve(handle), "old handle expected to stay null after reuse");
        assertEquals("b", table.resolve(next), "new handle expected to resolve");
    }
}
