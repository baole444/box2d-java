package org.box2d.internal;

import org.box2d.NativeLoader;

import java.lang.foreign.MemorySegment;

/**
 * Facade for native implementations of Box2D.
 */
public class nBox2D {
    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    private nBox2D() {}

    /**
     * Native implement of {@code b2IsValidRotation}.
     * @param segment the memory segment of {@link b2Rot}
     * @return true if the rotation is valid
     */
    public static boolean nIsValidRotation(MemorySegment segment) {
        return Box2D_1.b2IsValidRotation(segment);
    }

    /**
     * Native implement of {@code b2IsValidVec2}.
     * @param segment the memory segment of {@link b2Vec2}
     * @return true if the vector is valid
     */
    public static boolean nIsValidVec2(MemorySegment segment) {
        return Box2D_1.b2IsValidVec2(segment);
    }

    /**
     * Native implement of {@code b2IsValidFloat}.
     * @param val the float value to check
     * @return true if the value is valid
     */
    public static boolean nIsValidFloat(float val) {
        return Box2D_1.b2IsValidFloat(val);
    }
}
