package org.box2d.dynamics;

import org.box2d.internal.b2Capacity;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * Optional world capacities that can be used to avoid run-time allocations.
 * <p>
 * Set them on {@link WorldDef#capacity()} to preallocate a world's storage. A count of 0 leaves Box2D's minimum.
 * {@link World#maxCapacity()} reports the largest counts a world has reached, which can be fed back into a definition.
 * <p>
 * Wrapper for native {@link b2Capacity}.
 */
public final class Capacity {
    private final MemorySegment segment;

    /**
     * Create a new {@link Capacity} with every count at 0.
     */
    public Capacity() {
        segment = Arena.ofAuto().allocate(b2Capacity.layout());
    }

    /**
     * Create a new {@link Capacity} with the given counts.
     * @param staticShapeCount the number of expected static shapes
     * @param dynamicShapeCount the number of expected dynamic and kinematic shapes
     * @param staticBodyCount the number of expected static bodies
     * @param dynamicBodyCount the number of expected dynamic and kinematic bodies
     * @param contactCount the number of expected contacts
     * @param arenaByteCount the arena allocator space to reserve for temporary allocations, in bytes
     */
    public Capacity(int staticShapeCount, int dynamicShapeCount, int staticBodyCount, int dynamicBodyCount, int contactCount, int arenaByteCount) {
        segment = Arena.ofAuto().allocate(b2Capacity.layout());
        set(staticShapeCount, dynamicShapeCount, staticBodyCount, dynamicBodyCount, contactCount, arenaByteCount);
    }

    /**
     * Create a new {@link Capacity} and initialise it to the counts of the given one.
     * @param other the {@link Capacity} to set the value from
     */
    public Capacity(Capacity other) {
        this(other.staticShapeCount(), other.dynamicShapeCount(), other.staticBodyCount(), other.dynamicBodyCount(), other.contactCount(), other.arenaByteCount());
    }

    /**
     * Wrap an existing {@link b2Capacity} memory segment.
     * The segment is not owned by this {@link Capacity} instance.
     * @param segment the memory segment to wrap
     */
    public Capacity(MemorySegment segment) {
        this.segment = segment;
    }

    /**
     * Create a new {@link Capacity} with its values initialised to these counts.
     * @return a new {@link Capacity}
     */
    public Capacity copy() {
        return new Capacity(this);
    }

    /**
     * Get the number of expected static shapes.
     * @return the static shape count
     */
    public int staticShapeCount() {
        return b2Capacity.staticShapeCount(segment);
    }

    /**
     * Set the number of expected static shapes.
     * @param count the static shape count
     * @return this
     */
    public Capacity staticShapeCount(int count) {
        b2Capacity.staticShapeCount(segment, count);
        return this;
    }

    /**
     * Get the number of expected dynamic and kinematic shapes.
     * @return the dynamic shape count
     */
    public int dynamicShapeCount() {
        return b2Capacity.dynamicShapeCount(segment);
    }

    /**
     * Set the number of expected dynamic and kinematic shapes.
     * @param count the dynamic shape count
     * @return this
     */
    public Capacity dynamicShapeCount(int count) {
        b2Capacity.dynamicShapeCount(segment, count);
        return this;
    }

    /**
     * Get the number of expected static bodies.
     * @return the static body count
     */
    public int staticBodyCount() {
        return b2Capacity.staticBodyCount(segment);
    }

    /**
     * Set the number of expected static bodies.
     * @param count the static body count
     * @return this
     */
    public Capacity staticBodyCount(int count) {
        b2Capacity.staticBodyCount(segment, count);
        return this;
    }

    /**
     * Get the number of expected dynamic and kinematic bodies.
     * @return the dynamic body count
     */
    public int dynamicBodyCount() {
        return b2Capacity.dynamicBodyCount(segment);
    }

    /**
     * Set the number of expected dynamic and kinematic bodies.
     * @param count the dynamic body count
     * @return this
     */
    public Capacity dynamicBodyCount(int count) {
        b2Capacity.dynamicBodyCount(segment, count);
        return this;
    }

    /**
     * Get the number of expected contacts.
     * @return the contact count
     */
    public int contactCount() {
        return b2Capacity.contactCount(segment);
    }

    /**
     * Set the number of expected contacts.
     * @param count the contact count
     * @return this
     */
    public Capacity contactCount(int count) {
        b2Capacity.contactCount(segment, count);
        return this;
    }

    /**
     * Get the arena allocator space to reserve, used for temporary allocations.
     * @return the arena size in bytes
     */
    public int arenaByteCount() {
        return b2Capacity.arenaByteCount(segment);
    }

    /**
     * Set the arena allocator space to reserve, used for temporary allocations.
     * @param byteCount the arena size in bytes
     * @return this
     */
    public Capacity arenaByteCount(int byteCount) {
        b2Capacity.arenaByteCount(segment, byteCount);
        return this;
    }

    /**
     * Set every count to the given values.
     * @param staticShapeCount the number of expected static shapes
     * @param dynamicShapeCount the number of expected dynamic and kinematic shapes
     * @param staticBodyCount the number of expected static bodies
     * @param dynamicBodyCount the number of expected dynamic and kinematic bodies
     * @param contactCount the number of expected contacts
     * @param arenaByteCount the arena allocator space to reserve for temporary allocations, in bytes
     * @return this
     */
    public Capacity set(int staticShapeCount, int dynamicShapeCount, int staticBodyCount, int dynamicBodyCount, int contactCount, int arenaByteCount) {
        staticShapeCount(staticShapeCount);
        dynamicShapeCount(dynamicShapeCount);
        staticBodyCount(staticBodyCount);
        dynamicBodyCount(dynamicBodyCount);
        contactCount(contactCount);
        arenaByteCount(arenaByteCount);
        return this;
    }

    /**
     * Get the memory segment of this {@link Capacity}.
     * @return the underlying memory segment
     */
    public MemorySegment segment() {
        return segment;
    }

    @Override
    public String toString() {
        return String.format("Capacity[staticShapeCount=%d, dynamicShapeCount=%d, staticBodyCount=%d, dynamicBodyCount=%d, contactCount=%d, arenaByteCount=%d]",
                staticShapeCount(), dynamicShapeCount(), staticBodyCount(), dynamicBodyCount(), contactCount(), arenaByteCount());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Capacity capacity)) return false;
        return staticShapeCount() == capacity.staticShapeCount()
                && dynamicShapeCount() == capacity.dynamicShapeCount()
                && staticBodyCount() == capacity.staticBodyCount()
                && dynamicBodyCount() == capacity.dynamicBodyCount()
                && contactCount() == capacity.contactCount()
                && arenaByteCount() == capacity.arenaByteCount();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = staticShapeCount();
        result = prime * result + dynamicShapeCount();
        result = prime * result + staticBodyCount();
        result = prime * result + dynamicBodyCount();
        result = prime * result + contactCount();
        result = prime * result + arenaByteCount();
        return result;
    }
}
