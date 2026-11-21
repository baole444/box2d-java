package org.box2d.dynamics;

import org.box2d.NativeLoader;
import org.box2d.collision.*;
import org.box2d.internal.b2ShapeId;
import org.box2d.internal.nBox2D;
import org.box2d.math.Vec2;

import java.lang.foreign.MemorySegment;

/**
 * A shape bind raw geometry to a body and hold material properties including friction and restitution.
 * Created by {@code Body.create*Shape(ShapeDef, Geometry)}, such as {@link Body#createPolygonShape(ShapeDef, Polygon)}.
 * <p>
 * A handle to a shape owned by its {@link Body}. Freed by {@link #destroy()} or when its body / world is destroyed, after which {@link #isValid()} returns false.
 * Calling any other method on a shape that is not valid throws {@link IllegalStateException}.
 * <p>
 * While the world is {@link World#isLocked() locked}, Box2D ignores changes made to the shape.
 * <p>
 * Event reports condition for two involved shapes:
 * <ul>
 *     <li>Contact, hit and pre-solve events: flags enabled on either shape (ignored for sensors).</li>
 *     <li>Sensor events: flags enabled on both the sensor shape and the visiting shape.</li>
 * </ul>
 * <p>
 * Two {@link Shape} instances are equal when they refer to the same shape of the same world.
 * <p>
 * Wrapper for native {@code b2ShapeId}.
 */
public final class Shape {
    private final World world;
    private final MemorySegment id;

    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    /**
     * Wrap a native shape id that belongs to the given world.
     * @param world the owning world
     * @param id the {@code b2ShapeId} segment, may be the null id
     * @throws IllegalArgumentException when the id belongs to another world
     */
    Shape(World world, MemorySegment id) {
        boolean isNull = b2ShapeId.index1(id) == 0;
        if (!isNull && Short.toUnsignedInt(b2ShapeId.world0(id)) != world.index()) throw new IllegalArgumentException("Shape id does not belong to the given world");
        this.world = world;
        this.id = id;
    }

    /**
     * Destroy this shape and update the mass properties of its body.
     * <p>
     * Does nothing if the shape is not valid, or while its world is {@link World#isLocked() locked}.
     * @see #destroy(boolean)
     */
    public void destroy() {
        destroy(true);
    }

    /**
     * Destroy this shape and release the user data held for it.
     * <p>
     * The body mass update may be deferred, which can improve performance if several shapes on a body are destroyed at once.
     * Then {@link Body#updateMassFromShapes()} must be called afterwards.
     * <p>
     * Does nothing if the shape is not valid, or while its world is {@link World#isLocked() locked}.
     * @param updateBodyMass true to update the mass properties of the body
     */
    public void destroy(boolean updateBodyMass) {
        if (!isValid() || world.isLocked()) return;
        long handle = UserDataTable.handleOf(nBox2D.nShapeGetUserData(id));
        nBox2D.nDestroyShape(id, updateBodyMass);
        if (!nBox2D.nShapeIsValid(id)) world.userDataTable().release(handle);
    }

    /**
     * Check if this shape is valid.
     * <p>
     * A shape is invalid if it is destroyed, its body / world is destroyed, or when it was never created,
     * such as requesting while the world is locked, from an invalid definition or geometry.
     * @return true if valid
     */
    public boolean isValid() {
        if (world.isDestroyed() || b2ShapeId.index1(id) == 0) return false;
        return nBox2D.nShapeIsValid(id);
    }

    /**
     * Get the world that owns this shape.
     * @return the owning world
     */
    public World world() {
        return world;
    }

    /**
     * Get the body that this shape is attached to.
     * @return the owning {@link Body}
     */
    public Body body() {
        return new Body(world, nBox2D.nShapeGetBody(checked()));
    }

    /**
     * Get the type of this shape.
     * @return the shape type
     */
    public ShapeType type() {
        return ShapeType.of(nBox2D.nShapeGetType(checked()));
    }

    /**
     * Check if this shape is a sensor.
     * <p>
     * It is not possible to change a shape from sensor to solid dynamically because this breaks the contract for sensor events.
     * @return true if a sensor
     */
    public boolean sensor() {
        return nBox2D.nShapeIsSensor(checked());
    }

    /**
     * Get the application specific data of this shape.
     * <p>
     * This is useful when a shape is obtained from an event or query.
     * @return the user data, or null if none
     */
    public Object userData() {
        return world.userDataTable().resolve(UserDataTable.handleOf(nBox2D.nShapeGetUserData(checked())));
    }

    /**
     * Set the application specific data of this shape.
     * <p>
     * The world keeps a strong reference to the data until it is replaced, the shape is destroyed, or the world is destroyed.
     * @param userData the user data, or null to clear it
     * @return this
     * @apiNote Must not be called from inside a world callback.
     */
    public Shape userData(Object userData) {
        MemorySegment shapeId = checked();
        UserDataTable table = world.userDataTable();
        table.release(UserDataTable.handleOf(nBox2D.nShapeGetUserData(shapeId)));
        nBox2D.nShapeSetUserData(shapeId, UserDataTable.pointerOf(table.register(userData)));
        return this;
    }

    /**
     * Get the density of this shape, usually in kg/m².
     * @return the density
     */
    public float density() {
        return nBox2D.nShapeGetDensity(checked());
    }

    /**
     * Set the mass density of this shape, usually in kg/m², and update the mass properties of its body.
     * @param density the non-negative density
     * @return this
     * @see #density(float, boolean)
     */
    public Shape density(float density) {
        return density(density, true);
    }

    /**
     * Set the mass density of this shape, usually in kg/m². This optionally updates the mass properties of its body.
     * <p>
     * When not updated, {@link Body#updateMassFromShapes()} applies the new density later.
     * @param density the non-negative density
     * @param updateBodyMass true to update the mass properties of the body
     * @return this
     */
    public Shape density(float density, boolean updateBodyMass) {
        nBox2D.nShapeSetDensity(checked(), density, updateBodyMass);
        return this;
    }

    /**
     * Get the friction of this shape.
     * @return the friction coefficient
     */
    public float friction() {
        return nBox2D.nShapeGetFriction(checked());
    }

    /**
     * Set the friction of this shape.
     * @param friction the non-negative friction coefficient
     * @return this
     */
    public Shape friction(float friction) {
        nBox2D.nShapeSetFriction(checked(), friction);
        return this;
    }

    /**
     * Get the restitution (bounciness) of this shape.
     * @return the coefficient of restitution
     */
    public float restitution() {
        return nBox2D.nShapeGetRestitution(checked());
    }

    /**
     * Set the restitution (bounciness) of this shape.
     * @param restitution the non-negative coefficient of restitution
     * @return this
     */
    public Shape restitution(float restitution) {
        nBox2D.nShapeSetRestitution(checked(), restitution);
        return this;
    }

    /**
     * Get the user material identifier of this shape.
     * @return the 64-bit identifier, to be read as unsigned (see {@link Long#toUnsignedString(long)})
     * @see SurfaceMaterial#userMaterialId(long)
     */
    public long userMaterial() {
        return nBox2D.nShapeGetUserMaterial(checked());
    }

    /**
     * Set the user material identifier of this shape.
     * @param material the 64-bit identifier, read as unsigned
     * @return this
     * @see SurfaceMaterial#userMaterialId(long)
     */
    public Shape userMaterial(long material) {
        nBox2D.nShapeSetUserMaterial(checked(), material);
        return this;
    }

    /**
     * Get the surface material of this shape.
     * @return a new {@link SurfaceMaterial} holding the material
     */
    public SurfaceMaterial surfaceMaterial() {
        return new SurfaceMaterial(nBox2D.nShapeGetSurfaceMaterial(checked()));
    }

    /**
     * Set the surface material of this shape.
     * @param material the material to apply, with a non-negative friction, restitution and rolling resistance
     * @return this
     */
    public Shape surfaceMaterial(SurfaceMaterial material) {
        nBox2D.nShapeSetSurfaceMaterial(checked(), material.segment());
        return this;
    }

    /**
     * Get the collision filter of this shape.
     * @return a new {@link Filter} holding the filter
     */
    public Filter filter() {
        return new Filter(nBox2D.nShapeGetFilter(checked()));
    }

    /**
     * Set the collision filter of this shape.
     * <p>
     * This is almost as expensive as recreating the shape. This may cause contacts to be immediately destroyed.
     * However, contacts are not created until the next world step. Sensor overlap state is also not updated until the next world step.
     * @param filter the filter to apply
     * @return this
     */
    public Shape filter(Filter filter) {
        nBox2D.nShapeSetFilter(checked(), filter.segment());
        return this;
    }

    /**
     * Check if sensor events are enabled for this shape.
     * @return true if enabled
     */
    public boolean sensorEventsEnabled() {
        return nBox2D.nShapeAreSensorEventsEnabled(checked());
    }

    /**
     * Enable sensor events for this shape. This applies to sensors and non-sensors.
     * <p>
     * Both shapes involved must have this flag set to true.
     * @param flag true to enable sensor events
     * @return this
     */
    public Shape sensorEventsEnabled(boolean flag) {
        nBox2D.nShapeEnableSensorEvents(checked(), flag);
        return this;
    }

    /**
     * Check if contact events are enabled for this shape.
     * @return true if enabled
     */
    public boolean contactEventsEnabled() {
        return nBox2D.nShapeAreContactEventsEnabled(checked());
    }

    /**
     * Enable contact events for this shape.
     * <p>
     * Only applies to kinematic and dynamic bodies. Ignored for sensors.
     * @param flag true to enable contact events
     * @return this
     * @apiNote Changing this at run-time may lead to lost begin and end touch events.
     */
    public Shape contactEventsEnabled(boolean flag) {
        nBox2D.nShapeEnableContactEvents(checked(), flag);
        return this;
    }

    /**
     * Check if hit events are enabled for this shape.
     * @return true if enabled
     */
    public boolean hitEventsEnabled() {
        return nBox2D.nShapeAreHitEventsEnabled(checked());
    }

    /**
     * Enable contact hit events for this shape.
     * <p>
     * Ignored for sensors.
     * @param flag true to enable hit events
     * @return this
     */
    public Shape hitEventsEnabled(boolean flag) {
        nBox2D.nShapeEnableHitEvents(checked(), flag);
        return this;
    }

    /**
     * Check if pre-solve contact events are enabled for this shape.
     * @return true if enabled
     */
    public boolean preSolveEventsEnabled() {
        return nBox2D.nShapeArePreSolveEventsEnabled(checked());
    }

    /**
     * Enable pre-solve contact events for this shape.
     * <p>
     * Only applies to dynamic bodies. These are expensive and must be carefully handled due to multithreading. Ignored for sensors.
     * @param flag true to enable pre-solve events
     * @return this
     */
    public Shape preSolveEventsEnabled(boolean flag) {
        nBox2D.nShapeEnablePreSolveEvents(checked(), flag);
        return this;
    }

    /**
     * Test a world point for overlap with this shape.
     * @param point the point in world coordinates
     * @return true if the point is inside this shape
     */
    public boolean testPoint(Vec2 point) {
        return nBox2D.nShapeTestPoint(checked(), point.segment());
    }

    /**
     * Ray cast a single shape.
     * <p>
     * The ray starts at the origin and extends by the translation. The output point is a world position.
     * @param origin the ray origin in world coordinates
     * @param translation the ray translation, its end point is {@code origin + translation}
     * @return a new {@link CastOutput}, whose point is a world position
     */
    public CastOutput castRay(Vec2 origin, Vec2 translation) {
        return new CastOutput(nBox2D.nShapeCastRay(checked(), origin.segment(), translation.segment()));
    }

    /**
     * Get the current world bounding box of this shape.
     * <p>
     * Box2D pads the box by a small speculative margin, so it is slightly larger than the geometry.
     * @return a new {@link AABB} holding the bounds
     */
    public AABB bounds() {
        return new AABB(nBox2D.nShapeGetBounds(checked()));
    }

    /**
     * Compute the mass data of this shape, from its geometry and density.
     * @return a new {@link MassData} holding the mass data
     */
    public MassData computeMassData() {
        return new MassData(nBox2D.nShapeComputeMassData(checked()));
    }

    /**
     * Get the closest point on this shape to a target point.
     * @param target the target point in world coordinates
     * @return a new {@link Vec2} holding the closest point in world coordinates
     */
    public Vec2 closestPoint(Vec2 target) {
        return new Vec2(nBox2D.nShapeGetClosestPoint(checked(), target.segment()));
    }

    /**
     * Get a copy of this shape's geometry, whichever its type.
     * <p>
     * Use this with pattern matching to handle each geometry type, see {@link Geometry}.
     * @return a new {@link Circle}, {@link Capsule}, {@link Segment} or {@link Polygon}
     * @throws UnsupportedOperationException if this is a chain segment shape
     */
    public Geometry geometry() {
        MemorySegment shapeId = checked();
        ShapeType type = ShapeType.of(nBox2D.nShapeGetType(shapeId));
        return switch (type) {
            case CircleShape -> new Circle(nBox2D.nShapeGetCircle(shapeId));
            case CapsuleShape -> new Capsule(nBox2D.nShapeGetCapsule(shapeId));
            case SegmentShape -> new Segment(nBox2D.nShapeGetSegment(shapeId));
            case PolygonShape -> new Polygon(nBox2D.nShapeGetPolygon(shapeId));
            case ChainSegmentShape -> throw new UnsupportedOperationException("Chain segment geometry is not supported yet");
        };
    }

    /**
     * Get a copy of this shape's circle.
     * @return a new {@link Circle} holding the geometry
     * @throws IllegalStateException if this is not a circle shape
     */
    public Circle circle() {
        return new Circle(nBox2D.nShapeGetCircle(checkedType(ShapeType.CircleShape)));
    }

    /**
     * Change this shape to be a circle, or update the current circle.
     * @param circle the circle, with a non-negative radius
     * @return this
     * @apiNote This does not modify the mass properties.
     * @see Body#updateMassFromShapes() Update mass from shapes
     */
    public Shape circle(Circle circle) {
        nBox2D.nShapeSetCircle(checked(), circle.segment());
        return this;
    }

    /**
     * Get a copy of this shape's capsule.
     * @return a new {@link Capsule} holding the geometry
     * @throws IllegalStateException if this is not a capsule shape
     */
    public Capsule capsule() {
        return new Capsule(nBox2D.nShapeGetCapsule(checkedType(ShapeType.CapsuleShape)));
    }

    /**
     * Change this shape to be a capsule, or update the current capsule.
     * <p>
     * A capsule with distance between its centres less than {@value org.box2d.Box2D#LinearSlop} is ignored.
     * @param capsule the capsule, with a non-negative radius
     * @return this
     * @apiNote This does not modify the mass properties.
     * @see Body#updateMassFromShapes() Update mass from shapes
     */
    public Shape capsule(Capsule capsule) {
        nBox2D.nShapeSetCapsule(checked(), capsule.segment());
        return this;
    }

    /**
     * Get a copy of this shape's line segment.
     * @return a new {@link Segment} holding the geometry
     * @throws IllegalStateException if this is not a segment shape
     */
    public Segment segment() {
        return new Segment(nBox2D.nShapeGetSegment(checkedType(ShapeType.SegmentShape)));
    }

    /**
     * Change this shape to be a line segment, or update the current segment.
     * @param segment the line segment
     * @return this
     */
    public Shape segment(Segment segment) {
        nBox2D.nShapeSetSegment(checked(), segment.segment());
        return this;
    }

    /**
     * Get a copy of this shape's convex polygon.
     * @return a new {@link Polygon} holding the geometry
     * @throws IllegalStateException if this is not a polygon shape
     */
    public Polygon polygon() {
        return new Polygon(nBox2D.nShapeGetPolygon(checkedType(ShapeType.PolygonShape)));
    }

    /**
     * Change this shape to be a polygon, or update the current polygon.
     * <p>
     * This does not modify the mass properties, see {@link Body#updateMassFromShapes()}.
     * @param polygon the polygon
     * @return this
     */
    public Shape polygon(Polygon polygon) {
        nBox2D.nShapeSetPolygon(checked(), polygon.segment());
        return this;
    }

    @Override
    public String toString() {
        return String.format("Shape{index=%d, world=%d, generation=%d, valid=%b}", b2ShapeId.index1(id) - 1, Short.toUnsignedInt(b2ShapeId.world0(id)), Short.toUnsignedInt(b2ShapeId.generation(id)), isValid());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shape shape)) return false;
        return world == shape.world
                && b2ShapeId.index1(id) == b2ShapeId.index1(shape.id)
                && b2ShapeId.world0(id) == b2ShapeId.world0(shape.id)
                && b2ShapeId.generation(id) == b2ShapeId.generation(shape.id);
    }

    @Override
    public int hashCode() {
        int result = b2ShapeId.index1(id);
        result = 31 * result + b2ShapeId.world0(id);
        result = 31 * result + b2ShapeId.generation(id);
        return result;
    }

    /**
     * Get the memory segment of this shape's ID.
     * @return the underlying {@code b2ShapeId} segment
     */
    MemorySegment id() {
        return id;
    }

    private MemorySegment checked() {
        if (!isValid()) throw new IllegalStateException("Invalid shape: it, its body or its world has been destroyed");
        return id;
    }

    private MemorySegment checkedType(ShapeType expected) {
        MemorySegment shapeId = checked();
        ShapeType actual = ShapeType.of(nBox2D.nShapeGetType(shapeId));
        if (actual != expected) throw new IllegalStateException("Shape is a " + actual + ", not a " + expected);
        return shapeId;
    }
}
