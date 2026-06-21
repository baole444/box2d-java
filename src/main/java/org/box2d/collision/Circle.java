package org.box2d.collision;

import org.box2d.NativeLoader;
import org.box2d.internal.b2Circle;
import org.box2d.internal.nBox2D;
import org.box2d.math.Transform;
import org.box2d.math.Vec2;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * Solid circle geometry, defined by a local centre and radius.
 * <p>
 * Wrapper for native {@link b2Circle}.
 */
public final class Circle {
    private final MemorySegment segment;

    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    /**
     * Create a new {@link Circle} centred at the origin with zero radius.
     */
    public Circle() {
        segment = Arena.ofAuto().allocate(b2Circle.layout());
    }

    /**
     * Create a new {@link Circle} centred at the origin with the given radius.
     * @param radius the radius
     */
    public Circle(float radius) {
        segment = Arena.ofAuto().allocate(b2Circle.layout());
        radius(radius);
    }

    /**
     * Create a new {@link Circle} with the given centre and radius.
     * @param center the local centre
     * @param radius the radius
     */
    public Circle(Vec2 center, float radius) {
        segment = Arena.ofAuto().allocate(b2Circle.layout());
        center(center);
        radius(radius);
    }

    /**
     * Create a new {@link Circle} with the given centre and radius.
     * @param centerX the local centre x coordinate
     * @param centerY the local centre y coordinate
     * @param radius the radius
     */
    public Circle(float centerX, float centerY, float radius) {
        segment = Arena.ofAuto().allocate(b2Circle.layout());
        center(centerX, centerY);
        radius(radius);
    }

    /**
     * Create a new {@link Circle} and initialize it to the one of the given circle.
     * @param other the {@link Circle} to copy the value from
     */
    public Circle(Circle other) {
        this(other.center(), other.radius());
    }

    /**
     * Wrap an existing {@link b2Circle} memory segment.
     * The segment is not owned by this {@link Circle} instance.
     * @param segment the memory segment to wrap
     */
    public Circle(MemorySegment segment) {
        this.segment = segment;
    }

    /**
     * Create a new {@link Circle} in the given arena, with the given centre and radius.
     * @param arena the arena to allocate in
     * @param center the local centre
     * @param radius the radius
     */
    public Circle(Arena arena, Vec2 center, float radius) {
        segment = arena.allocate(b2Circle.layout());
        center(center);
        radius(radius);
    }

    /**
     * Create a new {@link Circle} with its values initialized to this circle's values.
     * @return a new {@link Circle}
     */
    public Circle copy() {
        return new Circle(center(), radius());
    }

    /**
     * Get the local centre of this circle.
     * This returns a wrapper, not a new copy.
     * @return a {@link Vec2} that wrap the centre
     */
    public Vec2 center() {
        return new Vec2(b2Circle.center(segment));
    }

    /**
     * Set the local centre of the given vector.
     * @param center the local centre
     * @return this
     */
    public Circle center(Vec2 center) {
        b2Circle.center(segment, center.segment());
        return this;
    }

    /**
     * Set the local centre to the given values.
     * @param x the centre x coordinate
     * @param y the centre y coordinate
     * @return this
     */
    public Circle center(float x, float y) {
        center().set(x, y);
        return this;
    }

    /**
     * Get the radius of this circle.
     * @return the radius value
     */
    public float radius() {
        return b2Circle.radius(segment);
    }

    /**
     * Set the radius of this circle.
     * @param radius the radius
     * @return this
     */
    public Circle radius(float radius) {
        b2Circle.radius(segment, radius);
        return this;
    }

    /**
     * Check if the given point is inside this circle's local frame or not.
     * @param point the point to test, in local coordinates
     * @return true if the point is inside this circle
     */
    public boolean pointInCircle(Vec2 point) {
        return nBox2D.nPointInCircle(point.segment(), segment);
    }

    /**
     * Compute the mass properties of this circle for the given density.
     * @param density the area density in kg/m^2
     * @return a new {@link MassData} for this circle
     */
    public MassData computeMass(float density) {
        return new MassData(nBox2D.nComputeCircleMass(segment, density));
    }

    /**
     * Compute the bounding box of this circle when placed by the given transform.
     * @param transform the transform to place this circle with
     * @return a new {@link AABB} containing this circle
     */
    public AABB computeAABB(Transform transform) {
        return new AABB(nBox2D.nComputeCircleAABB(segment, transform.segment()));
    }

    /**
     * Get the memory segment of this {@link Circle}.
     * @return the underlying memory segment
     */
    public MemorySegment segment() {
        return segment;
    }

    @Override
    public String toString() {
        return String.format("Circle[center=(%.3f, %.3f), radius=%.3f]", center().x(), center().y(), radius());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Circle circle)) return false;
        return Float.compare(radius(), circle.radius()) == 0 && center().equals(circle.center());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = center().hashCode();
        result = prime * result + Float.floatToIntBits(radius());
        return result;
    }
}
