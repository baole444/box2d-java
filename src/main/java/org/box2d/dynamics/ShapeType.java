package org.box2d.dynamics;

import org.box2d.NativeLoader;
import org.box2d.internal.nBox2D;

/**
 * The type of geometry a {@link Shape} is made of.
 * <p>
 * Maps to native {@code b2ShapeType}.
 */
public enum ShapeType {
    /**
     * A circle with an offset.
     */
    CircleShape(nBox2D.nCircleShape()),
    /**
     * A capsule is an extruded circle.
     */
    CapsuleShape(nBox2D.nCapsuleShape()),
    /**
     * A line segment.
     */
    SegmentShape(nBox2D.nSegmentShape()),
    /**
     * A convex polygon.
     */
    PolygonShape(nBox2D.nPolygonShape()),
    /**
     * A line segment owned by a chain shape.
     */
    ChainSegmentShape(nBox2D.nChainSegmentShape());

    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    final int nativeValue;

    ShapeType(int nativeValue) {
        this.nativeValue = nativeValue;
    }

    /**
     * Find the shape type of native {@code b2ShapeType} value.
     * @param nativeValue the native value
     * @return the matching shape type
     * @throws IllegalArgumentException when the value is not a shape type
     */
    static ShapeType of(int nativeValue) {
        for (ShapeType type : values()) {
            if (type.nativeValue == nativeValue) return type;
        }
        throw new IllegalArgumentException("Unknown b2ShapeType value: " + nativeValue);
    }
}
