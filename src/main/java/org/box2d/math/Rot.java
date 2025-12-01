package org.box2d.math;

import org.box2d.NativeLoader;
import org.box2d.internal.b2Rot;
import org.box2d.internal.nBox2D;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * 2D rotation, represent cosine and sine.
 * This is similar to using a complex number for rotation.<br>
 * Wrapper for native {@link b2Rot}
 */
public class Rot {
    private final MemorySegment segment;
    private final Arena arena;

    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    /**
     * Create a new {@link Rot} and initialize its components
     * to that of an identity rotation (angle = 0 Rad).
     */
    public Rot() {
        this(0.0f);
    }

    /**
     * Create a new {@link Rot} and initialize its components using the given angle in Radians.
     * @param angle the angle to initialize this rotation's components with
     */
    public Rot(float angle) {
        arena = Arena.ofAuto();
        segment = arena.allocate(b2Rot.layout());
        setAngle(angle);
    }

    /**
     * Create a new {@link Rot} and initialize its components to the given values.
     * @param cosine the cosine component
     * @param sine the sine component
     */
    public Rot(float cosine, float sine) {
        arena = Arena.ofAuto();
        segment = arena.allocate(b2Rot.layout());
        setC(cosine);
        setS(sine);
    }

    /**
     * Create a new {@link Rot} and initialize its components to the one of the given rotation.
     * @param other the {@link Rot} to copy the value from
     */
    public Rot(Rot other) {
        this(other.c(), other.s());
    }

    /**
     * Wrap an existing {@link b2Rot} memory segment.
     * The segment is not owned by this {@link Rot} instance.
     * @param segment the memory segment to wrap
     */
    public Rot(MemorySegment segment) {
        this.segment = segment;
        arena = null;
    }

    /**
     * Create a new {@link Rot} and initialize its components
     * in the given arena, with the given angle in Radian.
     * @param arena the arena to allocate in
     * @param angle the angle to initialize this rotation's components with
     */
    public Rot(Arena arena, float angle) {
        this.arena = null;
        segment = arena.allocate(b2Rot.layout());
        setAngle(angle);
    }

    /**
     * Create a new {@link Rot} and initialize its components
     * in the given arena, with the given values.
     * @param arena the arena to allocate in
     * @param cosine the cosine component
     * @param sine the sine component
     */
    public Rot(Arena arena, float cosine, float sine) {
        this.arena = null;
        segment = arena.allocate(b2Rot.layout());
        setC(cosine);
        setS(sine);
    }

    /**
     * Get the cosine component of this rotation.
     * @return value of cosine component
     */
    public float c() {
        return b2Rot.c(segment);
    }

    /**
     * Set the cosine component to the given value.
     * @param cosine the cosine component
     * @return this
     */
    public Rot setC(float cosine) {
        b2Rot.c(segment, cosine);
        return this;
    }

    /**
     * Get the sine component of this rotation.
     * @return value of sine component
     */
    public float s() {
        return b2Rot.s(segment);
    }

    /**
     * Set the sine component to the given value.
     * @param sine the sine component
     * @return this
     */
    public Rot setS(float sine) {
        b2Rot.s(segment, sine);
        return this;
    }

    /**
     * Set the components of this rotation to the given values.
     * @param cosine the cosine component
     * @param sine the sine component
     * @return this
     */
    public Rot set(float cosine, float sine) {
        setC(cosine);
        setS(sine);
        return this;
    }

    /**
     * Get the angle of this rotation in Radians.
     * @return value of the rotation angle
     */
    public float angle() {
        return (float) Math.atan2(s(), c());
    }

    /**
     * Set the components of this rotation using the given angle value in Radians.
     * @param angle the angle to set this rotation's components
     * @return this
     */
    public Rot setAngle(float angle) {
        float c = (float) Math.cos(angle);
        float s = (float) Math.sin(angle);
        return set(c, s);
    }

    /**
     * Set the cosine and sine components to 1 and 0,
     * turn this rotation to identity rotation (angle = 0).
     * @return this
     */
    public Rot toIdentity() {
        return set(1.0f, 0.0f);
    }

    /**
     * Check if this rotation is identity rotation (angle = 0).
     * @return true if this is the identity rotation
     */
    public boolean isIdentity() {
        return c() == 1.0f && s() == 0.0f;
    }

    /**
     * Get the X axis direction vector of this rotation.
     * @return a new rotated x unit {@link Vec2}
     * @see Vec2#unitX() Get a unit vector along the X axis
     */
    public Vec2 xAxis() {
        return new Vec2(c(), s());
    }

    /**
     * Get the Y axis direction vector of this rotation.
     * @return a new rotated y unit {@link Vec2}
     * @see Vec2#unitY() Get a unit vector along the Y axis
     */
    public Vec2 yAxis() {
        return new Vec2(-s(), c());
    }

    /**
     * Create a new {@link Rot} with its components initialized to this rotation's component values.
     * @return a new {@link Rot}
     */
    public Rot copy() {
        return new Rot(c(), s());
    }

    /**
     * Check if this rotation is valid.
     * @return true if valid
     */
    public boolean isValid() {
        return nBox2D.nIsValidRotation(segment);
    }

    /**
     * Get the memory segment of this {@link Rot}.
     * @return the underlying memory segment
     */
    public MemorySegment segment() {
        return segment;
    }

    /**
     * Get a new {@link Rot} as the identity rotation (angle = 0).
     * @return a new identity {@link Rot}
     */
    public static Rot identity() {
        return new Rot();
    }

    /**
     * Get a new {@link Rot} with its components initialized using the given angle in Radians.
     * @param radians the angle to initialize the rotation's components with
     * @return a new {@link Rot}
     */
    public static Rot fromRadians(float radians) {
        return new Rot(radians);
    }

    /**
     * Get a new {@link Rot} with its components initialized using the given angle in Degrees.
     * @param degree the angle to initialize the rotation's components with
     * @return a new {@link Rot}
     */
    public static Rot fromDegrees(float degree) {
        return new Rot((float) Math.toRadians(degree));
    }

    @Override
    public String toString() {
        return String.format("Rot(c=%.3f, s=%.3f, angle=%.3f degrees)", c(), s(), Math.toDegrees(angle()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rot rot)) return false;
        return Float.compare(c(), rot.c()) == 0 && Float.compare(s(), rot.s()) == 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result * Float.floatToIntBits(c());
        result = prime * result * Float.floatToIntBits(s());
        return result;
    }
}
