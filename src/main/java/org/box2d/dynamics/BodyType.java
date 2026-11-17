package org.box2d.dynamics;

import org.box2d.NativeLoader;
import org.box2d.internal.nBox2D;

/**
 * The body simulation type.
 * <p>
 * Each body is one of these three types. The type determines how the body behaves in the simulation.
 * <p>
 * Maps to native {@code b2BodyType}.
 */
public enum BodyType {
    /**
     * Zero mass, zero velocity, may be manually moved.
     */
    StaticBody(nBox2D.nStaticBody()),
    /**
     * Zero mass, velocity set by user, moved by solver.
     */
    KinematicBody(nBox2D.nKinematicBody()),
    /**
     * Positive mass, velocity determined by forces, moved by solver.
     */
    DynamicBody(nBox2D.nDynamicBody());

    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    final int nativeValue;

    BodyType(int nativeValue) {
        this.nativeValue = nativeValue;
    }

    /**
     * Find the body type of native {@code b2BodyType} value.
     * @param nativeValue the native value
     * @return the matching body type
     * @throws IllegalArgumentException when the value is not a body type
     */
    static BodyType of(int nativeValue) {
        for (BodyType type : values()) {
            if (type.nativeValue == nativeValue) return type;
        }
        throw new IllegalArgumentException("Unknown b2BodyType value: " + nativeValue);
    }
}
