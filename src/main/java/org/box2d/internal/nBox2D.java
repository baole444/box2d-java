package org.box2d.internal;

import org.box2d.NativeLoader;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * Facade for native implementations of Box2D.
 * <p>
 * This is the only place that calls into the generated {@code internal.Box2D*} bindings,
 * free the public wrappers from direct raw downcalls.
 * Methods that return a struct by value, allocate the result in an auto arena,
 * meaning returned segment manges its own lifetime through reachability.
 */
public class nBox2D {
    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    private nBox2D() {}

    /**
     * Native implement of {@code b2IsValidRotation}.
     * @param segment the memory segment of a {@link b2Rot}
     * @return true if the rotation is valid
     */
    public static boolean nIsValidRotation(MemorySegment segment) {
        return Box2D_1.b2IsValidRotation(segment);
    }

    /**
     * Native implement of {@code b2IsValidVec2}.
     * @param segment the memory segment of a {@link b2Vec2}
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

    /**
     * Native implement of {@code b2DefaultFilter}.
     * @return the memory segment of a {@link b2Filter}, initialized with Box2D's default values
     */
    public static MemorySegment nDefaultFilter() {
        return Box2D_1.b2DefaultFilter(Arena.ofAuto());
    }

    /**
     * Native implement of {@code b2MakeBox}.
     * @param halfWidth the box 1/2 width along the local x-axis
     * @param halfHeight the box 1/2 height along the local y-axis
     * @return the memory segment of the resulting {@link b2Polygon}
     */
    public static MemorySegment nMakeBox(float halfWidth, float halfHeight) {
        return Box2D_1.b2MakeBox(Arena.ofAuto(), halfWidth, halfHeight);
    }

    /**
     * Native implement of {@code b2MakeSquare}.
     * @param halfWidth the square 1/2 extent along both local axes
     * @return the memory segment of the resulting {@link b2Polygon}
     */
    public static MemorySegment nMakeSquare(float halfWidth) {
        return Box2D_1.b2MakeSquare(Arena.ofAuto(), halfWidth);
    }

    /**
     * Native implement of {@code b2MakeOffsetBox}.
     * @param halfWidth the box 1/2 width along the local x-axis
     * @param halfHeight the box 1/2 height along the local y-axis
     * @param centre the memory segment of the {@link b2Vec2} local centre
     * @param rotation the memory segment of the {@link b2Rot} local rotation
     * @return the memory segment of the resulting {@link b2Polygon}
     */
    public static MemorySegment nMakeOffsetBox(float halfWidth, float halfHeight, MemorySegment centre, MemorySegment rotation) {
        return Box2D_1.b2MakeOffsetBox(Arena.ofAuto(), halfWidth, halfHeight, centre, rotation);
    }

    /**
     * Native implement of {@code b2ComputeHull}.
     * @param points the memory segment of a {@link b2Vec2} array
     * @param count the number of points in the array
     * @return the memory segment of the resulting {@link b2Hull}, its {@code count} is 0 when the hull is degenerate
     */
    public static MemorySegment nComputeHull(MemorySegment points, int count) {
        return Box2D_1.b2ComputeHull(Arena.ofAuto(), points, count);
    }

    /**
     * Native implement of {@code b2MakePolygon}.
     * @param hull the memory segment of a valid {@link b2Hull}
     * @param radius the rounding radius of the polygon
     * @return the memory segment of the resulting {@link b2Polygon}
     */
    public static MemorySegment nMakePolygon(MemorySegment hull, float radius) {
        return Box2D_1.b2MakePolygon(Arena.ofAuto(), hull, radius);
    }

    /**
     * Native implement of {@code b2ComputeCircleMass}.
     * @param circle the memory segment of a {@link b2Circle}
     * @param density the area density in kg/m^2
     * @return the memory segment of the resulting {@link b2MassData}
     */
    public static MemorySegment nComputeCircleMass(MemorySegment circle, float density) {
        return Box2D_1.b2ComputeCircleMass(Arena.ofAuto(), circle, density);
    }

    /**
     * Native implement of {@code b2ComputeCapsuleMass}.
     * @param capsule the memory segment of a {@link b2Capsule}
     * @param density the area density in kg/m^2
     * @return the memory segment of the resulting {@link b2MassData}
     */
    public static MemorySegment nComputeCapsuleMass(MemorySegment capsule, float density) {
        return Box2D_1.b2ComputeCapsuleMass(Arena.ofAuto(), capsule, density);
    }

    /**
     * Native implement of {@code b2ComputePolygonMass}.
     * @param polygon the memory segment of a {@link b2Polygon}
     * @param density the area density in kg/m^2
     * @return the memory segment of the resulting {@link b2MassData}
     */
    public static MemorySegment nComputePolygonMass(MemorySegment polygon, float density) {
        return Box2D_1.b2ComputePolygonMass(Arena.ofAuto(), polygon, density);
    }

    /**
     * Native implement of {@code b2ComputeCircleAABB}.
     * @param circle the memory segment of a {@link b2Circle}
     * @param transform the memory segment of the {@link b2Transform} to place the shape with
     * @return the memory segment of the resulting {@link b2AABB}
     */
    public static MemorySegment nComputeCircleAABB(MemorySegment circle, MemorySegment transform) {
        return Box2D_1.b2ComputeCircleAABB(Arena.ofAuto(), circle, transform);
    }

    /**
     * Native implement of {@code b2ComputeCapsuleAABB}.
     * @param capsule the memory segment of a {@link b2Capsule}
     * @param transform the memory segment of the {@link b2Transform} to place the shape with
     * @return the memory segment of the resulting {@link b2AABB}
     */
    public static MemorySegment nComputeCapsuleAABB(MemorySegment capsule, MemorySegment transform) {
        return Box2D_1.b2ComputeCapsuleAABB(Arena.ofAuto(), capsule, transform);
    }

    /**
     * Native implement of {@code b2ComputePolygonAABB}.
     * @param polygon the memory segment of a {@link b2Polygon}
     * @param transform the memory segment of the {@link b2Transform} to place the shape with
     * @return the memory segment of the resulting {@link b2AABB}
     */
    public static MemorySegment nComputePolygonAABB(MemorySegment polygon, MemorySegment transform) {
        return Box2D_1.b2ComputePolygonAABB(Arena.ofAuto(), polygon, transform);
    }

    /**
     * Native implement of {@code b2ComputeSegmentAABB}.
     * @param segment the memory segment of a {@link b2Segment}
     * @param transform the memory segment of the {@link b2Transform} to place the shape with
     * @return the memory segment of the resulting {@link b2AABB}
     */
    public static MemorySegment nComputeSegmentAABB(MemorySegment segment, MemorySegment transform) {
        return Box2D_1.b2ComputeSegmentAABB(Arena.ofAuto(), segment, transform);
    }

    /**
     * Native implement of {@code b2PointInCircle}.
     * @param point the memory segment of the {@link b2Vec2} point, in the shape's local frame
     * @param circle the memory segment of a {@link b2Circle}
     * @return true if the point is inside the circle
     */
    public static boolean nPointInCircle(MemorySegment point, MemorySegment circle) {
        return Box2D_1.b2PointInCircle(point, circle);
    }

    /**
     * Native implement of {@code b2PointInCapsule}.
     * @param point the memory segment of the {@link b2Vec2} point, in the shape's local frame
     * @param capsule the memory segment of a {@link b2Capsule}
     * @return true if the point is inside the capsule
     */
    public static boolean nPointInCapsule(MemorySegment point, MemorySegment capsule) {
        return Box2D_1.b2PointInCapsule(point, capsule);
    }

    /**
     * Native implement of {@code b2PointInPolygon}.
     * @param point the memory segment of the {@link b2Vec2} point, in the shape's local frame
     * @param polygon the memory segment of a {@link b2Polygon}
     * @return true if the point is inside the polygon
     */
    public static boolean nPointInPolygon(MemorySegment point, MemorySegment polygon) {
        return Box2D_1.b2PointInPolygon(point, polygon);
    }
}
