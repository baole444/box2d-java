package org.box2d.dynamics;

import org.box2d.NativeLoader;
import org.box2d.collision.MassData;
import org.box2d.internal.b2BodyId;
import org.box2d.internal.nBox2D;
import org.box2d.math.Rot;
import org.box2d.math.Transform;
import org.box2d.math.Vec2;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.charset.StandardCharsets;

/**
 * A rigid body, created by {@link World#createBody(BodyDef)}.
 * <p>
 * A body is a handle to a body owned by its {@link World}. It is freed by {@link #destroy()} or when its world is destroyed, after which {@link #isValid()} returns false.
 * Calling any other method on a body that is not valid throws {@link IllegalStateException}.
 * <p>
 * While the world is {@link World#isLocked() locked}, Box2D ignores changes made to the body.
 * <p>
 * Two {@link Body} instances are equal when they refer to the same body of the same world.
 * <p>
 * Wrapper for native {@code b2BodyId}.
 */
public final class Body {
    /**
     * The number of bytes Box2D stores for a body name, excluding the null terminator.
     */
    private static final int MaxNameBytes = 31;

    private final World world;
    private final MemorySegment id;

    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    /**
     * Wrap a native body id that belongs to the given world.
     * @param world the owning world
     * @param id the {@code b2BodyId} segment, may be the null id
     * @throws IllegalArgumentException when the id belongs to another world
     */
    Body(World world, MemorySegment id) {
        boolean isNull = b2BodyId.index1(id) == 0;
        if (!isNull && Short.toUnsignedInt(b2BodyId.world0(id)) != world.index()) throw new IllegalArgumentException("Body id does not belong to the given world");
        this.world = world;
        this.id = id;
    }

    /**
     * Destroy this rigid body.
     * <p>
     * This destroys all shapes and joints attached to the body and releases the user data held for it.
     * <p>
     * Does nothing if the body is not valid, or while its world is {@link World#isLocked() locked}.
     */
    public void destroy() {
        if (!isValid() || world.isLocked()) return;
        world.userDataTable().release(UserDataTable.handleOf(nBox2D.nBodyGetUserData(id)));
        nBox2D.nDestroyBody(id);
    }

    /**
     * Check if this body is valid.
     * <p>
     * A body is invalid if it is destroyed, its world is destroyed, or when it was never created, such as requesting while the world is locked.
     * @return true if valid
     */
    public boolean isValid() {
        if (world.isDestroyed() || b2BodyId.index1(id) == 0) return false;
        return nBox2D.nBodyIsValid(id);
    }

    /**
     * Get the world that owns this body.
     * @return the owning world
     */
    public World world() {
        return world;
    }

    /**
     * Get the body type: static, kinematic, or dynamic.
     * @return the body type
     */
    public BodyType type() {
        return BodyType.of(nBox2D.nBodyGetType(checked()));
    }

    /**
     * Change the body type. This is an expensive operation.
     * <p>
     * This automatically updates the mass properties regardless of the automatic mass setting.
     * @param type the new body type
     * @return this
     */
    public Body type(BodyType type) {
        nBox2D.nBodySetType(checked(), type.nativeValue);
        return this;
    }

    /**
     * Get the body name.
     * @return the name, or null if none
     */
    public String name() {
        MemorySegment pointer = nBox2D.nBodyGetName(checked());
        if (pointer.equals(MemorySegment.NULL)) return null;
        String name = pointer.reinterpret(MaxNameBytes + 1).getString(0, StandardCharsets.UTF_8);
        return name.isEmpty() ? null : name;
    }

    /**
     * Set the body name, for debugging.
     * <p>
     * Box2D stores up to 31 bytes of the name encoded in UTF-8, truncated without splitting a character when longer.
     * An empty name is the same as no name.
     * @param name the name, or null to clear it
     * @return this
     */
    public Body name(String name) {
        MemorySegment bodyId = checked();
        try (Arena arena = Arena.ofConfined()) {
            nBox2D.nBodySetName(bodyId, encodeName(arena, name));
        }
        return this;
    }

    /**
     * Get the application specific data of this body.
     * @return the user data, or null if none
     */
    public Object userData() {
        return world.userDataTable().resolve(UserDataTable.handleOf(nBox2D.nBodyGetUserData(checked())));
    }

    /**
     * Set the application specific data of this body.
     * <p>
     * The world keeps a strong reference to the data until it is replaced, the body is destroyed, or the world is destroyed.
     * @param userData the user data, or null to clear it
     * @return this
     * @apiNote Must not be called from inside a world callback.
     */
    public Body userData(Object userData) {
        MemorySegment bodyId = checked();
        UserDataTable table = world.userDataTable();
        table.release(UserDataTable.handleOf(nBox2D.nBodyGetUserData(bodyId)));
        nBox2D.nBodySetUserData(bodyId, UserDataTable.pointerOf(table.register(userData)));
        return this;
    }

    /**
     * Get the world position of the body. This is the location of the body origin.
     * @return a new {@link Vec2} holding the position
     */
    public Vec2 position() {
        return new Vec2(nBox2D.nBodyGetPosition(checked()));
    }

    /**
     * Get the world rotation of this body as a cosine/sine pair (complex number).
     * @return a new {@link Rot} holding the rotation
     */
    public Rot rotation() {
        return new Rot(nBox2D.nBodyGetRotation(checked()));
    }

    /**
     * Get the world transform of this body.
     * @return a new {@link Transform} holding the transform
     */
    public Transform transform() {
        return new Transform(nBox2D.nBodyGetTransform(checked()));
    }

    /**
     * Set the world transform of this body.
     * <p>
     * This acts as a teleport and is fairly expensive. Generally, a body should be created with the intended transform.
     * @param position the new world position of the body origin
     * @param rotation the new world rotation
     * @return this
     */
    public Body transform(Vec2 position, Rot rotation) {
        nBox2D.nBodySetTransform(checked(), position.segment(), rotation.segment());
        return this;
    }

    /**
     * Get a local point on this body given a world point.
     * @param worldPoint the point in world coordinates
     * @return a new {@link Vec2} holding the point in body local coordinates
     */
    public Vec2 localPoint(Vec2 worldPoint) {
        return new Vec2(nBox2D.nBodyGetLocalPoint(checked(), worldPoint.segment()));
    }

    /**
     * Get a world point on this body given a local point.
     * @param localPoint the point in body local coordinates
     * @return a new {@link Vec2} holding the point in world coordinates
     */
    public Vec2 worldPoint(Vec2 localPoint) {
        return new Vec2(nBox2D.nBodyGetWorldPoint(checked(), localPoint.segment()));
    }

    /**
     * Get a local vector on this body given a world vector.
     * @param worldVector the vector in world coordinates
     * @return a new {@link Vec2} holding the vector in body local coordinates
     */
    public Vec2 localVector(Vec2 worldVector) {
        return new Vec2(nBox2D.nBodyGetLocalVector(checked(), worldVector.segment()));
    }

    /**
     * Get a world vector on this body given a local vector.
     * @param localVector the vector in body local coordinates
     * @return a new {@link Vec2} holding the vector in world coordinates
     */
    public Vec2 worldVector(Vec2 localVector) {
        return new Vec2(nBox2D.nBodyGetWorldVector(checked(), localVector.segment()));
    }

    /**
     * Get the linear velocity of this body's centre of mass, usually in m/s.
     * @return a new {@link Vec2} holding the linear velocity
     */
    public Vec2 linearVelocity() {
        return new Vec2(nBox2D.nBodyGetLinearVelocity(checked()));
    }

    /**
     * Set the linear velocity of this body, usually in m/s.
     * @param linearVelocity the new linear velocity
     * @return this
     */
    public Body linearVelocity(Vec2 linearVelocity) {
        nBox2D.nBodySetLinearVelocity(checked(), linearVelocity.segment());
        return this;
    }

    /**
     * Get the angular velocity of this body in rad/s.
     * @return the angular velocity
     */
    public float angularVelocity() {
        return nBox2D.nBodyGetAngularVelocity(checked());
    }

    /**
     * Set the angular velocity of this body in rad/s.
     * @param angularVelocity the new angular velocity
     * @return this
     */
    public Body angularVelocity(float angularVelocity) {
        nBox2D.nBodySetAngularVelocity(checked(), angularVelocity);
        return this;
    }

    /**
     * Set the velocity to reach the given transform after a given time step.
     * <p>
     * The result will be close but may not be exact. This is meant for kinematic bodies.
     * The target is not applied if the velocity would be below the sleep threshold and the body is currently asleep.
     * @param target the target transform
     * @param timeStep the time step over which to reach the target
     * @return this
     */
    public Body setTargetTransform(Transform target, float timeStep) {
        nBox2D.nBodySetTargetTransform(checked(), target.segment(), timeStep);
        return this;
    }

    /**
     * Get the linear velocity of a local point attached to this body, usually in m/s.
     * @param localPoint the point in body local coordinates
     * @return a new {@link Vec2} holding the point velocity
     */
    public Vec2 localPointVelocity(Vec2 localPoint) {
        return new Vec2(nBox2D.nBodyGetLocalPointVelocity(checked(), localPoint.segment()));
    }

    /**
     * Get the linear velocity of a world point attached to this body, usually in m/s.
     * @param worldPoint the point in world coordinates
     * @return a new {@link Vec2} holding the point velocity
     */
    public Vec2 worldPointVelocity(Vec2 worldPoint) {
        return new Vec2(nBox2D.nBodyGetWorldPointVelocity(checked(), worldPoint.segment()));
    }

    /**
     * Apply a force at a world point.
     * <p>
     * If the force is not applied at the centre of mass, it will generate a torque and affect the angular velocity.
     * This optionally wakes up the body. The force is ignored if the body is not awake.
     * @param force the world force vector, usually in newtons (N)
     * @param point the world position of the point of application
     * @param wake option to wake up the body
     * @return this
     */
    public Body applyForce(Vec2 force, Vec2 point, boolean wake) {
        nBox2D.nBodyApplyForce(checked(), force.segment(), point.segment(), wake);
        return this;
    }

    /**
     * Apply a force to the centre of mass.
     * <p>
     * This optionally wakes up the body. The force is ignored if the body is not awake.
     * @param force the world force vector, usually in newtons (N)
     * @param wake option to wake up the body
     * @return this
     */
    public Body applyForceToCenter(Vec2 force, boolean wake) {
        nBox2D.nBodyApplyForceToCenter(checked(), force.segment(), wake);
        return this;
    }

    /**
     * Apply a torque.
     * <p>
     * This affects the angular velocity without affecting the linear velocity.
     * This optionally wakes the body. The torque is ignored if the body is not awake.
     * @param torque about the z-axis (out of the screen), usually in N*m
     * @param wake option to wake up the body
     * @return this
     */
    public Body applyTorque(float torque, boolean wake) {
        nBox2D.nBodyApplyTorque(checked(), torque, wake);
        return this;
    }

    /**
     * Apply an impulse at a point.
     * <p>
     * This immediately modifies the velocity. It also modifies the angular velocity if the point of application is not at the centre of mass.
     * This optionally wakes the body. The impulse is ignored if the body is not awake.
     * @param impulse the world impulse vector, usually in N*s or kg*m/s
     * @param point the world position of the point of application
     * @param wake option to wake up the body
     * @return this
     * @apiNote This should be used for one-shot impulses. If a steady force is needed, use a force instead, which will work better with the sub-stepping solver.
     */
    public Body applyLinearImpulse(Vec2 impulse, Vec2 point, boolean wake) {
        nBox2D.nBodyApplyLinearImpulse(checked(), impulse.segment(), point.segment(), wake);
        return this;
    }

    /**
     * Apply an impulse to the centre of mass.
     * <p>
     * This immediately modifies the velocity. This optionally wakes the body. The impulse is ignored if the body is not awake.
     * @param impulse the world impulse vector, usually in N*s or kg*m/s
     * @param wake option to wake up the body
     * @return this
     * @apiNote This should be used for one-shot impulses. If a steady force is needed, use a force instead, which will work better with the sub-stepping solver.
     */
    public Body applyLinearImpulseToCenter(Vec2 impulse, boolean wake) {
        nBox2D.nBodyApplyLinearImpulseToCenter(checked(), impulse.segment(), wake);
        return this;
    }

    /**
     * Apply an angular impulse.
     * <p>
     * The impulse is ignored if the body is not awake. This optionally wakes the body.
     * @param impulse the angular impulse, usually in units of kg*m*m/s
     * @param wake option to wake up the body
     * @return this
     * @apiNote This should be used for one-shot impulses. If a steady torque is needed, use a torque instead, which will work better with the sub-stepping solver.
     */
    public Body applyAngularImpulse(float impulse, boolean wake) {
        nBox2D.nBodyApplyAngularImpulse(checked(), impulse, wake);
        return this;
    }

    /**
     * Get the mass of the body, usually in kilograms.
     * @return the mass
     */
    public float mass() {
        return nBox2D.nBodyGetMass(checked());
    }

    /**
     * Get the rotational inertia of the body, usually in kg*m².
     * @return the rotational inertia
     */
    public float rotationalInertia() {
        return nBox2D.nBodyGetRotationalInertia(checked());
    }

    /**
     * Get the centre of mass position of the body in local space.
     * @return a new {@link Vec2} holding the local centre of mass
     */
    public Vec2 localCenterOfMass() {
        return new Vec2(nBox2D.nBodyGetLocalCenterOfMass(checked()));
    }

    /**
     * Get the centre of mass position of the body in world space.
     * @return a new {@link Vec2} holding the world centre of mass
     */
    public Vec2 worldCenterOfMass() {
        return new Vec2(nBox2D.nBodyGetWorldCenterOfMass(checked()));
    }

    /**
     * Get the mass data for this body.
     * @return a new {@link MassData} holding the mass data
     */
    public MassData massData() {
        return new MassData(nBox2D.nBodyGetMassData(checked()));
    }

    /**
     * Override the body's mass properties.
     * <p>
     * Normally this is computed automatically using the shape geometry and density. This information is lost if a shape is added or removed or if the body type changes.
     * <p>
     * If motion locks are used to fix rotation then the rotational inertia value is ignored and rotational inertia remains fixed at zero.
     * @param massData the mass data to apply
     * @return this
     */
    public Body massData(MassData massData) {
        nBox2D.nBodySetMassData(checked(), massData.segment());
        return this;
    }

    /**
     * This updates the mass properties to the sum of the mass properties of the shapes.
     * <p>
     * This normally does not need to be called unless {@link #massData(MassData)} was called to override the mass, and it needs to be reset.
     * This may also be used when automatic mass computation has been disabled.
     * @return this
     * @apiNote This should be called regardless of body type. Note that sensor shapes may have mass.
     */
    public Body applyMassFromShapes() {
        nBox2D.nBodyApplyMassFromShapes(checked());
        return this;
    }


    /**
     * Get the current linear damping.
     * @return the linear damping
     */
    public float linearDamping() {
        return nBox2D.nBodyGetLinearDamping(checked());
    }

    /**
     * Adjust the linear damping. Normally this is set in {@link BodyDef} before creation.
     * @param linearDamping the non-negative linear damping
     * @return this
     */
    public Body linearDamping(float linearDamping) {
        nBox2D.nBodySetLinearDamping(checked(), linearDamping);
        return this;
    }

    /**
     * Get the current angular damping.
     * @return the angular damping
     */
    public float angularDamping() {
        return nBox2D.nBodyGetAngularDamping(checked());
    }

    /**
     * Adjust the angular damping. Normally this is set in {@link BodyDef} before creation.
     * @param angularDamping the non-negative angular damping
     * @return this
     */
    public Body angularDamping(float angularDamping) {
        nBox2D.nBodySetAngularDamping(checked(), angularDamping);
        return this;
    }

    /**
     * Get the current gravity scale.
     * @return the gravity scale
     */
    public float gravityScale() {
        return nBox2D.nBodyGetGravityScale(checked());
    }

    /**
     * Adjust the gravity scale. Normally this is set in {@link BodyDef} before creation.
     * @param gravityScale the gravity scale, non-dimensional
     * @return this
     */
    public Body gravityScale(float gravityScale) {
        nBox2D.nBodySetGravityScale(checked(), gravityScale);
        return this;
    }

    /**
     * Check if this body is awake.
     * @return true if awake
     */
    public boolean awake() {
        return nBox2D.nBodyIsAwake(checked());
    }

    /**
     * Wake a body from sleep.
     * <p>
     * This wakes the entire island the body is touching.
     * @param flag true to wake, false to put to sleep
     * @return this
     * @apiNote Putting a body to sleep will put the entire island of bodies touching this body to sleep, which can be expensive and possibly unintuitive.
     */
    public Body awake(boolean flag) {
        nBox2D.nBodySetAwake(checked(), flag);
        return this;
    }

    /**
     * Check if this body is allowed to sleep.
     * @return true if sleeping is enabled
     */
    public boolean sleepEnabled() {
        return nBox2D.nBodyIsSleepEnabled(checked());
    }

    /**
     * Enable or disable sleeping for this body.
     * <p>
     * If sleeping is disabled the body will wake (and the entire island).
     * @param flag true to allow sleeping
     * @return this
     */
    public Body sleepEnabled(boolean flag) {
        nBox2D.nBodyEnableSleep(checked(), flag);
        return this;
    }

    /**
     * Get the sleep threshold, usually in m/s.
     * @return the sleep threshold
     */
    public float sleepThreshold() {
        return nBox2D.nBodyGetSleepThreshold(checked());
    }

    /**
     * Set the sleep threshold, usually in me/s.
     * @param sleepThreshold the non-negative sleep threshold
     * @return this
     */
    public Body sleepThreshold(float sleepThreshold) {
        nBox2D.nBodySetSleepThreshold(checked(), sleepThreshold);
        return this;
    }

    /**
     * Check if this body is enabled.
     * @return true if enabled
     */
    public boolean enabled() {
        return nBox2D.nBodyIsEnabled(checked());
    }

    /**
     * Disable a body by removing it completely from the simulation. This is expensive.
     * @return this
     */
    public Body disable() {
        nBox2D.nBodyDisable(checked());
        return this;
    }

    /**
     * Enable a body by adding it to the simulation. This is expensive.
     * @return this
     */
    public Body enable() {
        nBox2D.nBodyEnable(checked());
        return this;
    }

    /**
     * Check if this body has fixed rotation.
     * @return true if rotation is fixed
     */
    public boolean fixedRotation() {
        return nBox2D.nBodyIsFixedRotation(checked());
    }

    /**
     * Set this body to have fixed rotation.
     * <p>
     * This causes the mass to be reset in all cases.
     * @param flag true to fix rotation
     * @return this
     */
    public Body fixedRotation(boolean flag) {
        nBox2D.nBodySetFixedRotation(checked(), flag);
        return this;
    }

    /**
     * Check if this body is a bullet.
     * @return true if this is a bullet
     */
    public boolean bullet() {
        return nBox2D.nBodyIsBullet(checked());
    }

    /**
     * Set this body to be a bullet.
     * <p>
     * A bullet does continuous collision detection against dynamic and kinematic bodies (but not other bullets). This only works on dynamic bodies.
     * @param flag true to make this a bullet
     * @return this
     */
    public Body bullet(boolean flag) {
        nBox2D.nBodySetBullet(checked(), flag);
        return this;
    }

    @Override
    public String toString() {
        return String.format("Body{index=%d, world=%d, generation=%d, valid=%b}", b2BodyId.index1(id) - 1,
                Short.toUnsignedInt(b2BodyId.world0(id)), Short.toUnsignedInt(b2BodyId.generation(id)), isValid());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Body body)) return false;
        return world == body.world
                && b2BodyId.index1(id) == b2BodyId.index1(body.id)
                && b2BodyId.world0(id) == b2BodyId.world0(body.id)
                && b2BodyId.generation(id) == b2BodyId.generation(body.id);
    }

    @Override
    public int hashCode() {
        int result = b2BodyId.index1(id);
        result = 31 * result + b2BodyId.world0(id);
        result = 31 * result + b2BodyId.generation(id);
        return result;
    }

    /**
     * Encode a body name.
     * <p>
     * Box2D stores up to 31 bytes of the name encoded in UTF-8, truncated without splitting a character when longer.
     * This is stored in a zero-filled 32 byte segment (Box2D always copies 31 bytes from the given pointer).
     * @param arena the arena to allocate in
     * @param name the name, can be null
     * @return the name segment, or {@link MemorySegment#NULL} for a null name
     * @apiNote This ensured that multibyte character is never spilt by backing off over UTF-8 continuation bytes
     */
    static MemorySegment encodeName(Arena arena, String name) {
        if (name == null) return MemorySegment.NULL;
        byte[] bytes = name.getBytes(StandardCharsets.UTF_8);
        int length = Math.min(bytes.length, MaxNameBytes);
        if (length < bytes.length) {
            while (length > 0 && (bytes[length] & 0xC0) == 0x80) length--;
        }
        MemorySegment segment = arena.allocate(MaxNameBytes + 1);
        MemorySegment.copy(bytes, 0, segment, ValueLayout.JAVA_BYTE, 0, length);
        return segment;
    }

    /**
     * Get the memory segment of this body's ID.
     * @return the underlying {@code b2BodyId} segment
     */
    MemorySegment id() {
        return id;
    }

    private MemorySegment checked() {
        if (!isValid()) throw new IllegalStateException("Invalid body: it or its world has been destroyed");
        return id;
    }
}
