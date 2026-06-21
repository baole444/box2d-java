package org.box2d.collision;

import org.box2d.NativeLoader;
import org.box2d.internal.b2MassData;
import org.box2d.math.Vec2;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * Mass properties of a shape or body: total mass, local centre of mass and rotational inertia.
 * <p>
 * Wrapper for native {@link b2MassData}.
 */
public final class MassData {
    private final MemorySegment segment;

    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    /**
     * Create a new {@link MassData} with zero mass, inertia and a centre at the origin.
     */
    public MassData() {
        segment = Arena.ofAuto().allocate(b2MassData.layout());
    }

    /**
     * Create a new {@link MassData} and initialize its values.
     * @param mass the total mass
     * @param center the local centre of mass
     * @param rotationalInertia the rotational inertia about the local origin
     */
    public MassData(float mass, Vec2 center, float rotationalInertia) {
        segment = Arena.ofAuto().allocate(b2MassData.layout());
        mass(mass);
        center(center);
        rotationalInertia(rotationalInertia);
    }

    /**
     * Create a new {@link MassData} and initialize its values.
     * @param mass the total mass
     * @param centerX the local centre of mass x coordinate
     * @param centerY the local centre of mass y coordinate
     * @param rotationalInertia the rotational inertia about the local origin
     */
    public MassData(float mass, float centerX, float centerY, float rotationalInertia) {
        segment = Arena.ofAuto().allocate(b2MassData.layout());
        mass(mass);
        center(centerX, centerY);
        rotationalInertia(rotationalInertia);
    }

    /**
     * Create a new {@link MassData} and initialize its values to the one of the given mass data.
     * @param other the {@link MassData} to copy the value from
     */
    public MassData(MassData other) {
        this(other.mass(), other.center(), other.rotationalInertia());
    }

    /**
     * Wrap an existing {@link b2MassData} memory segment.
     * The segment is not owned by this {@link MassData} instance.
     * @param segment the memory segment to wrap
     */
    public MassData(MemorySegment segment) {
        this.segment = segment;
    }

    /**
     * Create a new {@link MassData} in the given arena, with the given values.
     * @param arena the arena to allocate in
     * @param mass the total mass
     * @param center the local centre of mass
     * @param rotationalInertia the rotational inertia about the local origin
     */
    public MassData(Arena arena, float mass, Vec2 center, float rotationalInertia) {
        segment = arena.allocate(b2MassData.layout());
        mass(mass);
        center(center);
        rotationalInertia(rotationalInertia);
    }

    public MassData copy() {
        return new MassData(mass(), center(), rotationalInertia());
    }

    /**
     * Get the total mass.
     * @return the mass value
     */
    public float mass() {
        return b2MassData.mass(segment);
    }

    /**
     * Set the total mass.
     * @param mass the total mass
     * @return this
     */
    public MassData mass(float mass) {
        b2MassData.mass(segment, mass);
        return this;
    }

    /**
     * Get the local centre of mass.
     * This returns a wrapper, not a new copy.
     * @return a {@link Vec2} that wrap the centre of mass
     */
    public Vec2 center() {
        return new Vec2(b2MassData.center(segment));
    }

    /**
     * Set the local centre of mass to the given vector.
     * @param center the local centre of mass
     * @return this
     */
    public MassData center(Vec2 center) {
        b2MassData.center(segment, center.segment());
        return this;
    }

    /**
     * Set the local centre of mass to the given values.
     * @param x the centre of mass x coordinate
     * @param y the centre of mass y coordinate
     * @return this
     */
    public MassData center(float x, float y) {
        center().set(x, y);
        return this;
    }

    /**
     * Get the rotational inertia about the local origin.
     * @return the rotational inertia value
     */
    public float rotationalInertia() {
        return b2MassData.rotationalInertia(segment);
    }

    /**
     * Set the rotational inertia about the local origin.
     * @param rotationalInertia the rotational inertia
     * @return this
     */
    public MassData rotationalInertia(float rotationalInertia) {
        b2MassData.rotationalInertia(segment, rotationalInertia);
        return this;
    }

    /**
     * Get the memory segment of this {@link MassData}.
     * @return the underlying memory segment
     */
    public MemorySegment segment() {
        return segment;
    }

    @Override
    public String toString() {
        return String.format("MassData[mass=%.3f, center=(%.3f, %.3f), rotationalInertia=%.3f]", mass(), center().x(), center().y(), rotationalInertia());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MassData massData)) return false;
        return Float.compare(mass(), massData.mass()) == 0
                && center().equals(massData.center())
                && Float.compare(rotationalInertia(), massData.rotationalInertia()) == 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = Float.floatToIntBits(mass());
        result = prime * result + center().hashCode();
        result = prime * result + Float.floatToIntBits(rotationalInertia());
        return result;
    }
}
