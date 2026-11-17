package org.box2d.dynamics;

import org.box2d.NativeLoader;
import org.box2d.internal.b2WorldDef;
import org.box2d.internal.nBox2D;
import org.box2d.math.Vec2;

import java.lang.foreign.MemorySegment;

/**
 * World definition used to create a simulation world.
 * <p>
 * The definition is copied when a {@link World} is created, allow it to be changed and reused later.
 * <p>
 * Wrapper for native {@link b2WorldDef}.
 */
public final class WorldDef {
    private final MemorySegment segment;
    private Object userData = null;

    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    /**
     * Create a new {@link WorldDef} initialised with Box2D's default values.
     */
    public WorldDef() {
        segment = nBox2D.nDefaultWorldDef();
    }

    /**
     * Get the gravity vector.
     * This returns a wrapper, not a new copy.
     * @return a {@link Vec2} that wraps the gravity vector
     * @apiNote Box2D has no up-vector defined
     */
    public Vec2 gravity() {
        return new Vec2(b2WorldDef.gravity(segment));
    }

    /**
     * Set the gravity vector, usually in m/s².
     * @param gravity the gravity vector
     * @return this
     * @apiNote Box2D has no up-vector defined
     */
    public WorldDef gravity(Vec2 gravity) {
        b2WorldDef.gravity(segment, gravity.segment());
        return this;
    }

    /**
     * Set the gravity vector, usually in m/s².
     * @param x the gravity x component
     * @param y the gravity y component
     * @return this
     * @apiNote Box2D has no up-vector defined
     */
    public WorldDef gravity(float x, float y) {
        gravity().set(x, y);
        return this;
    }

    /**
     * Get the restitution speed threshold, usually in m/s.
     * <p>
     * Collisions above this speed have restitution applied (will bounce).
     * @return the restitution threshold
     */
    public float restitutionThreshold() {
        return b2WorldDef.restitutionThreshold(segment);
    }

    /**
     * Set the restitution speed threshold, usually in m/s.
     * <p>
     * Collisions above this speed have restitution applied (will bounce).
     * @param restitutionThreshold the restitution threshold
     * @return this
     */
    public WorldDef restitutionThreshold(float restitutionThreshold) {
        b2WorldDef.restitutionThreshold(segment, restitutionThreshold);
        return this;
    }

    /**
     * Get the threshold speed for hit events, usually in m/s.
     * @return the hit event threshold
     */
    public float hitEventThreshold() {
        return b2WorldDef.hitEventThreshold(segment);
    }

    /**
     * Set the threshold speed for hit events, usually in m/s.
     * @param hitEventThreshold the hit event threshold
     * @return this
     */
    public WorldDef hitEventThreshold(float hitEventThreshold) {
        b2WorldDef.hitEventThreshold(segment, hitEventThreshold);
        return this;
    }

    /**
     * Get the contact stiffness, in cycles per second.
     * @return the contact hertz
     */
    public float contactHertz() {
        return b2WorldDef.contactHertz(segment);
    }

    /**
     * Set the contact stiffness, in cycles per second.
     * <p>
     * Increasing this increases the speed of overlap recovery, but can introduce jitter.
     * @param contactHertz the contact hertz
     * @return this
     */
    public WorldDef contactHertz(float contactHertz) {
        b2WorldDef.contactHertz(segment, contactHertz);
        return this;
    }

    /**
     * Get the contact bounciness, non-dimensional.
     * @return the contact damping ratio
     */
    public float contactDampingRatio() {
        return b2WorldDef.contactDampingRatio(segment);
    }

    /**
     * Set the contact bounciness, non-dimensional.
     * <p>
     * Overlap recovery can be sped up by decreasing this, but it will make overlap resolution becomes more energetic.
     * @param contactDampingRatio the contact damping ratio
     * @return this
     */
    public WorldDef contactDampingRatio(float contactDampingRatio) {
        b2WorldDef.contactDampingRatio(segment, contactDampingRatio);
        return this;
    }

    /**
     * Get the cap on how fast overlap is resolved, usually in m/s.
     * @return the maximum contact push speed
     */
    public float maxContactPushSpeed() {
        return b2WorldDef.maxContactPushSpeed(segment);
    }

    /**
     * Set the cap on how fast overlap is resolved, usually in m/s.
     * <p>
     * This only puts a cap on the resolution speed, which is affected by changing the hertz and/or the damping ratio.
     * @param maxContactPushSpeed the maximum contact push speed
     * @return this
     */
    public WorldDef maxContactPushSpeed(float maxContactPushSpeed) {
        b2WorldDef.maxContactPushSpeed(segment, maxContactPushSpeed);
        return this;
    }

    /**
     * Get the maximum linear speed, usually in m/s.
     * @return the maximum linear speed
     */
    public float maximumLinearSpeed() {
        return b2WorldDef.maximumLinearSpeed(segment);
    }

    /**
     * Set the maximum linear speed, usually in m/s.
     * @param maximumLinearSpeed the maximum linear speed
     * @return this
     */
    public WorldDef maximumLinearSpeed(float maximumLinearSpeed) {
        b2WorldDef.maximumLinearSpeed(segment, maximumLinearSpeed);
        return this;
    }

    /**
     * Check if bodies can go to sleep to improve performance.
     * @return true if enabled
     */
    public boolean sleepingEnabled() {
        return b2WorldDef.enableSleep(segment);
    }

    /**
     * Set whether bodies can go to sleep to improve performance.
     * @param flag true to enable
     * @return this
     */
    public WorldDef sleepingEnabled(boolean flag) {
        b2WorldDef.enableSleep(segment, flag);
        return this;
    }

    /**
     * Check if continuous collision is enabled.
     * @return true if enabled
     */
    public boolean continuousEnabled() {
        return b2WorldDef.enableContinuous(segment);
    }

    /**
     * Set whether continuous collision is enabled.
     * @param flag true to enable
     * @return this
     */
    public WorldDef continuousEnabled(boolean flag) {
        b2WorldDef.enableContinuous(segment, flag);
        return this;
    }

    /**
     * Get the application specific data given to a world on creation.
     * @return the user data, or null if none
     */
    public Object userData() {
        return userData;
    }

    /**
     * Set the application specific data given to a world on creation.
     * <p>
     * The created world keeps a strong reference to the data until it is replaced or the world is destroyed.
     * @param userData the user data, or null for none
     * @return this
     */
    public WorldDef userData(Object userData) {
        this.userData = userData;
        return this;
    }

    /**
     * Get the memory segment of this {@link WorldDef}.
     * @return the underlying memory segment
     */
    public MemorySegment segment() {
        return segment;
    }

    @Override
    public String toString() {
        return String.format("WorldDef{gravity=%s, restitutionThreshold=%.2f, hitEventThreshold=%.2f, "
                        + "contactHertz=%.2f, contactDampingRatio=%.2f, maxContactPushSpeed=%.2f, "
                        + "maximumLinearSpeed=%.2f, enableSleep=%b, continuousEnabled=%b, userData=%s}",
                gravity(), restitutionThreshold(), hitEventThreshold(), contactHertz(), contactDampingRatio(),
                maxContactPushSpeed(), maximumLinearSpeed(), sleepingEnabled(), continuousEnabled(), userData);
    }
}
