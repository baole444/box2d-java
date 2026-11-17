package org.box2d.internal;

import org.box2d.NativeLoader;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

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
     * Native implement of {@code b2IsValidRot}.
     * @param segment the memory segment of a {@link b2Rot}
     * @return true if the rotation is valid
     */
    public static boolean nIsValidRot(MemorySegment segment) {
        return Box2D.b2IsValidRot(segment);
    }

    /**
     * Native implement of {@code b2IsValidVec2}.
     * @param segment the memory segment of a {@link b2Vec2}
     * @return true if the vector is valid
     */
    public static boolean nIsValidVec2(MemorySegment segment) {
        return Box2D.b2IsValidVec2(segment);
    }

    /**
     * Native implement of {@code b2IsValidFloat}.
     * @param val the float value to check
     * @return true if the value is valid
     */
    public static boolean nIsValidFloat(float val) {
        return Box2D.b2IsValidFloat(val);
    }

    /**
     * Native implement of {@code b2DefaultFilter}.
     * @return the memory segment of a {@link b2Filter}, initialised with Box2D's default values
     */
    public static MemorySegment nDefaultFilter() {
        return Box2D.b2DefaultFilter(Arena.ofAuto());
    }

    /**
     * Native implement of {@code b2MakeBox}.
     * @param halfWidth the box 1/2 width along the local x-axis
     * @param halfHeight the box 1/2 height along the local y-axis
     * @return the memory segment of the resulting {@link b2Polygon}
     */
    public static MemorySegment nMakeBox(float halfWidth, float halfHeight) {
        return Box2D.b2MakeBox(Arena.ofAuto(), halfWidth, halfHeight);
    }

    /**
     * Native implement of {@code b2MakeSquare}.
     * @param halfWidth the square 1/2 extent along both local axes
     * @return the memory segment of the resulting {@link b2Polygon}
     */
    public static MemorySegment nMakeSquare(float halfWidth) {
        return Box2D.b2MakeSquare(Arena.ofAuto(), halfWidth);
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
        return Box2D.b2MakeOffsetBox(Arena.ofAuto(), halfWidth, halfHeight, centre, rotation);
    }

    /**
     * Native implement of {@code b2ComputeHull}.
     * @param points the memory segment of a {@link b2Vec2} array
     * @param count the number of points in the array
     * @return the memory segment of the resulting {@link b2Hull}, its {@code count} is 0 when the hull is degenerate
     */
    public static MemorySegment nComputeHull(MemorySegment points, int count) {
        return Box2D.b2ComputeHull(Arena.ofAuto(), points, count);
    }

    /**
     * Native implement of {@code b2MakePolygon}.
     * @param hull the memory segment of a valid {@link b2Hull}
     * @param radius the rounding radius of the polygon
     * @return the memory segment of the resulting {@link b2Polygon}
     */
    public static MemorySegment nMakePolygon(MemorySegment hull, float radius) {
        return Box2D.b2MakePolygon(Arena.ofAuto(), hull, radius);
    }

    /**
     * Native implement of {@code b2ComputeCircleMass}.
     * @param circle the memory segment of a {@link b2Circle}
     * @param density the area density in kg/m²
     * @return the memory segment of the resulting {@link b2MassData}
     */
    public static MemorySegment nComputeCircleMass(MemorySegment circle, float density) {
        return Box2D.b2ComputeCircleMass(Arena.ofAuto(), circle, density);
    }

    /**
     * Native implement of {@code b2ComputeCapsuleMass}.
     * @param capsule the memory segment of a {@link b2Capsule}
     * @param density the area density in kg/m²
     * @return the memory segment of the resulting {@link b2MassData}
     */
    public static MemorySegment nComputeCapsuleMass(MemorySegment capsule, float density) {
        return Box2D.b2ComputeCapsuleMass(Arena.ofAuto(), capsule, density);
    }

    /**
     * Native implement of {@code b2ComputePolygonMass}.
     * @param polygon the memory segment of a {@link b2Polygon}
     * @param density the area density in kg/m²
     * @return the memory segment of the resulting {@link b2MassData}
     */
    public static MemorySegment nComputePolygonMass(MemorySegment polygon, float density) {
        return Box2D.b2ComputePolygonMass(Arena.ofAuto(), polygon, density);
    }

    /**
     * Native implement of {@code b2ComputeCircleBounds}.
     * @param circle the memory segment of a {@link b2Circle}
     * @param transform the memory segment of the {@link b2WorldTransform} to place the shape with
     * @return the memory segment of the resulting {@link b2AABB}
     */
    public static MemorySegment nComputeCircleBounds(MemorySegment circle, MemorySegment transform) {
        return Box2D.b2ComputeCircleBounds(Arena.ofAuto(), circle, transform);
    }

    /**
     * Native implement of {@code b2ComputeCapsuleBounds}.
     * @param capsule the memory segment of a {@link b2Capsule}
     * @param transform the memory segment of the {@link b2WorldTransform} to place the shape with
     * @return the memory segment of the resulting {@link b2AABB}
     */
    public static MemorySegment nComputeCapsuleBounds(MemorySegment capsule, MemorySegment transform) {
        return Box2D.b2ComputeCapsuleBounds(Arena.ofAuto(), capsule, transform);
    }

    /**
     * Native implement of {@code b2ComputePolygonBounds}.
     * @param polygon the memory segment of a {@link b2Polygon}
     * @param transform the memory segment of the {@link b2WorldTransform} to place the shape with
     * @return the memory segment of the resulting {@link b2AABB}
     */
    public static MemorySegment nComputePolygonBounds(MemorySegment polygon, MemorySegment transform) {
        return Box2D.b2ComputePolygonBounds(Arena.ofAuto(), polygon, transform);
    }

    /**
     * Native implement of {@code b2ComputeSegmentBounds}.
     * @param segment the memory segment of a {@link b2Segment}
     * @param transform the memory segment of the {@link b2WorldTransform} to place the shape with
     * @return the memory segment of the resulting {@link b2AABB}
     */
    public static MemorySegment nComputeSegmentBounds(MemorySegment segment, MemorySegment transform) {
        return Box2D.b2ComputeSegmentBounds(Arena.ofAuto(), segment, transform);
    }

    /**
     * Native implement of {@code b2PointInCircle}.
     * @param circle the memory segment of a {@link b2Circle}
     * @param localPoint the memory segment of the {@link b2Vec2} in the shape local coordinates
     * @return true if the point is inside the circle
     */
    public static boolean nPointInCircle(MemorySegment circle, MemorySegment localPoint) {
        return Box2D.b2PointInCircle(circle, localPoint);
    }

    /**
     * Native implement of {@code b2PointInCapsule}.
     * @param capsule the memory segment of a {@link b2Capsule}
     * @param localPoint the memory segment of the {@link b2Vec2} in the shape local coordinates
     * @return true if the point is inside the capsule
     */
    public static boolean nPointInCapsule(MemorySegment capsule, MemorySegment localPoint) {
        return Box2D.b2PointInCapsule(capsule, localPoint);
    }

    /**
     * Native implement of {@code b2PointInPolygon}.
     * @param polygon the memory segment of a {@link b2Polygon}
     * @param localPoint the memory segment of the {@link b2Vec2} in the shape local coordinates
     * @return true if the point is inside the polygon
     */
    public static boolean nPointInPolygon(MemorySegment polygon, MemorySegment localPoint) {
        return Box2D.b2PointInPolygon(polygon, localPoint);
    }

    /**
     * Native constant {@code b2_staticBody}.
     * @return the enum value for native {@code b2BodyType}'s static body
     */
    public static int nStaticBody() {
        return Box2D.b2_staticBody();
    }

    /**
     * Native constant {@code b2_kinematicBody}.
     * @return the enum value for native {@code b2BodyType}'s kinematic body
     */
    public static int nKinematicBody() {
        return Box2D.b2_kinematicBody();
    }

    /**
     * Native constant {@code b2_dynamicBody}.
     * @return the enum value for native {@code b2BodyType}'s dynamic body
     */
    public static int nDynamicBody() {
        return Box2D.b2_dynamicBody();
    }

    /**
     * Native implement of {@code b2Body_GetLinearVelocity}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return the memory segment of the resulting linear velocity {@link b2Vec2} at the centre of mass
     */
    public static MemorySegment nBodyGetLinearVelocity(MemorySegment bodyId) {
        return Box2D.b2Body_GetLinearVelocity(Arena.ofAuto(), bodyId);
    }

    /**
     * Native implement of {@code b2Body_GetLocalCenter}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return the memory segment of the resulting centre of mass {@link b2Vec2} in body local coordinate
     */
    public static MemorySegment nBodyGetLocalCenter(MemorySegment bodyId) {
        return Box2D.b2Body_GetLocalCenter(Arena.ofAuto(), bodyId);
    }

    /**
     * Native implement of {@code b2Body_GetLocalPoint}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param worldPoint the memory segment of the {@link b2Vec2} in world coordinates
     * @return the memory segment of the resulting linear velocity {@link b2Vec2} of the point
     */
    public static MemorySegment nBodyGetLocalPoint(MemorySegment bodyId, MemorySegment worldPoint) {
        return Box2D.b2Body_GetLocalPoint(Arena.ofAuto(), bodyId, worldPoint);
    }

    /**
     * Native implement of {@code b2Body_GetLocalPointVelocity}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param localPoint the memory segment of the {@link b2Vec2} in body local coordinates
     * @return the memory segment of the resulting linear velocity {@link b2Vec2} of the point
     */
    public static MemorySegment nBodyGetLocalPointVelocity(MemorySegment bodyId, MemorySegment localPoint) {
        return Box2D.b2Body_GetLocalPointVelocity(Arena.ofAuto(), bodyId, localPoint);
    }

    /**
     * Native implement of {@code b2Body_GetLocalVector}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param worldVector the memory segment of the {@link b2Vec2} in world coordinates
     * @return the memory segment of the resulting {@link b2Vec2} in body local coordinates
     */
    public static MemorySegment nBodyGetLocalVector(MemorySegment bodyId, MemorySegment worldVector) {
        return Box2D.b2Body_GetLocalVector(Arena.ofAuto(), bodyId, worldVector);
    }

    /**
     * Native implement of {@code b2Body_GetMassData}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return the memory segment of the resulting {@link b2MassData}
     */
    public static MemorySegment nBodyGetMassData(MemorySegment bodyId) {
        return Box2D.b2Body_GetMassData(Arena.ofAuto(), bodyId);
    }

    /**
     * Native implement of {@code b2Body_GetName}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return a pointer to the body's null-terminated name ({@code B2_NAME_LENGTH} + 1 bytes), valid until the body is destroyed
     */
    public static MemorySegment nBodyGetName(MemorySegment bodyId) {
        return Box2D.b2Body_GetName(bodyId);
    }

    /**
     * Native implement of {@code b2Body_GetMotionLocks}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return the memory segment of the resulting {@link b2MotionLocks}
     */
    public static MemorySegment nBodyGetMotionLocks(MemorySegment bodyId) {
        return Box2D.b2Body_GetMotionLocks(Arena.ofAuto(), bodyId);
    }

    /**
     * Native implement of {@code b2Body_GetSafetyFactor}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return the continuous collision safety factor, non-dimensional
     */
    public static float nBodyGetSafetyFactor(MemorySegment bodyId) {
        return Box2D.b2Body_GetSafetyFactor(bodyId);
    }

    /**
     * Native implement of {@code b2Body_GetPosition}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return the memory segment of the resulting body origin {@link b2Vec2} in world space
     */
    public static MemorySegment nBodyGetPosition(MemorySegment bodyId) {
        return Box2D.b2Body_GetPosition(Arena.ofAuto(), bodyId);
    }

    /**
     * Native implement of {@code b2Body_GetRotation}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return the memory segment of the resulting {@link b2Rot} in world space
     */
    public static MemorySegment nBodyGetRotation(MemorySegment bodyId) {
        return Box2D.b2Body_GetRotation(Arena.ofAuto(), bodyId);
    }

    /**
     * Native implement of {@code b2Body_GetTransform}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return the memory segment of the resulting {@link b2Transform} in world space
     */
    public static MemorySegment nBodyGetTransform(MemorySegment bodyId) {
        return Box2D.b2Body_GetTransform(Arena.ofAuto(), bodyId);
    }

    /**
     * Native implement of {@code b2Body_GetUserData}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return the pointer of the user data, {@link MemorySegment#NULL} if none exist
     */
    public static MemorySegment nBodyGetUserData(MemorySegment bodyId) {
        return Box2D.b2Body_GetUserData(bodyId);
    }

    /**
     * Native implement of {@code b2Body_GetWorldCenter}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return the memory segment of the resulting centre of mass {@link b2Vec2} in world coordinates
     */
    public static MemorySegment nBodyGetWorldCenter(MemorySegment bodyId) {
        return Box2D.b2Body_GetWorldCenter(Arena.ofAuto(), bodyId);
    }

    /**
     * Native implement of {@code b2Body_GetWorldPoint}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param localPoint the memory segment of the {@link b2Vec2} in body local coordinates
     * @return the memory segment of the resulting {@link b2Vec2} in world coordinates
     */
    public static MemorySegment nBodyGetWorldPoint(MemorySegment bodyId, MemorySegment localPoint) {
        return Box2D.b2Body_GetWorldPoint(Arena.ofAuto(), bodyId, localPoint);
    }

    /**
     * Native implement of {@code b2Body_GetWorldPointVelocity}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param worldPoint the memory segment of the {@link b2Vec2} in world coordinates
     * @return the memory segment of the resulting linear velocity {@link b2Vec2} of the point
     */
    public static MemorySegment nBodyGetWorldPointVelocity(MemorySegment bodyId, MemorySegment worldPoint) {
        return Box2D.b2Body_GetWorldPointVelocity(Arena.ofAuto(), bodyId, worldPoint);
    }

    /**
     * Native implement of {@code b2Body_GetWorldVector}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param localVector the memory segment of the {@link b2Vec2} in body local coordinates
     * @return the memory segment of the resulting {@link b2Vec2} in world coordinates
     */
    public static MemorySegment nBodyGetWorldVector(MemorySegment bodyId, MemorySegment localVector) {
        return Box2D.b2Body_GetWorldVector(Arena.ofAuto(), bodyId, localVector);
    }

    /**
     * Native implement of {@code b2CreateBody}.
     * @param worldId the memory segment of the {@link b2WorldId}
     * @param def the memory segment of the {@link b2BodyDef}, copied by Box2D
     * @return the memory segment of the new {@link b2BodyId}, or null id if the world is locked
     */
    public static MemorySegment nCreateBody(MemorySegment worldId, MemorySegment def) {
        return Box2D.b2CreateBody(Arena.ofAuto(), worldId, def);
    }

    /**
     * Native implement of {@code b2CreateWorld}.
     * @param def the memory segment of the {@link b2WorldDef}, copied by Box2D
     * @return the memory segment of the new {@link b2WorldId}
     */
    public static MemorySegment nCreateWorld(MemorySegment def) {
        return Box2D.b2CreateWorld(Arena.ofAuto(), def);
    }

    /**
     * Native implement of {@code b2DefaultWorldDef}.
     * @return the memory segment of a {@link b2WorldDef}, initialised with Box2D's default values
     */
    public static MemorySegment nDefaultBodyDef() {
        return Box2D.b2DefaultBodyDef(Arena.ofAuto());
    }

    /**
     * Native implement of {@code b2DefaultWorldDef}.
     * @return the memory segment of a {@link b2WorldDef}, initialised with Box2D's default values
     */
    public static MemorySegment nDefaultWorldDef() {
        return Box2D.b2DefaultWorldDef(Arena.ofAuto());
    }

    /**
     * Native implement of {@code b2World_GetGravity}.
     * @param worldId the memory segment of the {@link b2WorldId}
     * @return the memory segment of the resulting gravity {@link b2Vec2}
     */
    public static MemorySegment nWorldGetGravity(MemorySegment worldId) {
        return Box2D.b2World_GetGravity(Arena.ofAuto(), worldId);
    }

    /**
     * Native implement of {@code b2World_GetUserData}.
     * @param worldId the memory segment of the {@link b2WorldId}
     * @return the pointer to the user data, {@link MemorySegment#NULL} if none exist
     */
    public static MemorySegment nWorldGetUserData(MemorySegment worldId) {
        return Box2D.b2World_GetUserData(worldId);
    }

    /**
     * Native implement of {@code b2Body_IsAwake}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return true if the body is awake
     */
    public static boolean nBodyIsAwake(MemorySegment bodyId) {
        return Box2D.b2Body_IsAwake(bodyId);
    }

    /**
     * Native implement of {@code b2Body_IsBullet}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return true if the body is a bullet
     */
    public static boolean nBodyIsBullet(MemorySegment bodyId) {
        return Box2D.b2Body_IsBullet(bodyId);
    }

    /**
     * Native implement of {@code b2Body_IsContactRecyclingEnabled}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return true if contact recycling is enabled
     */
    public static boolean nBodyIsContactRecyclingEnabled(MemorySegment bodyId) {
        return Box2D.b2Body_IsContactRecyclingEnabled(bodyId);
    }

    /**
     * Native implement of {@code b2Body_IsEnabled}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return true if the body is enabled
     */
    public static boolean nBodyIsEnabled(MemorySegment bodyId) {
        return Box2D.b2Body_IsEnabled(bodyId);
    }

    /**
     * Native implement of {@code b2Body_IsSleepingEnabled}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return true if the body is allowed to sleep
     */
    public static boolean nBodyIsSleepingEnabled(MemorySegment bodyId) {
        return Box2D.b2Body_IsSleepingEnabled(bodyId);
    }

    /**
     * Native implement of {@code b2Body_IsValid}.
     * @param id the memory segment of the {@link b2BodyId} to check
     * @return true if the id refers to a live body
     */
    public static boolean nBodyIsValid(MemorySegment id) {
        return Box2D.b2Body_IsValid(id);
    }

    /**
     * Native implement of {@code b2World_IsContinuousEnabled}.
     * @param worldId the memory segment of the {@link b2WorldId}
     * @return true if continuous collision is enabled
     */
    public static boolean nWorldIsContinuousEnabled(MemorySegment worldId) {
        return Box2D.b2World_IsContinuousEnabled(worldId);
    }

    /**
     * Native implement of {@code b2World_IsSleepingEnabled}.
     * @param worldId the memory segment of the {@link b2WorldId}
     * @return true if sleeping is enabled
     */
    public static boolean nWorldIsSleepingEnabled(MemorySegment worldId) {
        return Box2D.b2World_IsSleepingEnabled(worldId);
    }

    /**
     * Native implement of {@code b2World_IsValid}.
     * @param id the memory segment of the {@link b2WorldId} to check
     * @return true if the id refers to a live world
     */
    public static boolean nWorldIsValid(MemorySegment id) {
        return Box2D.b2World_IsValid(id);
    }

    /**
     * Native implement of {@code b2World_IsWarmStartingEnabled}.
     * @param worldId the memory segment of the {@link b2WorldId}
     * @return true if warm starting is enabled
     */
    public static boolean nWorldIsWarmStartingEnabled(MemorySegment worldId) {
        return Box2D.b2World_IsWarmStartingEnabled(worldId);
    }

    /**
     * Native implement of {@code b2Body_GetAngularDamping}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return the angular damping factor
     */
    public static float nBodyGetAngularDamping(MemorySegment bodyId) {
        return Box2D.b2Body_GetAngularDamping(bodyId);
    }

    /**
     * Native implement of {@code b2Body_GetAngularVelocity}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return the angular velocity, in rad/s
     */
    public static float nBodyGetAngularVelocity(MemorySegment bodyId) {
        return Box2D.b2Body_GetAngularVelocity(bodyId);
    }

    /**
     * Native implement of {@code b2Body_GetGravityScale}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return the gravity scale factor
     */
    public static float nBodyGetGravityScale(MemorySegment bodyId) {
        return Box2D.b2Body_GetGravityScale(bodyId);
    }

    /**
     * Native implement of {@code b2Body_GetLinearDamping}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return the linear damping factor
     */
    public static float nBodyGetLinearDamping(MemorySegment bodyId) {
        return Box2D.b2Body_GetLinearDamping(bodyId);
    }

    /**
     * Native implement of {@code b2Body_GetMass}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return the mass, usually in kilograms
     */
    public static float nBodyGetMass(MemorySegment bodyId) {
        return Box2D.b2Body_GetMass(bodyId);
    }

    /**
     * Native implement of {@code b2Body_GetRotationalInertia}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return the rotational inertia, usually in kg*m²
     */
    public static float nBodyGetRotationalInertia(MemorySegment bodyId) {
        return Box2D.b2Body_GetRotationalInertia(bodyId);
    }

    /**
     * Native implement of {@code b2Body_GetSleepThreshold}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return the sleep threshold, usually in m/s
     */
    public static float nBodyGetSleepThreshold(MemorySegment bodyId) {
        return Box2D.b2Body_GetSleepThreshold(bodyId);
    }

    /**
     * Native implement of {@code b2World_GetHitEventThreshold}.
     * @param worldId the memory segment of the {@link b2WorldId}
     * @return the hit event threshold, usually in m/s
     */
    public static float nWorldGetHitEventThreshold(MemorySegment worldId) {
        return Box2D.b2World_GetHitEventThreshold(worldId);
    }

    /**
     * Native implement of {@code b2World_GetContactRecycleDistance}.
     * @param worldId the memory segment of the {@link b2WorldId}
     * @return the contact point recycling distance, usually in meters
     */
    public static float nWorldGetContactRecycleDistance(MemorySegment worldId) {
        return Box2D.b2World_GetContactRecycleDistance(worldId);
    }

    /**
     * Native implement of {@code b2World_GetContactTuning}.
     * @param worldId the memory segment of the {@link b2WorldId}
     * @return the contact hertz, damping ratio and push speed, in that order
     */
    public static float[] nWorldGetContactTuning(MemorySegment worldId) {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment values = arena.allocate(ValueLayout.JAVA_FLOAT, 3);
            Box2D.b2World_GetContactTuning(worldId, values, values.asSlice(Float.BYTES), values.asSlice(2L * Float.BYTES));
            return values.toArray(ValueLayout.JAVA_FLOAT);
        }
    }

    /**
     * Native implement of {@code b2World_GetMaxLinearSpeed}.
     * @param worldId the memory segment of the {@link b2WorldId}
     * @return the maximum linear speed, usually in m/s
     */
    public static float nWorldGetMaxLinearSpeed(MemorySegment worldId) {
        return Box2D.b2World_GetMaxLinearSpeed(worldId);
    }

    /**
     * Native implement of {@code b2World_GetRestitutionThreshold}.
     * @param worldId the memory segment of the {@link b2WorldId}
     * @return the restitution threshold, usually in m/s
     */
    public static float nWorldGetRestitutionThreshold(MemorySegment worldId) {
        return Box2D.b2World_GetRestitutionThreshold(worldId);
    }

    /**
     * Native implement of {@code b2Body_GetShapeCount}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return the number of shapes on the body
     */
    public static int nBodyGetShapeCount(MemorySegment bodyId) {
        return Box2D.b2Body_GetShapeCount(bodyId);
    }

    /**
     * Native implement of {@code b2Body_GetShapes}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param shapeArray the memory segment of a {@link b2ShapeId} array to fill
     * @param capacity the number of {@link b2ShapeId} elements the array can hold
     * @return the number of shape ids written to the array
     */
    public static int nBodyGetShapes(MemorySegment bodyId, MemorySegment shapeArray, int capacity) {
        return Box2D.b2Body_GetShapes(bodyId, shapeArray, capacity);
    }

    /**
     * Native implement of {@code b2Body_GetType}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @return the native {@code b2BodyType} value
     */
    public static int nBodyGetType(MemorySegment bodyId) {
        return Box2D.b2Body_GetType(bodyId);
    }

    /**
     * Native implement of {@code b2World_GetAwakeBodyCount}.
     * @param worldId the memory segment of the {@link b2WorldId}
     * @return the number of awake bodies
     */
    public static int nWorldGetAwakeBodyCount(MemorySegment worldId) {
        return Box2D.b2World_GetAwakeBodyCount(worldId);
    }

    /**
     * Native implement of {@code b2Body_ApplyAngularImpulse}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param impulse the angular impulse, usually in kg*m*m/s
     * @param wake true to wake up the body
     */
    public static void nBodyApplyAngularImpulse(MemorySegment bodyId, float impulse, boolean wake) {
        Box2D.b2Body_ApplyAngularImpulse(bodyId, impulse, wake);
    }

    /**
     * Native implement of {@code b2Body_ApplyForce}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param force the memory segment of the world force {@link b2Vec2}, usually in newtons (N)
     * @param point the memory segment of the point of application {@link b2Vec2} in world space
     * @param wake true to wake up the body
     */
    public static void nBodyApplyForce(MemorySegment bodyId, MemorySegment force, MemorySegment point, boolean wake) {
        Box2D.b2Body_ApplyForce(bodyId, force, point, wake);
    }

    /**
     * Native implement of {@code b2Body_ApplyForceToCenter}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param force the memory segment of the world force {@link b2Vec2}, usually in newtons (N)
     * @param wake true to wake up the body
     */
    public static void nBodyApplyForceToCenter(MemorySegment bodyId, MemorySegment force, boolean wake) {
        Box2D.b2Body_ApplyForceToCenter(bodyId, force, wake);
    }

    /**
     * Native implement of {@code b2Body_ApplyLinearImpulse}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param impulse the memory segment of the world impulse {@link b2Vec2}, usually in N*s or kg*m/s
     * @param point the memory segment of the point of application {@link b2Vec2} in world space
     * @param wake true to wake up the body
     */
    public static void nBodyApplyLinearImpulse(MemorySegment bodyId, MemorySegment impulse, MemorySegment point, boolean wake) {
        Box2D.b2Body_ApplyLinearImpulse(bodyId, impulse, point, wake);
    }

    /**
     * Native implement of {@code b2Body_ApplyLinearImpulseToCenter}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param impulse the memory segment of the world impulse {@link b2Vec2}, usually in N*s or kg*m/s
     * @param wake true to wake up the body
     */
    public static void nBodyApplyLinearImpulseToCenter(MemorySegment bodyId, MemorySegment impulse, boolean wake) {
        Box2D.b2Body_ApplyLinearImpulseToCenter(bodyId, impulse, wake);
    }

    /**
     * Native implement of {@code b2Body_UpdateMassFromShapes}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     */
    public static void nBodyUpdateMassFromShapes(MemorySegment bodyId) {
        Box2D.b2Body_UpdateMassFromShapes(bodyId);
    }

    /**
     * Native implement of {@code b2Body_ApplyTorque}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param torque the torque about the z-axis (out of the screen), usually in N*m
     * @param wake true to wake up the body
     */
    public static void nBodyApplyTorque(MemorySegment bodyId, float torque, boolean wake) {
        Box2D.b2Body_ApplyTorque(bodyId, torque, wake);
    }

    /**
     * Native implement of {@code b2Body_Disable}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     */
    public static void nBodyDisable(MemorySegment bodyId) {
        Box2D.b2Body_Disable(bodyId);
    }

    /**
     * Native implement of {@code b2Body_Enable}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     */
    public static void nBodyEnable(MemorySegment bodyId) {
        Box2D.b2Body_Enable(bodyId);
    }

    /**
     * Native implement of {@code b2Body_EnableSleeping}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param enableSleep true to allow the body to sleep
     */
    public static void nBodyEnableSleeping(MemorySegment bodyId, boolean enableSleep) {
        Box2D.b2Body_EnableSleeping(bodyId, enableSleep);
    }

    /**
     * Native implement of {@code b2Body_EnableContactRecycling}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param flag true to enable contact recycling
     */
    public static void nBodyEnableContactRecycling(MemorySegment bodyId, boolean flag) {
        Box2D.b2Body_EnableContactRecycling(bodyId, flag);
    }

    /**
     * Native implement of {@code b2Body_SetAngularDamping}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param angularDamping the non-negative angular damping
     */
    public static void nBodySetAngularDamping(MemorySegment bodyId, float angularDamping) {
        Box2D.b2Body_SetAngularDamping(bodyId, angularDamping);
    }

    /**
     * Native implement of {@code b2Body_SetAngularVelocity}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param angularVelocity the angular velocity, in rad/s
     */
    public static void nBodySetAngularVelocity(MemorySegment bodyId, float angularVelocity) {
        Box2D.b2Body_SetAngularVelocity(bodyId, angularVelocity);
    }

    /**
     * Native implement of {@code b2Body_SetAwake}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param awake true to wake the body, false to put it to sleep
     */
    public static void nBodySetAwake(MemorySegment bodyId, boolean awake) {
        Box2D.b2Body_SetAwake(bodyId, awake);
    }

    /**
     * Native implement of {@code b2Body_SetBullet}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param flag true to make the body a bullet
     */
    public static void nBodySetBullet(MemorySegment bodyId, boolean flag) {
        Box2D.b2Body_SetBullet(bodyId, flag);
    }

    /**
     * Native implement of {@code b2Body_SetGravityScale}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param gravityScale the gravity scale, non-dimensional
     */
    public static void nBodySetGravityScale(MemorySegment bodyId, float gravityScale) {
        Box2D.b2Body_SetGravityScale(bodyId, gravityScale);
    }

    /**
     * Native implement of {@code b2Body_SetLinearDamping}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param linearDamping the non-negative linear damping
     */
    public static void nBodySetLinearDamping(MemorySegment bodyId, float linearDamping) {
        Box2D.b2Body_SetLinearDamping(bodyId, linearDamping);
    }

    /**
     * Native implement of {@code b2Body_SetLinearVelocity}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param linearVelocity the memory segment of the linear velocity {@link b2Vec2}, usually in m/s
     */
    public static void nBodySetLinearVelocity(MemorySegment bodyId, MemorySegment linearVelocity) {
        Box2D.b2Body_SetLinearVelocity(bodyId, linearVelocity);
    }

    /**
     * Native implement of {@code b2Body_SetMassData}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param massData the memory segment of the {@link b2MassData} to apply
     */
    public static void nBodySetMassData(MemorySegment bodyId, MemorySegment massData) {
        Box2D.b2Body_SetMassData(bodyId, massData);
    }

    /**
     * Native implement of {@code b2Body_SetMotionLocks}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param locks the memory segment of the {@link b2MotionLocks} to apply
     */
    public static void nBodySetMotionLocks(MemorySegment bodyId, MemorySegment locks) {
        Box2D.b2Body_SetMotionLocks(bodyId, locks);
    }

    /**
     * Native implement of {@code b2Body_SetName}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param name the memory segment of a null-terminated UTF-8 name (at least {@code B2_NAME_LENGTH} + 1 bytes), or {@link MemorySegment#NULL} to clear the name
     */
    public static void nBodySetName(MemorySegment bodyId, MemorySegment name) {
        Box2D.b2Body_SetName(bodyId, name);
    }

    /**
     * Native implement of {@code b2Body_SetSafetyFactor}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param safetyFactor the continuous collision safety factor, non-dimensional
     */
    public static void nBodySetSafetyFactor(MemorySegment bodyId, float safetyFactor) {
        Box2D.b2Body_SetSafetyFactor(bodyId, safetyFactor);
    }

    /**
     * Native implement of {@code b2Body_SetSleepThreshold}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param sleepThreshold the non-negative sleep threshold, usually in m/s
     */
    public static void nBodySetSleepThreshold(MemorySegment bodyId, float sleepThreshold) {
        Box2D.b2Body_SetSleepThreshold(bodyId, sleepThreshold);
    }

    /**
     * Native implement of {@code b2Body_SetTargetTransform}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param target the memory segment of the target {@link b2WorldTransform}
     * @param timeStep the time step over which to reach the target, in seconds
     * @param wake true to wake the body
     */
    public static void nBodySetTargetTransform(MemorySegment bodyId, MemorySegment target, float timeStep, boolean wake) {
        Box2D.b2Body_SetTargetTransform(bodyId, target, timeStep, wake);
    }

    /**
     * Native implement of {@code b2Body_SetTransform}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param position the memory segment of the body origin {@link b2Vec2} in world space
     * @param rotation the memory segment of the {@link b2Rot} in world space
     */
    public static void nBodySetTransform(MemorySegment bodyId, MemorySegment position, MemorySegment rotation) {
        Box2D.b2Body_SetTransform(bodyId, position, rotation);
    }

    /**
     * Native implement of {@code b2Body_SetType}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param type the native {@code b2BodyType} value
     */
    public static void nBodySetType(MemorySegment bodyId, int type) {
        Box2D.b2Body_SetType(bodyId, type);
    }

    /**
     * Native implement of {@code b2Body_SetUserData}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     * @param userData the pointer to the user data, or {@link MemorySegment#NULL} for none
     */
    public static void nBodySetUserData(MemorySegment bodyId, MemorySegment userData) {
        Box2D.b2Body_SetUserData(bodyId, userData);
    }

    /**
     * Native implement of {@code b2DestroyBody}.
     * @param bodyId the memory segment of the {@link b2BodyId}
     */
    public static void nDestroyBody(MemorySegment bodyId) {
        Box2D.b2DestroyBody(bodyId);
    }

    /**
     * Native implement of {@code b2DestroyWorld}.
     * @param worldId the memory segment of the {@link b2WorldId}
     */
    public static void nDestroyWorld(MemorySegment worldId) {
        Box2D.b2DestroyWorld(worldId);
    }

    /**
     * Native implement of {@code b2World_EnableContinuous}.
     * @param worldId the memory segment of the {@link b2WorldId}
     * @param flag true to enable continuous collision
     */
    public static void nWorldEnableContinuous(MemorySegment worldId, boolean flag) {
        Box2D.b2World_EnableContinuous(worldId, flag);
    }

    /**
     * Native implement of {@code b2World_EnableSleeping}.
     * @param worldId the memory segment of the {@link b2WorldId}
     * @param flag true to enable sleeping
     */
    public static void nWorldEnableSleeping(MemorySegment worldId, boolean flag) {
        Box2D.b2World_EnableSleeping(worldId, flag);
    }

    /**
     * Native implement of {@code b2World_EnableWarmStarting}.
     * @param worldId the memory segment of the {@link b2WorldId}
     * @param flag true to enable warm starting
     */
    public static void nWorldEnableWarmStarting(MemorySegment worldId, boolean flag) {
        Box2D.b2World_EnableWarmStarting(worldId, flag);
    }

    /**
     * Native implement of {@code b2World_SetContactTuning}.
     * @param worldId the memory segment of the {@link b2WorldId}
     * @param hertz the contact stiffness, in cycles per second
     * @param dampingRatio the contact bounciness with 1 being critical damping, non-dimensional
     * @param pushSpeed the maximum contact constraint push out speed, usually in m/s
     */
    public static void nWorldSetContactTuning(MemorySegment worldId, float hertz, float dampingRatio, float pushSpeed) {
        Box2D.b2World_SetContactTuning(worldId, hertz, dampingRatio, pushSpeed);
    }

    /**
     * Native implement of {@code b2World_SetContactRecycleDistance}.
     * @param worldId the memory segment of the {@link b2WorldId}
     * @param recycleDistance the contact point recycling distance, usually in meters, set to 0.0 disables recycling
     */
    public static void nWorldSetContactRecycleDistance(MemorySegment worldId, float recycleDistance) {
        Box2D.b2World_SetContactRecycleDistance(worldId, recycleDistance);
    }

    /**
     * Native implement of {@code b2World_SetGravity}.
     * @param worldId the memory segment of the {@link b2WorldId}
     * @param gravity the memory segment of the gravity {@link b2Vec2}, usually in m/s²
     */
    public static void nWorldSetGravity(MemorySegment worldId, MemorySegment gravity) {
        Box2D.b2World_SetGravity(worldId, gravity);
    }

    /**
     * Native implement of {@code b2World_SetHitEventThreshold}.
     * @param worldId the memory segment of the {@link b2WorldId}
     * @param value the hit event threshold, usually in m/s
     */
    public static void nWorldSetHitEventThreshold(MemorySegment worldId, float value) {
        Box2D.b2World_SetHitEventThreshold(worldId, value);
    }

    /**
     * Native implement of {@code b2World_SetMaxLinearSpeed}.
     * @param worldId the memory segment of the {@link b2WorldId}
     * @param maximumLinearSpeed the maximum linear speed, usually in m/s
     */
    public static void nWorldSetMaxLinearSpeed(MemorySegment worldId, float maximumLinearSpeed) {
        Box2D.b2World_SetMaxLinearSpeed(worldId, maximumLinearSpeed);
    }

    /**
     * Native implement of {@code b2World_SetRestitutionThreshold}.
     * @param worldId the memory segment of the {@link b2WorldId}
     * @param value the restitution threshold, usually in m/s
     */
    public static void nWorldSetRestitutionThreshold(MemorySegment worldId, float value) {
        Box2D.b2World_SetRestitutionThreshold(worldId, value);
    }

    /**
     * Native implement of {@code b2World_SetUserData}.
     * @param worldId the memory segment of the {@link b2WorldId}
     * @param userData the pointer to the user data, or {@link MemorySegment#NULL} for none
     */
    public static void nWorldSetUserData(MemorySegment worldId, MemorySegment userData) {
        Box2D.b2World_SetUserData(worldId, userData);
    }

    /**
     * Native implement of {@code b2World_Step}.
     * @param worldId the memory segment of the {@link b2WorldId}
     * @param timeStep the amount of time to simulate in seconds, usually 1/60
     * @param subStepCount the number of sub-steps, increasing it can increase accuracy, usually 4
     */
    public static void nWorldStep(MemorySegment worldId, float timeStep, int subStepCount) {
        Box2D.b2World_Step(worldId, timeStep, subStepCount);
    }
}
