package org.box2d.dynamics;

import org.box2d.internal.b2MotionLocks;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * Motion locks to restrict the body movement.
 * <p>
 * Each lock prevents one degree of freedom: translation along the x-axis, translation along the y-axis,
 * or rotation around the z-axis. Locking only the rotation makes a body keep its orientation, useful for characters.
 * <p>
 * Locks may lead to softer constraints along the locked direction.
 * <p>
 * Wrapper for native {@link b2MotionLocks}.
 */
public final class MotionLocks {
    private final MemorySegment segment;

    /**
     * Create a new {@link MotionLocks} with every lock released.
     */
    public MotionLocks() {
        segment = Arena.ofAuto().allocate(b2MotionLocks.layout());
    }

    /**
     * Create a new {@link MotionLocks} with the given locks.
     * @param linearX true to prevent translation along the x-axis
     * @param linearY true to prevent translation along the y-axis
     * @param angularZ true to prevent rotation around the z-axis
     */
    public MotionLocks(boolean linearX, boolean linearY, boolean angularZ) {
        this();
        set(linearX, linearY, angularZ);
    }

    /**
     * Create a new {@link MotionLocks} and initialise it to the locks of the given one.
     * @param other the {@link MotionLocks} to set the value from
     */
    public MotionLocks(MotionLocks other) {
        this(other.linearX(), other.linearY(), other.angularZ());
    }

    /**
     * Wrap an existing {@link b2MotionLocks} memory segment.
     * The segment is not owned by this {@link MotionLocks} instance.
     * @param segment the memory segment to wrap
     */
    public MotionLocks(MemorySegment segment) {
        this.segment = segment;
    }

    /**
     * Create a new {@link MotionLocks} with its values initialised to these locks.
     * @return a new {@link MotionLocks}
     */
    public MotionLocks copy() {
        return new MotionLocks(this);
    }

    /**
     * Check if translation along the x-axis is prevented.
     * @return true if locked
     */
    public boolean linearX() {
        return b2MotionLocks.linearX(segment);
    }

    /**
     * Set whether translation along the x-axis is prevented.
     * @param flag true to lock
     * @return this
     */
    public MotionLocks linearX(boolean flag) {
        b2MotionLocks.linearX(segment, flag);
        return this;
    }

    /**
     * Check if translation along the y-axis is prevented.
     * @return true if locked
     */
    public boolean linearY() {
        return b2MotionLocks.linearY(segment);
    }

    /**
     * Set whether translation along the y-axis is prevented.
     * @param flag true to lock
     * @return this
     */
    public MotionLocks linearY(boolean flag) {
        b2MotionLocks.linearY(segment, flag);
        return this;
    }

    /**
     * Check if rotation around the z-axis is prevented.
     * @return true if locked
     */
    public boolean angularZ() {
        return b2MotionLocks.angularZ(segment);
    }

    /**
     * Set whether rotation around the z-axis is prevented.
     * @param flag true to lock
     * @return this
     */
    public MotionLocks angularZ(boolean flag) {
        b2MotionLocks.angularZ(segment, flag);
        return this;
    }

    /**
     * Set every lock to the given values.
     * @param linearX true to prevent translation along the x-axis
     * @param linearY true to prevent translation along the y-axis
     * @param angularZ true to prevent rotation around the z-axis
     * @return this
     */
    public MotionLocks set(boolean linearX, boolean linearY, boolean angularZ) {
        linearX(linearX);
        linearY(linearY);
        angularZ(angularZ);
        return this;
    }

    /**
     * Get the memory segment of this {@link MotionLocks}.
     * @return the underlying memory segment
     */
    public MemorySegment segment() {
        return segment;
    }

    @Override
    public String toString() {
        return String.format("MotionLocks[linearX=%b, linearY=%b, angularZ=%b]", linearX(), linearY(), angularZ());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MotionLocks locks)) return false;
        return linearX() == locks.linearX() && linearY() == locks.linearY() && angularZ() == locks.angularZ();
    }

    @Override
    public int hashCode() {
        return (linearX() ? 1 : 0) | (linearY() ? 2 : 0) | (angularZ() ? 4 : 0);
    }
}
