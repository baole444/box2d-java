package org.box2d.collision;

import org.box2d.NativeLoader;
import org.box2d.internal.b2Capsule;
import org.box2d.internal.nBox2D;
import org.box2d.math.Transform;
import org.box2d.math.Vec2;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * Solid capsule geometry, composed by a circle's swept area of the given radius between two local centres.
 * <p>
 * Wrapper for native {@link b2Capsule}.
 */
public final class Capsule {
    private final MemorySegment segment;

    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    /**
     * Create a new {@link Capsule} with both centres at the origin and zero radius.
     */
    public Capsule() {
        segment = Arena.ofAuto().allocate(b2Capsule.layout());
    }

    /**
     * Create a new {@link Capsule} with the given centres and radius.
     * @param center1 the first local centre
     * @param center2 the second local centre
     * @param radius the radius
     */
    public Capsule(Vec2 center1, Vec2 center2, float radius) {
        segment = Arena.ofAuto().allocate(b2Capsule.layout());
        center1(center1);
        center2(center2);
        radius(radius);
    }

    /**
     * Create a new {@link Capsule} with the given centres and radius.
     * @param x1 the first centre x coordinate
     * @param y1 the first centre y coordinate
     * @param x2 the second centre x coordinate
     * @param y2 the second centre y coordinate
     * @param radius the radius
     */
    public Capsule(float x1, float y1, float x2, float y2, float radius) {
        segment = Arena.ofAuto().allocate(b2Capsule.layout());
        center1(x1, y1);
        center2(x2, y2);
        radius(radius);
    }

    /**
     * Create a new {@link Capsule} and initialize it to the one of the given capsule.
     * @param other the {@link Capsule} to copy the value from
     */
    public Capsule(Capsule other) {
        this(other.center1(), other.center2(), other.radius());
    }

    /**
     * Wrap an existing {@link b2Capsule} memory segment.
     * The segment is not owned by this {@link Capsule} instance.
     * @param segment the memory segment to wrap
     */
    public Capsule(MemorySegment segment) {
        this.segment = segment;
    }

    /**
     * Create a new {@link Capsule} in the given arena, with the given centres and radius.
     * @param arena the arena to allocate in
     * @param center1 the first local centre
     * @param center2 the second local centre
     * @param radius the radius
     */
    public Capsule(Arena arena, Vec2 center1, Vec2 center2, float radius) {
        segment = arena.allocate(b2Capsule.layout());
        center1(center1);
        center2(center2);
        radius(radius);
    }

    /**
     * Create a vertical capsule with its axis along the local y-axis.
     * It fits a bounding box defined by the given width and height, centred at the origin
     * <p>
     * The {@code width} is the capsule's diameter, while {@code height} is the full length from 2 tips.
     * The 2 centres sit at {@code (0, -(h - w) / 2)} and {@code (0, (h - w) / 2)}.
     * </p>
     * When {@code width} and {@code height} are equal, the returning capsule will be shaped like a circle,
     * with radius at {@code width / 2}.
     * @param width the capsule width, diameter in world units
     * @param height the capsule height, full length in world units
     * @return a new vertical {@link Capsule}
     */
    public static Capsule vertical(float width, float height) {
        float radius = width * 0.5f;
        float halfSegment = (height - width) * 0.5f;
        return new Capsule(0.0f, -halfSegment, 0.0f, halfSegment, radius);
    }

    /**
     * Create a horizontal capsule with its axis along the local x-axis.
     * It fits a bounding box defined by the given width and height, centred at the origin.
     * <p>
     * The {@code height} is the capsule's diameter, while {@code width} is the full length from 2 tips.
     * The 2 centres sit at {@code (-(w - h) / 2, 0)} and {@code ((w - h) / 2, 0)}.
     * </p>
     * When {@code width} and {@code height} are equal, the returning capsule will be shaped like a circle,
     * with radius at {@code height / 2}.
     * @param width the capsule width, full length in world units
     * @param height the capsule height, diameter in world units
     * @return a new horizontal {@link Capsule}
     */
    public static Capsule horizontal(float width, float height) {
        float radius = height * 0.5f;
        float halfSegment = (width - height) * 0.5f;
        return new Capsule(-halfSegment, 0.0f, halfSegment, 0.0f, radius);
    }

    /**
     * Create a new {@link Capsule} with its values initialized to this capsule's values.
     * @return a new {@link Capsule}
     */
    public Capsule copy() {
        return new Capsule(center1(), center2(), radius());
    }

    /**
     * Get the first local centre of this capsule.
     * This returns a wrapper, not a new copy.
     * @return a {@link Vec2} that wrap the first centre
     */
    public Vec2 center1() {
        return new Vec2(b2Capsule.center1(segment));
    }

    /**
     * Set the first local centre to the given vector.
     * @param center1 the first local centre
     * @return this
     */
    public Capsule center1(Vec2 center1) {
        b2Capsule.center1(segment, center1.segment());
        return this;
    }

    /**
     * Set the first local centre to the given values.
     * @param x the first centre x coordinate
     * @param y the first centre y coordinate
     * @return this
     */
    public Capsule center1(float x, float y) {
        center1().set(x, y);
        return this;
    }

    /**
     * Get the second local centre of this capsule.
     * This returns a wrapper, not a new copy.
     * @return a {@link Vec2} that wrap the second centre
     */
    public Vec2 center2() {
        return new Vec2(b2Capsule.center2(segment));
    }

    /**
     * Set the second local centre to the given vector.
     * @param center2 the second local centre
     * @return this
     */
    public Capsule center2(Vec2 center2) {
        b2Capsule.center2(segment, center2.segment());
        return this;
    }

    /**
     * Set the second local centre to the given values.
     * @param x the second centre x coordinate
     * @param y the second centre y coordinate
     * @return this
     */
    public Capsule center2(float x, float y) {
        center2().set(x, y);
        return this;
    }

    /**
     * Get the radius of this capsule.
     * @return the radius value
     */
    public float radius() {
        return b2Capsule.radius(segment);
    }

    /**
     * Set the radius of this capsule.
     * @param radius the radius
     * @return this
     */
    public Capsule radius(float radius) {
        b2Capsule.radius(segment, radius);
        return this;
    }

    /**
     * Check if the given point is inside this capsule's local frame or not.
     * @param point the point to test, in local coordinates
     * @return tru8 if the point is inside this capsule
     */
    public boolean pointInCapsule(Vec2 point) {
        return nBox2D.nPointInCapsule(point.segment(), segment);
    }

    /**
     * Compute the mass properties of this capsule for the given density.
     * @param density the area density in kg/m^2
     * @return a new {@link MassData} for this capsule
     */
    public MassData computeMass(float density) {
        return new MassData(nBox2D.nComputeCapsuleMass(segment, density));
    }

    /**
     * Compute the bounding box of this capsule when placed by the given transform.
     * @param transform the transform to place this capsule with
     * @return a new {@link AABB} containing this capsule
     */
    public AABB computeAABB(Transform transform) {
        return new AABB(nBox2D.nComputeCapsuleAABB(segment, transform.segment()));
    }

    /**
     * Get the memory segment of this {@link Capsule}.
     * @return the underlying memory segment
     */
    public MemorySegment segment() {
        return segment;
    }

    @Override
    public String toString() {
        return String.format("Capsule[center1=(%.3f, %.3f), center2=(%.3f, %.3f), radius=%.3f]", center1().x(), center1().y(), center2().x(), center2().y(), radius());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Capsule capsule)) return false;
        return Float.compare(radius(), capsule.radius()) == 0
                && center1().equals(capsule.center1())
                && center2().equals(capsule.center2());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = center1().hashCode();
        result = prime * result + center2().hashCode();
        result = prime * result + Float.floatToIntBits(radius());
        return result;
    }
}
