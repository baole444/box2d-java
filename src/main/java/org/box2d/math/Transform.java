package org.box2d.math;

import org.box2d.NativeLoader;
import org.box2d.internal.b2Transform;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * 2D rigid transform, represent position and rotation.<br>
 * Wrapper for native {@link b2Transform}
 */
public class Transform {
    private final MemorySegment segment;
    private final Arena arena;

    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    /**
     * Create a new {@link Transform} and initialize its components
     * to that of an identity transform (position = (0,0), rotation = 0 Rad).
     */
    public Transform() {
        this(0f, 0f, 0f);
    }

    /**
     * Create a new {@link Transform} and initialized its position using the given vector
     * and rotation to identity rotation.
     * @param position the position component
     */
    public Transform(Vec2 position) {
        this(position, new Rot());
    }

    /**
     * Create a new {@link Transform} and initialize its components to the given values.
     * @param position the position component
     * @param angle the angle value in Radians for the rotation component
     */
    public Transform(Vec2 position, float angle) {
        this(position, new Rot(angle));
    }

    /**
     * Create a new {@link Transform} and initialize its components to the given values.
     * @param position the position component
     * @param rotation the rotation component
     */
    public Transform(Vec2 position, Rot rotation) {
        arena = Arena.ofAuto();
        segment = arena.allocate(b2Transform.layout());
        setPosition(position);
        setRotation(rotation);
    }

    /**
     * Create a new {@link Transform} and initialize its components to the given values.
     * @param x the position's x coordinate
     * @param y the position's y coordinate
     * @param angle the angle value in Radians for the rotation component
     */
    public Transform(float x, float y, float angle) {
        arena = Arena.ofAuto();
        segment = arena.allocate(b2Transform.layout());
        setPosition(x, y);
        setRotation(angle);
    }

    /**
     * Create a new {@link Transform} and initialize its components to the one of the given transform.
     * @param other the {@link Transform} to copy the value from
     */
    public Transform(Transform other) {
        this(other.position(), other.rotation());
    }

    /**
     * Wrap an existing {@link b2Transform} memory segment.
     * The segment is not owned by this {@link Transform} instance.
     * @param segment the memory segment to wrap
     */
    public Transform(MemorySegment segment) {
        this.segment = segment;
        arena = null;
    }

    /**
     * Create a new {@link Transform} and initialize its components
     * in the given arena, with the given values.
     * @param arena the arena to allocate in
     * @param position the position component
     * @param rotation the rotation component
     */
    public Transform(Arena arena, Vec2 position, Rot rotation) {
        this.arena = null;
        segment = arena.allocate(b2Transform.layout());
        setPosition(position);
        setRotation(rotation);
    }

    /**
     * Get the position's x coordinate of this transform.
     * @return value of x coordinate
     */
    public float x() {
        return position().x();
    }

    /**
     * Set the position's x coordinate to the given value.
     * @param x the position's x coordinate
     * @return this
     */
    public Transform setX(float x) {
        position().setX(x);
        return this;
    }

    /**
     * Get the position's y coordinate of this transform.
     * @return value of y coordinate
     */
    public float y() {
        return position().y();
    }

    /**
     * Set the position's y coordinate to the given value.
     * @param y the position's y coordinate
     * @return this
     */
    public Transform setY(float y) {
        position().setY(y);
        return this;
    }

    /**
     * Get the position component of this transform.
     * This returns a wrapper, not a new copy.
     * @return a {@link Vec2} that wrap the position component
     */
    public Vec2 position() {
        return new Vec2(b2Transform.p(segment));
    }

    /**
     * Set the position component to the given vector.
     * @param position the position component
     * @return this
     */
    public Transform setPosition(Vec2 position) {
        b2Transform.p(segment, position.segment());
        return this;
    }

    /**
     * Set the position component to the given values.
     * @param x the position's x coordinate
     * @param y the position's y coordinate
     * @return this
     */
    public Transform setPosition(float x, float y) {
        Vec2 pos = position();
        pos.set(x, y);
        return this;
    }

    /**
     * Get the rotation's angle of this transform.
     * @return the angle value in Radians
     */
    public float angle() {
        return rotation().angle();
    }

    /**
     * Get the rotation component of this transform.
     * This returns a wrapper, not a new copy.
     * @return a {@link Rot} that wrap the rotation component
     */
    public Rot rotation() {
        return new Rot(b2Transform.q(segment));
    }

    /**
     * Set the rotation component to the given rotation.
     * @param rotation the rotation component
     * @return this
     */
    public Transform setRotation(Rot rotation) {
        b2Transform.q(segment, rotation.segment());
        return this;
    }

    /**
     * Set the rotation component to the given angle in Radians.
     * @param angle the angle value in Radian to set the rotation components
     * @return this
     */
    public Transform setRotation(float angle) {
        Rot rot = rotation();
        rot.setAngle(angle);
        return this;
    }

    /**
     * Set the components of this transform to the given values.
     * @param position the position component
     * @param rotation the rotation component
     * @return this
     */
    public Transform set(Vec2 position, Rot rotation) {
        setPosition(position);
        setRotation(rotation);
        return this;
    }

    /**
     * Set the components of this transform to the given values.
     * @param x the position's x coordinate
     * @param y the position's y coordinate
     * @param angle the angle value in Radians to set the rotation's components
     * @return this
     */
    public Transform set(float x, float y, float angle) {
        setPosition(x, y);
        setRotation(angle);
        return this;
    }

    /**
     * Set the position and rotation components to (0,0) and identity rotation,
     * turn this transform to identity transform (position = (0,0), rotation = 0 Rad).
     * @return this
     */
    public Transform toIdentity() {
        setPosition(0.0f, 0.0f);
        setRotation(0.0f);
        return this;
    }

    /**
     * Create a new {@link Transform} with its components initialized to this transform's component values.
     * @return a new {@link Transform}
     */
    public Transform copy() {
        return new Transform(position(), rotation());
    }

    /**
     * Get the memory segment of this {@link Transform}
     * @return the underlying memory segment
     */
    public MemorySegment segment() {
        return segment;
    }

    /**
     * Get a new {@link Transform} as the identity transform (position = (0,0), rotation = 0 Rad).
     * @return a new identity {@link Transform}
     */
    public static Transform identity() {
        return new Transform();
    }

    @Override
    public String toString() {
        return String.format("Transform{p=(%.2f, %.2f), q=%.2f}", x(), y(), angle());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transform transform)) return false;
        return position().equals(transform.position()) && rotation().equals(transform.rotation());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = position().hashCode();
        result = prime * result * rotation().hashCode();
        return result;
    }
}
