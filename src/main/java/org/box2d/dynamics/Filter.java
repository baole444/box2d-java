package org.box2d.dynamics;

import org.box2d.NativeLoader;
import org.box2d.internal.b2Filter;
import org.box2d.internal.nBox2D;

import java.lang.foreign.MemorySegment;

/**
 * Collision filtering data for a shape.
 * <p>
 * Two shapes collided when the category bits of each are present in the mask bits of each other.
 * In case where both are on the same none zero group index, a positive group always collides,
 * while negative group never, as this overridden the category and mask test.
 * </p>
 * Wrapper for native {@link b2Filter}
 */
public final class Filter {
    private final MemorySegment segment;

    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    /**
     * Create a new {@link Filter} and initialize it with Box2D's default values
     * (category bit 1, all mask bits set, group index 0.)
     */
    public Filter() {
        segment = nBox2D.nDefaultFilter();
    }

    /**
     * Create a new {@link Filter} with the given values
     * @param categoryBits the category bits of this shape
     * @param maskBits the categories this shape collides with
     * @param groupIndex the collision group index
     */
    public Filter(long categoryBits, long maskBits, int groupIndex) {
        segment = nBox2D.nDefaultFilter();
        categoryBits(categoryBits);
        maskBits(maskBits);
        groupIndex(groupIndex);
    }

    /**
     * Create a new {@link Filter} and initialize it to the one of the given filter
     * @param other the {@link Filter} to set the value from
     */
    public Filter(Filter other) {
        this(other.categoryBits(), other.maskBits(), other.groupIndex());
    }

    /**
     * Wrap an existing {@link b2Filter} memory segment.
     * The segment is not owned by this {@link Filter} instance.
     * @param segment the memory segment to wrap
     */
    public Filter(MemorySegment segment) {
        this.segment = segment;
    }

    /**
     * Create a new {@link Filter} with its values initialized to this filter's values.
     * @return a new {@link Filter}
     */
    public Filter copy() {
        return new Filter(categoryBits(), maskBits(), groupIndex());
    }

    /**
     * Get the category bits of this shape.
     * @return the category bits value
     */
    public long categoryBits() {
        return b2Filter.categoryBits(segment);
    }

    /**
     * Set the category bits of this shape to the given value.
     * @param categoryBits the category bits
     * @return this
     */
    public Filter categoryBits(long categoryBits) {
        b2Filter.categoryBits(segment, categoryBits);
        return this;
    }

    /**
     * Get the mask bits of this shape.
     * @return the mask bits value
     */
    public long maskBits() {
        return b2Filter.maskBits(segment);
    }

    /**
     * Set the mask bits of this shape, the categories it collides with.
     * @param maskBits the mask bits
     * @return this
     */
    public Filter maskBits(long maskBits) {
        b2Filter.maskBits(segment, maskBits);
        return this;
    }

    /**
     * Get the collision group index of this shape.
     * @return the group index value
     */
    public int groupIndex() {
        return b2Filter.groupIndex(segment);
    }

    /**
     * Set the collision group index of this shape.
     * <p>
     * A positive group always collides, while negative group does not. When the value is 0, collision is base on category and mask test.
     * @param groupIndex the group index
     * @return this
     */
    public Filter groupIndex(int groupIndex) {
        b2Filter.groupIndex(segment, groupIndex);
        return this;
    }

    /**
     * Set the category/mask bits, and group index of this filter to the given values.
     * @param categoryBits the category bits
     * @param maskBits the mask bits
     * @param groupIndex the group index
     * @return this
     */
    public Filter set(long categoryBits, long maskBits, int groupIndex) {
        categoryBits(categoryBits);
        maskBits(maskBits);
        groupIndex(groupIndex);
        return this;
    }

    /**
     * Get the memory segment of this {@link Filter}.
     * @return the underlying memory segment
     */
    public MemorySegment segment() {
        return segment;
    }

    @Override
    public String toString() {
        return String.format("Filter[categoryBits=0x%X, maskBits=0x%X, groupIndex=%d]", categoryBits(), maskBits(), groupIndex());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Filter filter)) return false;
        return categoryBits() == filter.categoryBits()
                && maskBits() == filter.maskBits()
                && groupIndex() == filter.groupIndex();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = Long.hashCode(categoryBits());
        result = prime * result + Long.hashCode(maskBits());
        result = prime * result + groupIndex();
        return result;
    }
}
