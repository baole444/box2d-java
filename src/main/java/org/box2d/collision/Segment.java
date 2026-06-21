package org.box2d.collision;

import org.box2d.NativeLoader;
import org.box2d.internal.b2Segment;
import org.box2d.internal.nBox2D;
import org.box2d.math.Transform;
import org.box2d.math.Vec2;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * Line segment geometry, defined by two local endpoints. Segments have no volume and no mass,
 * often attached to static bodies.
 * <p>
 * Wrapper for native {@link b2Segment}.
 */
public final class Segment {
    private final MemorySegment segment;

    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    /**
     * Create a new {@link Segment} with both endpoints at the origin.
     */
    public Segment() {
        segment = Arena.ofAuto().allocate(b2Segment.layout());
    }

    /**
     * Create a new {@link Segment} with the given endpoint
     * @param point1 the first local endpoint
     * @param point2 the second local endpoint
     */
    public Segment(Vec2 point1, Vec2 point2) {
        segment = Arena.ofAuto().allocate(b2Segment.layout());
        point1(point1);
        point2(point2);
    }

    /**
     * Create a new {@link Segment} with the given endpoints.
     * @param x1 the first endpoint x coordinate
     * @param y1 the second endpoint y coordinate
     * @param x2 the second endpoint x coordinate
     * @param y2 the second endpoint y coordinate
     */
    public Segment(float x1, float y1, float x2, float y2) {
        segment = Arena.ofAuto().allocate(b2Segment.layout());
        point1(x1, y1);
        point2(x2, y2);
    }

    /**
     * Create a new {@link Segment} and initialize it to the one of the given segment.
     * @param other the {@link Segment} to copy the value from
     */
    public Segment(Segment other) {
        this(other.point1(), other.point2());
    }

    /**
     * Wrap an existing {@link b2Segment} memory segment.
     * The segment is not owned by this {@link Segment} instance.
     * @param segment the memory segment to wrap
     */
    public Segment(MemorySegment segment) {
        this.segment = segment;
    }

    /**
     * Create a new {@link Segment} in the given arena, with the given endpoints.
     * @param arena the arena to allocate in
     * @param point1 the first local endpoint
     * @param point2 the second local endpoint
     */
    public Segment(Arena arena, Vec2 point1, Vec2 point2) {
        segment = arena.allocate(b2Segment.layout());
        point1(point1);
        point2(point2);
    }

    /**
     * Create a new {@link Segment} with its endpoints initialized to this segment's endpoints.
     * @return a new {@link Segment}
     */
    public Segment copy() {
        return new Segment(point1(), point2());
    }

    /**
     * Get the first endpoint of this segment.
     * This return a wrapper, not a new copy.
     * @return a {@link Vec2} that wrap the first endpoint
     */
    public Vec2 point1() {
        return new Vec2(b2Segment.point1(segment));
    }

    /**
     * Set the first endpoint to the given vector.
     * @param point1 the first endpoint
     * @return this
     */
    public Segment point1(Vec2 point1) {
        b2Segment.point1(segment, point1.segment());
        return this;
    }

    /**
     * Set the first endpoint to the given values.
     * @param x the first endpoint x coordinate
     * @param y the first endpoint y coordinate
     * @return this
     */
    public Segment point1(float x, float y) {
        point1().set(x, y);
        return this;
    }

    /**
     * Get the second endpoint of this segment.
     * This returns a wrapper, not a new copy.
     * @return a {@link Vec2} that wrap the second endpoint
     */
    public Vec2 point2() {
        return new Vec2(b2Segment.point2(segment));
    }

    /**
     * Set the second endpoint to the given vector.
     * @param point2 the second endpoint
     * @return this
     */
    public Segment point2(Vec2 point2) {
        b2Segment.point2(segment, point2.segment());
        return this;
    }

    /**
     * Set the second endpoint to the given values.
     * @param x the second endpoint x coordinate
     * @param y the second endpoint y coordinate
     * @return this
     */
    public Segment point2(float x, float y) {
        point2().set(x, y);
        return this;
    }

    /**
     * Get the length of this segment.
     * @return the distance between the two endpoints
     */
    public float length() {
        return point1().distance(point2());
    }

    /**
     * Compute the bounding box of this segment when placed by the given transform.
     * @param transform the transform to place this segment with
     * @return a new {@link AABB} containing this segment
     */
    public AABB computeAABB(Transform transform) {
        return new AABB(nBox2D.nComputeSegmentAABB(segment, transform.segment()));
    }

    /**
     * Get the memory segment of this {@link Segment}.
     * @return the underlying memory segment
     */
    public MemorySegment segment() {
        return segment;
    }

    @Override
    public String toString() {
        return String.format("Segment[point1=(%.3f, %.3f), point2=(%.3f, %.3f)]", point1().x(), point1().y(), point2().x(), point2().y());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Segment seg)) return false;
        return point1().equals(seg.point1()) && point2().equals(seg.point2());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = point1().hashCode();
        result = prime * result + point2().hashCode();
        return result;
    }
}
