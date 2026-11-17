package org.box2d.dynamics;

import java.lang.foreign.MemorySegment;
import java.util.Arrays;

/**
 * Per {@link World} table that maps the handles stored in Box2D's native {@code void* userData} slots to the Java objects they represented.
 * <p>
 * A handle packs the {@code slot index + 1} in its low 32 bits and the slot generation in its high 32 bits.
 * This is complied with Box2D, where {@code 0} ({@code NULL}) represent no user data.
 * Releasing a slot increases its generation, so a stale handle resolves to {@code null}.
 * <p>
 * Not thread-safe. Box2D run single-threaded with the default world definition.
 */
final class UserDataTable {
    private static final int InitialCapacity = 16;

    private Object[] values = new Object[InitialCapacity];
    private int[] generations = new int[InitialCapacity];
    private int[] freeSlots = new int[InitialCapacity];
    private int freeCount = 0;
    private int slotCount = 0;

    /**
     * Store an object in a new slot.
     * @param value the object to store, can be null
     * @return the encoded handle, {@code 0} when the value is null
     */
    long register(Object value) {
        if (value == null) return 0L;
        int slot = freeCount > 0 ? freeSlots[--freeCount] : newSlot();
        values[slot] = value;
        return encode(slot, generations[slot]);
    }

    /**
     * Get the object a handle represents.
     * @param handle the encoded handle
     * @return the stored object, or null if the handle is {@code 0}, released or unknow
     */
    Object resolve(long handle) {
        int slot = slotOf(handle);
        if (slot < 0) return null;
        return values[slot];
    }

    /**
     * Release the slot a handle represents, unknown or stale handles are ignored.
     * @param handle the encoded handle
     */
    void release(long handle) {
        int slot = slotOf(handle);
        if (slot < 0) return;
        values[slot] = null;
        generations[slot]++;
        freeSlots[freeCount++] = slot;
    }

    /**
     * Drop all stored object.
     * Handles given out before will resolve to null afterward.
     */
    void clear() {
        for (int slot = 0; slot < slotCount; slot++) {
            if (values[slot] == null) continue;
            values[slot] = null;
            generations[slot]++;
        }
        freeCount = 0;
        slotCount = 0;
    }

    /**
     * Get the number of currently occupied slots.
     * @return the live slot count
     */
    int size() {
        return slotCount - freeCount;
    }

    /**
     * Get the total amount of slots ever allocated.
     * @return the slot count, live or free
     */
    int slotCount() {
        return slotCount;
    }

    /**
     * Read the handle out of a native user data pointer.
     * @param pointer the native pointer
     * @return the encoded handle
     */
    static long handleOf(MemorySegment pointer) {
        return pointer == null ? 0L : pointer.address();
    }

    /**
     * Turn a handle into a native user data pointer.
     * @param handle the encoded handle
     * @return the pointer segment, or {@link MemorySegment#NULL} for {@code 0}
     */
    static MemorySegment pointerOf(long handle) {
        return handle == 0L ? MemorySegment.NULL : MemorySegment.ofAddress(handle);
    }

    /**
     * Allocate a fresh slot, increase size of all 3 arrays.
     * @return the increased slot count
     * @apiNote The free list never outgrows the slots
     */
    private int newSlot() {
        if (slotCount != values.length) return slotCount++;
        int capacity = values.length * 2;
        values = Arrays.copyOf(values, capacity);
        generations = Arrays.copyOf(generations, capacity);
        freeSlots = Arrays.copyOf(freeSlots, capacity);
        return slotCount++;
    }

    private int slotOf(long handle) {
        if (handle == 0L) return -1;
        int slot = (int) handle - 1;
        if (slot < 0 || slot >= slotCount) return -1;
        if (generations[slot] != (int) (handle >>> 32) || values[slot] == null) return -1;
        return slot;
    }

    private static long encode(int slot, int generation) {
        return ((long) generation << 32) | ((slot + 1) & 0xFFFFFFFFL);
    }
}
