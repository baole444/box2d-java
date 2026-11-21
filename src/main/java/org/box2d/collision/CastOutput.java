package org.box2d.collision;

import org.box2d.NativeLoader;
import org.box2d.internal.b2CastOutput;
import org.box2d.math.Vec2;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * Low level ray cast or shape cast output data.
 * <p>
 * The hit point is in the local or relative frame of the input. Returns a zero fraction and normal in the case of initial overlap.
 * <p>
 * Wrapper for native {@link b2CastOutput}.
 */
public final class CastOutput {
    private final MemorySegment segment;

    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    /**
     * Wrap an existing {@link b2CastOutput} memory segment.
     * The segment is not owned by this {@link CastOutput} instance.
     * @param segment the memory segment to wrap
     */
    public CastOutput(MemorySegment segment) {
        this.segment = segment;
    }

    /**
     * Create a new {@link CastOutput} with its values initialised to this output's values.
     * @return a new {@link CastOutput}
     */
    public CastOutput copy() {
        MemorySegment destination = Arena.ofAuto().allocate(b2CastOutput.layout());
        MemorySegment.copy(segment, 0L, destination, 0L, b2CastOutput.layout().byteSize());
        return new CastOutput(destination);
    }

    /**
     * Get the surface normal at the hit point.
     * @return a new {@link Vec2} holding the normal
     */
    public Vec2 normal() {
        return new Vec2(b2CastOutput.normal(segment)).copy();
    }

    /**
     * Get the surface hit point.
     * @return a new {@link Vec2} holding the hit point
     */
    public Vec2 point() {
        return new Vec2(b2CastOutput.point(segment)).copy();
    }

    /**
     * Get the fraction of the input translation at collision.
     * @return the fraction, in [0, 1]
     */
    public float fraction() {
        return b2CastOutput.fraction(segment);
    }

    /**
     * Get the number of iterations used.
     * @return the iteration count
     */
    public int iterations() {
        return b2CastOutput.iterations(segment);
    }

    /**
     * Check if the cast hit.
     * @return true if the cast hit
     */
    public boolean hit() {
        return b2CastOutput.hit(segment);
    }

    /**
     * Get the memory segment of this {@link CastOutput}.
     * @return the underlying memory segment
     */
    public MemorySegment segment() {
        return segment;
    }

    @Override
    public String toString() {
        return String.format("CastOutput[hit=%b, point=%s, normal=%s, fraction=%.3f, iterations=%d]", hit(), point(), normal(), fraction(), iterations());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CastOutput output)) return false;
        return hit() == output.hit()
                && iterations() == output.iterations()
                && Float.compare(fraction(), output.fraction()) == 0
                && normal().equals(output.normal())
                && point().equals(output.point());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = Boolean.hashCode(hit());
        result = prime * result + iterations();
        result = prime * result + Float.floatToIntBits(fraction());
        result = prime * result + normal().hashCode();
        result = prime * result + point().hashCode();
        return result;
    }
}
