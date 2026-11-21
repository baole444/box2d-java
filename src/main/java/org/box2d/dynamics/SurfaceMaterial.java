package org.box2d.dynamics;

import org.box2d.NativeLoader;
import org.box2d.internal.b2SurfaceMaterial;
import org.box2d.internal.nBox2D;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * The surface material groups together parameters related to the shape surface, which is the 2D boundary.
 * <p>
 * Surface materials allow chain shapes to have per segment properties.
 * <p>
 * Wrapper for native {@link b2SurfaceMaterial}.
 */
public final class SurfaceMaterial {
    private final MemorySegment segment;

    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    /**
     * Create a new {@link SurfaceMaterial} initialised with Box2D's default values (friction 0.6, every other value 0).
     */
    public SurfaceMaterial() {
        segment = nBox2D.nDefaultSurfaceMaterial();
    }

    /**
     * Create a new {@link SurfaceMaterial} and initialise it to the values of the given material.
     * @param other the {@link SurfaceMaterial} to copy the values from
     */
    public SurfaceMaterial(SurfaceMaterial other) {
        segment = Arena.ofAuto().allocate(b2SurfaceMaterial.layout());
        MemorySegment.copy(other.segment, 0L, segment, 0L, b2SurfaceMaterial.layout().byteSize());
    }

    /**
     * Wrap an existing {@link b2SurfaceMaterial} memory segment.
     * The segment is not owned by this {@link SurfaceMaterial} instance.
     * @param segment the memory segment to wrap
     */
    public SurfaceMaterial(MemorySegment segment) {
        this.segment = segment;
    }

    /**
     * Create a new {@link SurfaceMaterial} with its values initialised to this material's values.
     * @return a new {@link SurfaceMaterial}
     */
    public SurfaceMaterial copy() {
        return new SurfaceMaterial(this);
    }

    /**
     * Get the Coulomb (dry) friction coefficient.
     * @return the friction coefficient
     */
    public float friction() {
        return b2SurfaceMaterial.friction(segment);
    }

    /**
     * Set the Coulomb (dry) friction coefficient, usually in the range [0, 1].
     * @param friction the non-negative friction coefficient
     * @return this
     */
    public SurfaceMaterial friction(float friction) {
        b2SurfaceMaterial.friction(segment, friction);
        return this;
    }

    /**
     * Get the coefficient of restitution (bounce).
     * @return the coefficient of restitution
     * @see <a href="https://en.wikipedia.org/wiki/Coefficient_of_restitution">Coefficient of Restitution</a>
     */
    public float restitution() {
        return b2SurfaceMaterial.restitution(segment);
    }

    /**
     * Set the coefficient of restitution (bounce), usually in the range [0, 1].
     * @param restitution the non-negative coefficient of restitution
     * @return this
     */
    public SurfaceMaterial restitution(float restitution) {
        b2SurfaceMaterial.restitution(segment, restitution);
        return this;
    }

    /**
     * Get the rolling resistance.
     * @return the rolling resistance
     */
    public float rollingResistance() {
        return b2SurfaceMaterial.rollingResistance(segment);
    }

    /**
     * Set the rolling resistance, usually in the range [0, 1].
     * @param rollingResistance the non-negative rolling resistance
     * @return this
     */
    public SurfaceMaterial rollingResistance(float rollingResistance) {
        b2SurfaceMaterial.rollingResistance(segment, rollingResistance);
        return this;
    }

    /**
     * Get the tangent speed for conveyor belts, usually in m/s.
     * @return the tangent speed
     */
    public float tangentSpeed() {
        return b2SurfaceMaterial.tangentSpeed(segment);
    }

    /**
     * Set the tangent speed for conveyor belts, usually in m/s.
     * @param tangentSpeed the tangent speed
     * @return this
     */
    public SurfaceMaterial tangentSpeed(float tangentSpeed) {
        b2SurfaceMaterial.tangentSpeed(segment, tangentSpeed);
        return this;
    }

    /**
     * Get the user material identifier.
     * @return the 64-bit identifier, to be read as unsigned
     * @see Long#toUnsignedString(long) Convert long to unsigned string
     */
    public long userMaterialId() {
        return b2SurfaceMaterial.userMaterialId(segment);
    }

    /**
     * Set the user material identifier.
     * <p>
     * This is passed with query results and to friction and restitution combining functions. It is not used internally.
     * @param userMaterialId the 64-bit identifier, read as unsigned
     * @return this
     */
    public SurfaceMaterial userMaterialId(long userMaterialId) {
        b2SurfaceMaterial.userMaterialId(segment, userMaterialId);
        return this;
    }

    /**
     * Get the custom debug draw colour.
     * @return the colour as {@code 0xRRGGBB}, or 0 for none
     */
    public int customColor() {
        return b2SurfaceMaterial.customColor(segment);
    }

    /**
     * Set the custom debug draw colour.
     * @param customColor the colour as {@code 0xRRGGBB}, or 0 for none
     * @return this
     */
    public SurfaceMaterial customColor(int customColor) {
        b2SurfaceMaterial.customColor(segment, customColor);
        return this;
    }

    /**
     * Get the memory segment of this {@link SurfaceMaterial}.
     * @return the underlying memory segment
     */
    public MemorySegment segment() {
        return segment;
    }

    @Override
    public String toString() {
        return String.format("SurfaceMaterial[friction=%.3f, restitution=%.3f, rollingResistance=%.3f, tangentSpeed=%.3f, userMaterialId=%s, customColor=0x%06X]",
                friction(), restitution(), rollingResistance(), tangentSpeed(), Long.toUnsignedString(userMaterialId()), customColor());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SurfaceMaterial material)) return false;
        return Float.compare(friction(), material.friction()) == 0
                && Float.compare(restitution(), material.restitution()) == 0
                && Float.compare(rollingResistance(), material.rollingResistance()) == 0
                && Float.compare(tangentSpeed(), material.tangentSpeed()) == 0
                && userMaterialId() == material.userMaterialId()
                && customColor() == material.customColor();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = Float.floatToIntBits(friction());
        result = prime * result + Float.floatToIntBits(restitution());
        result = prime * result + Float.floatToIntBits(rollingResistance());
        result = prime * result + Float.floatToIntBits(tangentSpeed());
        result = prime * result + Long.hashCode(userMaterialId());
        result = prime * result + customColor();
        return result;
    }
}
