package org.box2d.collision;

import org.box2d.NativeLoader;
import org.box2d.internal.b2AABB;
import org.box2d.math.Vec2;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * Axis aligned bounding box, defined by a lower and an upper bound.
 * <p>
 * Wrapper for native {@link b2AABB}.
 */
public final class AABB {
    private final MemorySegment segment;

    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    /**
     * Create a new {@link AABB} with both bounds at the origin.
     */
    public AABB() {
        segment = Arena.ofAuto().allocate(b2AABB.layout());
    }

    /**
     * Create a new {@link AABB} and initialize its bounds to the given vectors.
     * @param lowerBound the lower bound
     * @param upperBound the upper bound
     */
    public AABB(Vec2 lowerBound, Vec2 upperBound) {
        segment = Arena.ofAuto().allocate(b2AABB.layout());
        lowerBound(lowerBound);
        upperBound(upperBound);
    }

    /**
     * Create a new {@link AABB} and initialize its bounds to the given values.
     * @param lowerX the lower bound x coordinate
     * @param lowerY the lower bound y coordinate
     * @param upperX the upper bound x coordinate
     * @param upperY the upper bound y coordinate
     */
    public AABB(float lowerX, float lowerY, float upperX, float upperY) {
        segment = Arena.ofAuto().allocate(b2AABB.layout());
        lowerBound(lowerX, lowerY);
        upperBound(upperX, upperY);
    }

    /**
     * Create a new {@link AABB} and initialize its bounds to the one of the given box.
     * @param other the {@link AABB} to copy the value from
     */
    public AABB(AABB other) {
        this(other.lowerBound(), other.upperBound());
    }

    /**
     * Wrap an existing {@link b2AABB} memory segment.
     * The segment is not owned by this {@link AABB} instance.
     * @param segment the memory segment to wrap
     */
    public AABB(MemorySegment segment) {
        this.segment = segment;
    }

    /**
     * Create a new {@link AABB} in the given arena, with the given bounds.
     * @param arena the arena to allocate in
     * @param lowerBound the lower bound
     * @param upperBound the upper bound
     */
    public AABB(Arena arena, Vec2 lowerBound, Vec2 upperBound) {
        segment = arena.allocate(b2AABB.layout());
        lowerBound(lowerBound);
        upperBound(upperBound);
    }

    /**
     * Create a new {@link AABB} with its bounds initialized to this box's bounds.
     * @return a new {@link AABB}
     */
    public AABB copy() {
        return new AABB(lowerBound(), upperBound());
    }

    /**
     * Get the lower bound of this box.
     * This returns a wrapper, not a new copy.
     * @return a {@link Vec2} that wrap the lower bound
     */
    public Vec2 lowerBound() {
        return new Vec2(b2AABB.lowerBound(segment));
    }

    /**
     * Set the lower bound to the given vector.
     * @param lowerBound the lower bound
     * @return this
     */
    public AABB lowerBound(Vec2 lowerBound) {
        b2AABB.lowerBound(segment, lowerBound.segment());
        return this;
    }

    /**
     * Set the lower bound to the given values
     * @param x the lower bound x coordinate
     * @param y the lower bound y coordinate
     * @return this
     */
    public AABB lowerBound(float x, float y) {
        lowerBound().set(x, y);
        return this;
    }

    /**
     * Get the upper bound of this box.
     * This returns a wrapper, not a new copy.
     * @return a {@link Vec2} that wrap the upper bound
     */
    public Vec2 upperBound() {
        return new Vec2(b2AABB.upperBound(segment));
    }

    /**
     * Set the upper bound to the given vector.
     * @param upperBound the upper bound
     * @return this
     */
    public AABB upperBound(Vec2 upperBound) {
        b2AABB.upperBound(segment, upperBound.segment());
        return this;
    }

    /**
     * Set the upper bound to the given values
     * @param x the upper bound x coordinate
     * @param y the upper bound y coordinate
     * @return this
     */
    public AABB upperBound(float x, float y) {
        upperBound().set(x, y);
        return this;
    }

    /**
     * Set the bounds of this box to the given vectors.
     * @param lowerBound the lower bound
     * @param upperBound the upper bound
     * @return this
     */
    public AABB set(Vec2 lowerBound, Vec2 upperBound) {
        lowerBound(lowerBound);
        upperBound(upperBound);
        return this;
    }

    /**
     * Set the bounds of this box to the given values.
     * @param lowerX the lower bound x coordinate
     * @param lowerY the lower bound y coordinate
     * @param upperX the upper bound x coordinate
     * @param upperY the upper bound y coordinate
     * @return this
     */
    public AABB set(float lowerX, float lowerY, float upperX, float upperY) {
        lowerBound(lowerX, lowerY);
        upperBound(upperX, upperY);
        return this;
    }

    /**
     * Get the centre of this box.
     * @return a new {@link Vec2} at the centre of this box
     */
    public Vec2 center() {
        Vec2 lower = lowerBound();
        Vec2 upper = upperBound();
        return new Vec2(0.5f * (lower.x() + upper.x()), 0.5f * (lower.y() + upper.y()));
    }

    public Vec2 extents() {
        Vec2 lower = lowerBound();
        Vec2 upper = upperBound();
        return new Vec2(0.5f * (upper.x() - lower.x()), 0.5f * (upper.y() - lower.y()));
    }

    /**
     * Combine this box with the given box, growing this box to contain both, union.
     * @param other the box to combine into this box
     * @return this
     */
    public AABB combine(AABB other) {
        Vec2 lower = lowerBound();
        Vec2 upper = upperBound();
        Vec2 otherLower = other.lowerBound();
        Vec2 otherUpper = other.upperBound();
        lower.set(Math.min(lower.x(), otherLower.x()), Math.min(lower.y(), otherLower.y()));
        upper.set(Math.max(upper.x(), otherUpper.x()), Math.max(upper.y(), otherUpper.y()));
        return this;
    }

    /**
     * Check if this box fully contains the given box.
     * @param other the box to test
     * @return true if this box contains {@code other}
     */
    public boolean contains(AABB other) {
        Vec2 lower = lowerBound();
        Vec2 upper = upperBound();
        Vec2 otherLower = other.lowerBound();
        Vec2 otherUpper = other.upperBound();
        return lower.x() <= otherLower.x() && lower.y() <= otherLower.y() && otherUpper.x() <= upper.x() && otherUpper.y() <= upper.y();
    }

    /**
     * Check if this box overlaps the given box.
     * @param other the box to test
     * @return true if the two boxes overlap
     */
    public boolean overlaps(AABB other) {
        Vec2 lower = lowerBound();
        Vec2 upper = upperBound();
        Vec2 otherLower = other.lowerBound();
        Vec2 otherUpper = other.upperBound();
        return lower.x() <= otherUpper.x() && otherLower.x() <= upper.x() && lower.y() <= otherUpper.y() && otherLower.y() <= upper.y();
    }

    /**
     * Check if this box is valid.
     * A valid box has no negative extents and finite bounds.
     * @return true if valid
     */
    public boolean isValid() {
        Vec2 lower = lowerBound();
        Vec2 upper = upperBound();
        float dx = upper.x() - lower.x();
        float dy = upper.y() - lower.y();
        return dx >= 0.0f && dy >= 0.0f && lower.isValid() && upper.isValid();
    }

    /**
     * Get the memory segment of this {@link AABB}.
     * @return the underlying memory segment
     */
    public MemorySegment segment() {
        return segment;
    }

    @Override
    public String toString() {
        return String.format("AABB[lower=(%.3f, %.3f), upper=(%.3f, %.3f)]", lowerBound().x(), lowerBound().y(), upperBound().x(), upperBound().y());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AABB aabb)) return false;
        return lowerBound().equals(aabb.lowerBound()) && upperBound().equals(aabb.upperBound());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = lowerBound().hashCode();
        result = prime * result + upperBound().hashCode();
        return result;
    }
}
