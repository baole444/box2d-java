package org.box2d;

import org.box2d.internal.nBox2D;
import org.box2d.math.Rot;
import org.box2d.math.Vec2;

/**
 * Public API of Box2D
 */
public class Box2D {
    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    private Box2D() {}

    /**
     * Check if a rotation is valid.
     * A valid rotation is normalized and its components are not NaN or infinite.
     * @param rotation the rotation to validate
     * @return true if valid
     */
    public static boolean isValidRotation(Rot rotation) {
        if (rotation == null) return false;
        return nBox2D.nIsValidRotation(rotation.segment());
    }

    /**
     * Check if a vector is valid.
     * A valid vector's components are not NaN or infinite.
     * @param vector the vector to validate
     * @return true if valid
     */
    public static boolean isValidVec2(Vec2 vector) {
        if (vector == null) return false;
        return nBox2D.nIsValidVec2(vector.segment());
    }

    /**
     * Check if a float value is valid.
     * A valid float value is not NaN or infinite.
     * @param value the float value to validate
     * @return true if valid
     */
    public static boolean isValidFloat(float value) {
        return nBox2D.nIsValidFloat(value);
    }
}
