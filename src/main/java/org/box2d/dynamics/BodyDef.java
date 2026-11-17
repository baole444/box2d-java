package org.box2d.dynamics;

import org.box2d.NativeLoader;
import org.box2d.internal.b2BodyDef;
import org.box2d.internal.nBox2D;
import org.box2d.math.Rot;
import org.box2d.math.Vec2;

import java.lang.foreign.MemorySegment;

/**
 * A body definition holds all the data needed to construct a rigid body.
 * <p>
 * The definition is copied when a {@link Body} is created, allow it to be changed and reused later.
 * <p>
 * Wrapper for native {@link b2BodyDef}.
 */
public final class BodyDef {
    private final MemorySegment segment;
    private String name = null;
    private Object userData = null;

    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    /**
     * Create a new {@link BodyDef} initialised with Box2D's default values.
     */
    public BodyDef() {
        segment = nBox2D.nDefaultBodyDef();
    }

    /**
     * Get the body type: static, kinematic, or dynamic.
     * @return the body type
     */
    public BodyType type() {
        return BodyType.of(b2BodyDef.type(segment));
    }

    /**
     * Set the body type: static, kinematic, or dynamic.
     * @param type the body type
     * @return this
     */
    public BodyDef type(BodyType type) {
        b2BodyDef.type(segment, type.nativeValue);
        return this;
    }

    /**
     * Get the initial world position of the body.
     * This returns a wrapper, not a new copy.
     * @return a {@link Vec2} that wraps the position
     */
    public Vec2 position() {
        return new Vec2(b2BodyDef.position(segment));
    }

    /**
     * Set the initial world position of the body. Bodies should be created with the desired position.
     * <p>
     * Creating bodies at the origin and then moving them nearly doubles the cost of body creation,
     * especially if the body is moved after shapes have been added.
     * @param position the initial position
     * @return this
     */
    public BodyDef position(Vec2 position) {
        b2BodyDef.position(segment, position.segment());
        return this;
    }

    /**
     * Set the initial world position of the body. Bodies should be created with the desired position.
     * <p>
     * Creating bodies at the origin and then moving them nearly doubles the cost of body creation,
     * especially if the body is moved after shapes have been added.
     * @param x the initial x coordinate
     * @param y the initial y coordinate
     * @return this
     */
    public BodyDef position(float x, float y) {
        position().set(x, y);
        return this;
    }

    /**
     * Get the initial world rotation of the body.
     * This returns a wrapper, not a new copy.
     * @return a {@link Rot} that wraps the rotation
     */
    public Rot rotation() {
        return new Rot(b2BodyDef.rotation(segment));
    }

    /**
     * Set the initial world rotation of the body.
     * @param rotation the initial rotation
     * @return this
     */
    public BodyDef rotation(Rot rotation) {
        b2BodyDef.rotation(segment, rotation.segment());
        return this;
    }

    /**
     * Set the initial world rotation of the body from an angle.
     * @param angle the initial angle in Radians
     * @return this
     */
    public BodyDef rotation(float angle) {
        rotation().angle(angle);
        return this;
    }

    /**
     * Get the initial linear velocity of the body's origin, usually in m/s.
     * This returns a wrapper, not a new copy.
     * @return a {@link Vec2} that wraps the linear velocity
     */
    public Vec2 linearVelocity() {
        return new Vec2(b2BodyDef.linearVelocity(segment));
    }

    /**
     * Set the initial linear velocity of the body's origin, usually in m/s.
     * @param linearVelocity the initial linear velocity
     * @return this
     */
    public BodyDef linearVelocity(Vec2 linearVelocity) {
        b2BodyDef.linearVelocity(segment, linearVelocity.segment());
        return this;
    }

    /**
     * Get the initial angular velocity of the body, in rad/s.
     * @return the angular velocity
     */
    public float angularVelocity() {
        return b2BodyDef.angularVelocity(segment);
    }

    /**
     * Set the initial angular velocity of the body, in rad/s.
     * @param angularVelocity the angular velocity
     * @return this
     */
    public BodyDef angularVelocity(float angularVelocity) {
        b2BodyDef.angularVelocity(segment, angularVelocity);
        return this;
    }

    /**
     * Get the linear damping, used to reduce the linear velocity.
     * @return the linear damping
     */
    public float linearDamping() {
        return b2BodyDef.linearDamping(segment);
    }

    /**
     * Set the linear damping, used to reduce the linear velocity.
     * <p>
     * The damping parameter can be larger than 1 but the damping effect becomes sensitive to the time step when the damping parameter is large.
     * <p>
     * Generally linear damping is undesirable because it makes objects move slowly as if they are floating.
     * @param linearDamping the non-negative linear damping
     * @return this
     */
    public BodyDef linearDamping(float linearDamping) {
        b2BodyDef.linearDamping(segment, linearDamping);
        return this;
    }

    /**
     * Get the angular damping, used to reduce the angular velocity.
     * @return the angular damping
     */
    public float angularDamping() {
        return b2BodyDef.angularDamping(segment);
    }

    /**
     * Set the angular damping, used to reduce the angular velocity.
     * <p>
     * The damping parameter can be larger than 1 but the damping effect becomes sensitive to the time step when the damping parameter is large.
     * <p>
     * Angular damping can be used to slow down rotating bodies.
     * @param angularDamping the non-negative angular damping
     * @return this
     */
    public BodyDef angularDamping(float angularDamping) {
        b2BodyDef.angularDamping(segment, angularDamping);
        return this;
    }

    /**
     * Get the scale of the gravity applied to this body, non-dimensional.
     * @return the gravity scale
     */
    public float gravityScale() {
        return b2BodyDef.gravityScale(segment);
    }

    /**
     * Set the scale of the gravity applied to this body, non-dimensional.
     * @param gravityScale the gravity scale
     * @return this
     */
    public BodyDef gravityScale(float gravityScale) {
        b2BodyDef.gravityScale(segment, gravityScale);
        return this;
    }

    /**
     * Get the sleep speed threshold, usually in m/s.
     * @return the sleep threshold
     */
    public float sleepThreshold() {
        return b2BodyDef.sleepThreshold(segment);
    }

    /**
     * Set the sleep speed threshold, usually in m/s.
     * @param sleepThreshold the non-negative sleep threshold
     * @return this
     */
    public BodyDef sleepThreshold(float sleepThreshold) {
        b2BodyDef.sleepThreshold(segment, sleepThreshold);
        return this;
    }

    /**
     * Get the optional body name for debugging.
     * @return the name, or null if none
     */
    public String name() {
        return name;
    }

    /**
     * Set the optional body name for debugging.
     * <p>
     * The created body stores up to 10 bytes of the name encoded in UTF-8, truncated without splitting a character when longer.
     * @param name the name, or null for none
     * @return this
     */
    public BodyDef name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get the application specific data given to the body on creation.
     * @return the user data, or null if none
     */
    public Object userData() {
        return userData;
    }

    /**
     * Set the application specific data given to the body on creation.
     * <p>
     * The world that owns the created body keeps a strong reference to the data until it is replaced, the body is destroyed, or the world is destroyed.
     * @param userData the user data, or null for none
     * @return this
     */
    public BodyDef userData(Object userData) {
        this.userData = userData;
        return this;
    }

    /**
     * Check if this body is allowed to fall asleep.
     * @return true if enabled
     */
    public boolean sleepingEnabled() {
        return b2BodyDef.enableSleep(segment);
    }

    /**
     * Set whether this body should be able to fall asleep.
     * @param flag true to allow sleeping
     * @return this
     */
    public BodyDef sleepingEnabled(boolean flag) {
        b2BodyDef.enableSleep(segment, flag);
        return this;
    }

    /**
     * Check if this body is initially awake.
     * @return true if  awake, false if sleeping
     */
    public boolean awake() {
        return b2BodyDef.isAwake(segment);
    }

    /**
     * Set whether this body is initially awake or sleeping.
     * @param flag true for awake, false for sleeping
     * @return this
     */
    public BodyDef awake(boolean flag) {
        b2BodyDef.isAwake(segment, flag);
        return this;
    }

    /**
     * Get the motion locks that restrict linear and angular movement.
     * This returns a wrapper, not a new copy.
     * @return a {@link MotionLocks} that wraps the motion locks
     */
    public MotionLocks motionLocks() {
        return new MotionLocks(b2BodyDef.motionLocks(segment));
    }

    /**
     * Set the motion locks that restrict linear and angular movement.
     * <p>
     * Locking only the rotation keeps the body's orientation fixed, useful for characters.
     * Locks may lead to softer constraints along the locked direction.
     * @param locks the motion locks
     * @return this
     */
    public BodyDef motionLocks(MotionLocks locks) {
        b2BodyDef.motionLocks(segment, locks.segment());
        return this;
    }

    /**
     * Get the continuous collision safety factor, non-dimensional.
     * @return the safety factor
     */
    public float safetyFactor() {
        return b2BodyDef.safetyFactor(segment);
    }

    /**
     * Set the continuous collision safety factor, non-dimensional.
     * <p>
     * This improves performance and prevents movement hitches. Recommended range [0.01, 0.5]. Default is 0.5 for high performance with low tunnelling risk.
     * @param safetyFactor the non-negative safety factor
     * @return this
     * @apiNote
     * The solver only uses continuous collision if there is a risk of tunnelling. If the body is moving fast enough to risk tunnelling then it is considered a "fast body".
     * <p>
     * If a body moving N meters risks tunnelling, then the body will be considered fast if it moves more than a {@code safetyFactor * N meters} over one full time step.
     */
    public BodyDef safetyFactor(float safetyFactor) {
        b2BodyDef.safetyFactor(segment, safetyFactor);
        return this;
    }

    /**
     * Check if contact recycling is enabled for this body.
     * @return true if enabled
     */
    public boolean contactRecyclingEnabled() {
        return b2BodyDef.enableContactRecycling(segment);
    }

    /**
     * Enable or disable contact recycling for this body. Enabled by default.
     * <p>
     * Leaving this enabled improves performance but may lead to ghost collisions that should be avoided on characters.
     * @param flag true to enable contact recycling
     * @return this
     */
    public BodyDef contactRecyclingEnabled(boolean flag) {
        b2BodyDef.enableContactRecycling(segment, flag);
        return this;
    }

    /**
     * Check if this body is treated as a high speed object.
     * @return true if this is a bullet
     */
    public boolean bullet() {
        return b2BodyDef.isBullet(segment);
    }

    /**
     * Treat this dynamic body as a high speed object that performs continuous collision detection
     * against dynamic and kinematic bodies, but not other bullet bodies.
     * @param flag true to make this a bullet
     * @return this
     * @apiNote
     * Bullets should be used sparingly. They are not a solution for general dynamic-vs-dynamic continuous collisions.
     * <p>
     * They do not guarantee accurate collision if both bodies are fast moving because the bullet does a continuous check after all non-bullet bodies have moved.
     * It can be very unlucky when the bullet body ends a time step very close to a non-bullet body and the non-bullet body then moves over the bullet body.
     * <p>
     * In continuous collision, initial overlap is ignored to avoid freezing bodies in place.
     * It is not recommended to use them for game projectiles if precise collision timing is needed.
     * Instead, consider using a ray or shape cast. It is possible to use a marching ray or shape cast for a projectile that moves over time.
     * <p>
     * For a fast moving projectile to collide with a fast moving target, the relative movement in ray or shape cast should be factored.
     * This is out of scope for Box2D. In current state, bullets are good for pinball games or games with dynamic containers that hold other objects.
     * It should be a use case where missing a collision won't break the game, but having them captured improves the quality of the game.
     */
    public BodyDef bullet(boolean flag) {
        b2BodyDef.isBullet(segment, flag);
        return this;
    }

    /**
     * Check if this body is initially enabled.
     * @return true if enabled
     */
    public boolean enabled() {
        return b2BodyDef.isEnabled(segment);
    }

    /**
     * Set whether this body is initially enabled. A disabled body does not move or collide.
     * @param flag true to enable
     * @return this
     */
    public BodyDef enabled(boolean flag) {
        b2BodyDef.isEnabled(segment, flag);
        return this;
    }

    /**
     * Check if this body is allowed to bypass rotational speed limits.
     * @return true if allowed
     */
    public boolean allowFastRotation() {
        return b2BodyDef.allowFastRotation(segment);
    }

    /**
     * Allow this body to bypass rotational speed limits.
     * <p>
     * Should only be used for circular objects, like wheels.
     * @param flag true to allow
     * @return this
     */
    public BodyDef allowFastRotation(boolean flag) {
        b2BodyDef.allowFastRotation(segment, flag);
        return this;
    }

    /**
     * Get the memory segment of this {@link BodyDef}.
     * @return the underlying memory segment
     * @apiNote The native name and user data pointer are only filled in during body creation.
     */
    public MemorySegment segment() {
        return segment;
    }

    @Override
    public String toString() {
        return String.format("BodyDef{type=%s, position=%s, rotation=%s, name=%s, userData=%s}", type(), position(), rotation(), name, userData);
    }
}
