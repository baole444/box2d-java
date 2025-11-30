package org.box2d.math;

import org.box2d.NativeLoader;
import org.box2d.internal.b2Vec2;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * 2D vector, represent a point or free vector.<br>
 * Wrapper for native {@link b2Vec2}
 */
public class Vec2 {
    private final MemorySegment segment;
    private final Arena arena;

    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    /**
     * Create a new {@link Vec2} and initialize its components to zero.
     */
    public Vec2() {
        this(0f, 0f);
    }

    /**
     * Create a new {@link Vec2} and initialize its components to the given value
     * @param x the x component
     * @param y the y component
     */
    public Vec2(float x, float y) {
        arena = Arena.ofAuto();
        segment = arena.allocate(b2Vec2.layout());
        setX(x);
        setY(y);
    }

    /**
     * Create a new {@link Vec2} and initialize its components to the one of the given vector.
     * @param other the {@link Vec2} to copy the value from
     */
    public Vec2(Vec2 other) {
        this(other.x(), other.y());
    }

    /**
     * Wrap an existing {@link b2Vec2} memory segment.
     * The segment is not owned by this {@link Vec2} instance.
     * @param segment the memory segment to wrap
     */
    public Vec2(MemorySegment segment) {
        this.segment = segment;
        arena = null;
    }

    /**
     * Create a new {@link Vec2} and initialize its components in the given arena.
     * @param arena the arena to allocate in
     * @param x the x component
     * @param y the y component
     */
    public Vec2(Arena arena, float x, float y) {
        this.arena = null;
        this.segment = arena.allocate(b2Vec2.layout());
    }

    /**
     * Create a new {@link Vec2} with its components initialized to this vector's components value.
     * @return a new {@link Vec2}
     */
    public Vec2 copy() {
       return new Vec2(x(), y());
    }

    /**
     * Get the x component of this vector.
     * @return value of x component
     */
    public float x() {
        return b2Vec2.x(segment);
    }

    /**
     * Set the x component to the given value.
     * @param x the x component
     * @return this
     */
    public Vec2 setX(float x) {
        b2Vec2.x(segment, x);
        return this;
    }

    /**
     * Get the y component of this vector.
     * @return value of y component
     */
    public float y() {
        return b2Vec2.y(segment);
    }

    /**
     * Set the y component to the given value.
     * @param y the y component
     * @return this
     */
    public Vec2 setY(float y) {
        b2Vec2.y(segment, y);
        return this;
    }

    /**
     * Set the x and y components to the given values.
     * @param x the x component
     * @param y the y component
     * @return this
     */
    public Vec2 set(float x, float y) {
        setX(x);
        setY(y);
        return this;
    }

    /**
     * Set the x and y components to zero.
     * @return this
     */
    public Vec2 toZero() {
       return set(0.0f, 0.0f);
    }

    /**
     * Negate this vector.
     * @return this
     */
    public Vec2 negate() {
        return set(-x(), -y());
    }

    /**
     * Add {@code other} to this vector.
     * @param other the vector to add
     * @return this
     */
    public Vec2 add(Vec2 other) {
        return set(x() + other.x(), y() + other.y());
    }

    /**
     * Increment the components of this vector by the given values.
     * @param x the x component to add
     * @param y the y component to add
     * @return this
     */
    public Vec2 add(float x, float y) {
        return set(x() + x, y() + y);
    }

    /**
     * Subtract {@code other} from this vector.
     * @param other - the vector to subtract
     * @return this
     */
    public Vec2 sub(Vec2 other) {
        return set(x() - other.x(), y() - other.y());
    }

    /**
     * Subtract {@code (x, y)} from this vector.
     * @param x the x component to subtract
     * @param y the y component to subtract
     * @return this
     */
    public Vec2 sub(float x, float y) {
        return set(x() - x, y() - y);
    }

    /**
     * Multiply the components of this vector by the given scalar.
     * @param scalar the value to multiply this vector's components by
     * @return this
     */
    public Vec2 mul(float scalar) {
        return set(x() * scalar, y() * scalar);
    }

    /**
     * Multiply the components of this vector by the given scalar values.
     * @param x the x component to multiply this vector by
     * @param y the y component to multiply this vector by
     * @return this
     */
    public Vec2 mul(float x, float y) {
        return set(x() * x, y() * y);
    }

    /**
     * Multiply this vector component-wise by another vector.
     * @param other the vector to multiply by
     * @return this
     */
    public Vec2 mul(Vec2 other) {
        return set(x() * other.x(), y() * other.y());
    }

    /**
     * Divide all components of this vector by the given scalar.
     * @param scalar the value to divide this vector's component by
     * @return this
     */
    public Vec2 div(float scalar) {
        return set(x() / scalar, y() / scalar);
    }

    /**
     * Divide the components of this vector by the given scalar values.
     * @param x the x component to divide this vector by
     * @param y the y component to divide this vector by
     * @return this
     */
    public Vec2 div(float x, float y) {
        return set(x() / x, y() / y);
    }

    /**
     * Divide this vector component-wise by another vector.
     * @param other the vector to divide by
     * @return this
     */
    public Vec2 div(Vec2 other) {
        return set(x() / other.x(), y() / other.y());
    }

    /**
     * Get the dot product of this and another vector.
     * @param other the other vector to calculate the dot product with
     * @return the value of dot product
     */
    public float dot(Vec2 other) {
        return x() * other.x() + y() * other.y();
    }

    /**
     * Get the cross product of this and another vector.
     * In 2D, this will result in the z component of the 3D cross product.
     * @param other the other vector to calculate the cross product with
     * @return the value of cross product (scalar)
     */
    public float cross(Vec2 other) {
        return x() * other.y() - y() * other.x();
    }

    /**
     * Get the cross product between this vector and the given scalar.
     * @param scalar the value to calculate the cross product with
     * @return a new {@link Vec2} with its components initialized as the cross product result
     */
    public Vec2 cross(float scalar) {
        return new Vec2(scalar * y(), -scalar * x());
    }

    /**
     * Get the length (magnitude) of this vector.
     * @return the length of this vector
     */
    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }

    /**
     * Get the length squared of this vector.
     * @return the squared length of this vector
     */
    public float lengthSquared() {
        float x = x();
        float y = y();
        return x * x + y * y;
    }

    /**
     * Normalize this vector to unit length (unit vector).
     * @return this
     */
    public Vec2 normalize() {
        float length = length();
        if (length > 0.0f) {
            float invertedLength = 1.0f / length;
            mul(invertedLength);
        }

        return this;
    }

    /**
     * Scale this vector to have the given length.
     * @param length the desired length
     * @return this
     */
    public Vec2 normalize(float length) {
        float invertedLength = 1.0f / length() * length;
        return mul(invertedLength);
    }

    /**
     * Get the distance between this and the other vector.
     * @param other the other vector to calculate the distance with
     * @return the Euclidean distance
     */
    public float distance(Vec2 other) {
        float dx = other.x() - x();
        float dy = other.y() - y();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Get the squared distance between this and the other vector.
     * @param other the other vector to calculate the squared distance with
     * @return the Euclidean distance squared
     */
    public float distanceSquared(Vec2 other) {
        float dx = other.x() - x();
        float dy = other.y() - y();
        return dx * dx + dy * dy;
    }

    /**
     * Check if this vector is valid.
     * This requires its components to be not {@code NaN} or infinite.
     * @return true if valid
     */
    public boolean isValid() {
        float x = x();
        float y = y();
        return !Float.isNaN(x) && !Float.isNaN(y) && !Float.isInfinite(x) && !Float.isInfinite(y);
    }

    /**
     * Get the memory segment of this {@link Vec2}.
     * @return the underlying memory segment
     */
    public MemorySegment segment() {
        return segment;
    }

    /**
     * Get a new {@link Vec2} with its components initialized to zero.
     * @return a new {@link Vec2}
     */
    public static Vec2 zero() {
        return new Vec2();
    }

    /**
     * Get a new {@link Vec2} that is a unit vector along the x-axis {@code (1, 0)}.
     * @return a new x unit {@link Vec2}
     */
    public static Vec2 unitX() {
        return new Vec2(1.0f, 0.0f);
    }

    /**
     * Get a new {@link Vec2} that is a unit vector along the y-axis {@code (0, 1)}.
     * @return a new y unit {@link Vec2}
     */
    public static Vec2 unitY() {
        return new Vec2(0.0f, 1.0f);
    }

    @Override
    public String toString() {
        return String.format("Vec2(%.3f, %.3f", x(), y());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vec2 vec2)) return false;
        return Float.compare(x(), vec2.x()) == 0 && Float.compare(y(), vec2.y()) == 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(x());
        result = prime * result + Float.floatToIntBits(y());
        return result;
    }
}
