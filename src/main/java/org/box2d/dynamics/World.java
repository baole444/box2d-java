package org.box2d.dynamics;

import org.box2d.NativeLoader;
import org.box2d.internal.*;
import org.box2d.math.Vec2;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * The world contains all bodies and joints. It manages all aspects of the simulation and allows for async queries (such as AABB and ray-casts).
 * <p>
 * A world that is no longer needed must be {@link #destroy() destroyed}.
 * After which, all the {@link Body Bodies} within it will become {@link Body#isValid() invalid}.
 * <p>
 * Wrapper for native {@code b2WorldId}.
 */
public final class World {
    private final MemorySegment id;
    private final UserDataTable userDataTable = new UserDataTable();
    private boolean destroyed = false;
    private boolean locked = false;

    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    /**
     * Create a world for rigid body simulation with Box2D's default world definition.
     * @see #World(WorldDef)
     */
    public World() {
        this(new WorldDef());
    }

    /**
     * Create a world for rigid body simulation.
     * <p>
     * A world contains bodies, shapes, and constraints. Up to 128 worlds can be created.
     * Each world is completely independent and may be simulated in parallel.
     * @param def the world definition
     * @apiNote The provided definition is copied, so it can be changed and reused later.
     */
    public World(WorldDef def) {
        MemorySegment defSegment = def.segment();
        long handle = userDataTable.register(def.userData());
        b2WorldDef.userData(defSegment, UserDataTable.pointerOf(handle));
        try {
            id = nBox2D.nCreateWorld(defSegment);
        } finally {
            b2WorldDef.userData(defSegment, MemorySegment.NULL);
        }
    }

    /**
     * Destroy this world and everything in it.
     * <p>
     * Every handle obtained from this world becomes invalid and the user data it held is released.
     * <p>
     * Does nothing if the world is already destroyed, or while it is locked inside {@link #step(float, int)}.
     */
    public void destroy() {
        if (!isValid() || locked) return;
        nBox2D.nDestroyWorld(id);
        destroyed = true;
        userDataTable.clear();
    }

    /**
     * Check if this world is valid. A world is invalid when destroyed.
     * @return true if valid
     */
    public boolean isValid() {
        return !destroyed && nBox2D.nWorldIsValid(id);
    }

    /**
     * Simulate a world for one time step.
     * <p>
     * This performs collision detection, integration, and constraint solution.
     * @param timeStep the amount of time to simulate, this should be a fixed number, usually 1/60.
     * @param subStepCount the number of sub-steps, increasing the sub-step count can increase accuracy, usually 4.
     */
    public void step(float timeStep, int subStepCount) {
        MemorySegment worldId = checked();
        locked = true;
        try {
            nBox2D.nWorldStep(worldId, timeStep, subStepCount);
        } finally {
            locked = false;
        }
    }

    /**
     * Check if this world is locked, which is the case while it is inside {@link #step(float, int)}.
     * <p>
     * Box2D ignores changes made to a locked world. Creating a body during lock returns a handle that is {@link Body#isValid() invalid}.
     * @return true if locked
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Create a rigid body from the given definition.
     * <p>
     * The body belongs to this world, it is freed when it is {@link Body#destroy() destroyed} or when this world is destroyed.
     * @param def the body definition
     * @return the new {@link Body} handle
     * @apiNote
     * The definition is copied, so it can be changed and reused later.
     * <p>
     * No new body is created while this world is {@link #isLocked() locked}, making the returned handle {@link Body#isValid() invalid}.
     * <p>
     * Box2D also rejects a definition with an invalid value and logs it: a non-finite position, rotation, velocity or gravity scale,
     * or a negative or non-finite damping, sleep threshold or safety factor. The returned handle is then invalid too.
     */
    public Body createBody(BodyDef def) {
        MemorySegment worldId = checked();
        MemorySegment defSegment = def.segment();
        long handle = userDataTable.register(def.userData());
        MemorySegment bodyId;
        try (Arena arena = Arena.ofConfined()) {
            b2BodyDef.name(defSegment, Body.encodeName(arena, def.name()));
            b2BodyDef.userData(defSegment, UserDataTable.pointerOf(handle));
            bodyId = nBox2D.nCreateBody(worldId, defSegment);
        } finally {
            b2BodyDef.name(defSegment, MemorySegment.NULL);
            b2BodyDef.userData(defSegment, MemorySegment.NULL);
        }
        if (b2BodyId.index1(bodyId) == 0) userDataTable.release(handle);
        return new Body(this, bodyId);
    }

    /**
     * Get the gravity vector of this world.
     * @return a new {@link Vec2} holding the gravity vector
     */
    public Vec2 gravity() {
        return new Vec2(nBox2D.nWorldGetGravity(checked()));
    }

    /**
     * Set the gravity vector for the entire world, usually in m/s².
     * @param gravity the gravity vector
     * @return this
     * @apiNote Box2D has no concept of an up direction and this is left as a decision for the application.
     */
    public World gravity(Vec2 gravity) {
        nBox2D.nWorldSetGravity(checked(), gravity.segment());
        return this;
    }

    /**
     * Check if body sleeping is enabled.
     * @return true if enabled
     */
    public boolean sleepingEnabled() {
        return nBox2D.nWorldIsSleepingEnabled(checked());
    }

    /**
     * Enable or disable sleeping.
     * <p>
     * If the application does not need sleeping, some performance can be gained by disabling sleep completely at the world level.
     * @param flag true to enable sleeping
     * @return this
     */
    public World sleepingEnabled(boolean flag) {
        nBox2D.nWorldEnableSleeping(checked(), flag);
        return this;
    }

    /**
     * Check if continuous collision is enabled.
     * @return true if enabled
     */
    public boolean continuousEnabled() {
        return nBox2D.nWorldIsContinuousEnabled(checked());
    }

    /**
     * Enable or disable continuous collision between dynamic and static bodies.
     * <p>
     * Generally, continuous collision should be enabled to prevent fast moving objects from going through static objects.
     * The performance gain from disabling continuous collision is minor.
     * @param flag true to enable continuous collision
     * @return this
     */
    public World continuousEnabled(boolean flag) {
        nBox2D.nWorldEnableContinuous(checked(), flag);
        return this;
    }

    /**
     * Check if constraint warm starting is enabled.
     * @return true if enabled
     */
    public boolean warmStartingEnabled() {
        return nBox2D.nWorldIsWarmStartingEnabled(checked());
    }

    /**
     * Enable or disable constraint warm starting.
     * <p>
     * Advanced feature for testing. Disabling warm starting greatly reduces stability and provides no performance gain.
     * @param flag true to enable warm starting
     * @return this
     */
    public World warmStartingEnabled(boolean flag) {
        nBox2D.nWorldEnableWarmStarting(checked(), flag);
        return this;
    }

    /**
     * Get the restitution speed threshold, usually in m/s.
     * @return the restitution threshold
     */
    public float restitutionThreshold() {
        return nBox2D.nWorldGetRestitutionThreshold(checked());
    }

    /**
     * Adjust the restitution threshold, usually in m/s.
     * <p>
     * It is recommended not to make this value very small because it will prevent bodies from sleeping.
     * @param value the restitution threshold
     * @return this
     */
    public World restitutionThreshold(float value) {
        nBox2D.nWorldSetRestitutionThreshold(checked(), value);
        return this;
    }

    /**
     * Get the hit event threshold, usually in m/s.
     * @return the hit event threshold
     */
    public float hitEventThreshold() {
        return nBox2D.nWorldGetHitEventThreshold(checked());
    }

    /**
     * Adjust the hit event threshold, usually in m/s.
     * <p>
     * This controls the collision speed needed to generate a hit event.
     * @param value the hit event threshold
     * @return this
     */
    public World hitEventThreshold(float value) {
        nBox2D.nWorldSetHitEventThreshold(checked(), value);
        return this;
    }

    /**
     * Get the maximum linear speed, usually in m/s.
     * @return the maximum linear speed
     */
    public float maxLinearSpeed() {
        return nBox2D.nWorldGetMaxLinearSpeed(checked());
    }

    /**
     * Set the maximum linear speed, usually in m/s.
     * @param maximumLinearSpeed the maximum linear speed
     * @return this
     */
    public World maxLinearSpeed(float maximumLinearSpeed) {
        nBox2D.nWorldSetMaxLinearSpeed(checked(), maximumLinearSpeed);
        return this;
    }

    /**
     * Get the contact stiffness, in cycles per second.
     * @return the contact hertz
     */
    public float contactHertz() {
        return nBox2D.nWorldGetContactTuning(checked())[0];
    }

    /**
     * Get the contact bounciness, non-dimensional, with 1 being critical damping.
     * @return the contact damping ratio
     */
    public float contactDampingRatio() {
        return nBox2D.nWorldGetContactTuning(checked())[1];
    }

    /**
     * Get the cap on how fast overlap is resolved, usually in m/s.
     * @return the contact speed
     */
    public float contactSpeed() {
        return nBox2D.nWorldGetContactTuning(checked())[2];
    }

    /**
     * Adjust the contact tuning parameters.
     * <p>
     * Increasing the stiffness increases the speed of overlap recovery, but can introduce jitter.
     * Decreasing the damping ratio also speeds up overlap recovery, with the trade-off that overlap resolution becomes more energetic.
     * <p>
     * The contact speed only puts a cap on the resolution speed. Negative values are clamped to zero.
     * @param hertz the contact stiffness, in cycles per second
     * @param dampingRatio the contact bounciness with 1 being critical damping, non-dimensional
     * @param contactSpeed the maximum contact constraint push out speed, usually in m/s
     * @return this
     * @apiNote Advanced feature
     */
    public World setContactTuning(float hertz, float dampingRatio, float contactSpeed) {
        nBox2D.nWorldSetContactTuning(checked(), hertz, dampingRatio, contactSpeed);
        return this;
    }

    /**
     * Get the contact point recycling distance, usually in meters.
     * @return the recycle distance
     */
    public float contactRecycleDistance() {
        return nBox2D.nWorldGetContactRecycleDistance(checked());
    }

    /**
     * Set the contact point recycling distance, usually in meters.
     * <p>
     * Setting this to zero disables contact point recycling.
     * @param recycleDistance the recycle distance
     * @return this
     */
    public World contactRecycleDistance(float recycleDistance) {
        nBox2D.nWorldSetContactRecycleDistance(checked(), recycleDistance);
        return this;
    }

    /**
     * Get the number of awake bodies.
     * @return the awake body count
     */
    public int awakeBodyCount() {
        return nBox2D.nWorldGetAwakeBodyCount(checked());
    }

    /**
     * Get the application specific data of this world.
     * @return the user data, or null if none
     */
    public Object userData() {
        return userDataTable.resolve(UserDataTable.handleOf(nBox2D.nWorldGetUserData(checked())));
    }

    /**
     * Set the application specific data of this world.
     * <p>
     * This world keeps a strong reference to the data until it is replaced or the world is destroyed.
     * @param userData the user data, or null to clear it
     * @return this
     * @apiNote Must not be called from inside a world callback.
     */
    public World userData(Object userData) {
        MemorySegment worldId = checked();
        userDataTable.release(UserDataTable.handleOf(nBox2D.nWorldGetUserData(worldId)));
        long handle = userDataTable.register(userData);
        nBox2D.nWorldSetUserData(worldId, UserDataTable.pointerOf(handle));
        return this;
    }

    @Override
    public String toString() {
        return String.format("World{index=%d, generation=%d, valid=%b}", index(), Short.toUnsignedInt(b2WorldId.generation(id)), isValid());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof World world)) return false;
        return b2WorldId.index1(id) == b2WorldId.index1(world.id)
                && b2WorldId.generation(id) == b2WorldId.generation(world.id);
    }

    @Override
    public int hashCode() {
        return 31 * b2WorldId.index1(id) + b2WorldId.generation(id);
    }

    /**
     * Get the user data table of this world.
     * @return the per-world user data table
     */
    UserDataTable userDataTable() {
        return userDataTable;
    }

    /**
     * Check if Box2D has freed this world.
     * @return true after {@link #destroy()} freed the native world
     */
    boolean isDestroyed() {
        return destroyed;
    }

    /**
     * Get the index of the world stored by Box2D.
     * @return the zero-based native world index, as carried by body and shape ids
     */
    int index() {
        return Short.toUnsignedInt(b2WorldId.index1(id)) - 1;
    }

    private MemorySegment checked() {
        if (!isValid()) throw new IllegalStateException("World is not valid: it has been destroyed");
        return id;
    }
}
