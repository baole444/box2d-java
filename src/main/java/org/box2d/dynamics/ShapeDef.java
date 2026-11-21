package org.box2d.dynamics;

import org.box2d.NativeLoader;
import org.box2d.internal.b2ShapeDef;
import org.box2d.internal.nBox2D;

import java.lang.foreign.MemorySegment;

/**
 * Used to create a shape.
 * <p>
 * The definition is copied when a {@link Shape} is created, allow it to be changed and reused later.
 * <p>
 * Every event flag is false by default, even for sensors.
 * <p>
 * Wrapper for native {@link b2ShapeDef}.
 */
public final class ShapeDef {
    private final MemorySegment segment;
    private Object userData = null;

    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    /**
     * Create a new {@link ShapeDef} initialised with Box2D's default values.
     */
    public ShapeDef() {
        segment = nBox2D.nDefaultShapeDef();
    }

    /**
     * Get the application specific data given to the shape on creation.
     * @return the user data, or null if none
     */
    public Object userData() {
        return userData;
    }

    /**
     * Set the application specific data given to the shape on creation.
     * <p>
     * The world that owns the created shape keeps a strong reference to the data until it is replaced, the shape is destroyed, or the world is destroyed.
     * @param userData the user data, or null for none
     * @return this
     */
    public ShapeDef userData(Object userData) {
        this.userData = userData;
        return this;
    }

    /**
     * Get the surface material for this shape.
     * This returns a wrapper, not a new copy.
     * @return a {@link SurfaceMaterial} that wraps the material
     */
    public SurfaceMaterial material() {
        return new SurfaceMaterial(b2ShapeDef.material(segment));
    }

    /**
     * Set the surface material for this shape.
     * @param material the material to copy into this definition
     * @return this
     */
    public ShapeDef material(SurfaceMaterial material) {
        b2ShapeDef.material(segment, material.segment());
        return this;
    }

    /**
     * Get the density, usually in kg/m².
     * @return the density
     */
    public float density() {
        return b2ShapeDef.density(segment);
    }

    /**
     * Set the density, usually in kg/m².
     * <p>
     * This is not part of the surface material because this is for the interior, which may have other considerations, such as being hollow.
     * For example a wood barrel may be hollow or full of water.
     * @param density the non-negative density
     * @return this
     */
    public ShapeDef density(float density) {
        b2ShapeDef.density(segment, density);
        return this;
    }

    /**
     * Get the collision filtering data.
     * This returns a wrapper, not a new copy.
     * @return a {@link Filter} that wraps the filter
     */
    public Filter filter() {
        return new Filter(b2ShapeDef.filter(segment));
    }

    /**
     * Set the collision filtering data.
     * @param filter the filter to copy into this definition
     * @return this
     */
    public ShapeDef filter(Filter filter) {
        b2ShapeDef.filter(segment, filter.segment());
        return this;
    }

    /**
     * Check if custom filtering is enabled.
     * @return true if enabled
     */
    public boolean enableCustomFiltering() {
        return b2ShapeDef.enableCustomFiltering(segment);
    }

    /**
     * Enable custom filtering.
     * <p>
     * Only one of the two shapes needs to enable custom filtering.
     * @param flag true to enable custom filtering
     * @return this
     */
    public ShapeDef enableCustomFiltering(boolean flag) {
        b2ShapeDef.enableCustomFiltering(segment, flag);
        return this;
    }

    /**
     * Check if the shape is a sensor.
     * @return true if a sensor
     */
    public boolean sensor() {
        return b2ShapeDef.isSensor(segment);
    }

    /**
     * Set whether the shape is a sensor.
     * <p>
     * A sensor shape generates overlap events but never generates a collision response.
     * Fast moving sensors may miss overlaps. Instead, use a ray or shape cast for those scenarios.
     * Sensors still contribute to the body mass if they have non-zero density.
     * <p>
     * Sensor events are disabled by default, see {@link #sensorEventsEnabled(boolean)}.
     * @param flag true to make the shape a sensor
     * @return this
     */
    public ShapeDef sensor(boolean flag) {
        b2ShapeDef.isSensor(segment, flag);
        return this;
    }

    /**
     * Check if sensor events are enabled for the shape.
     * @return true if enabled
     */
    public boolean sensorEventsEnabled() {
        return b2ShapeDef.enableSensorEvents(segment);
    }

    /**
     * Enable sensor events for the shape. This applies to sensors and non-sensors.
     * <p>
     * Both shapes involved must have this flag set to true. False by default, even for sensors.
     * @param flag true to enable sensor events
     * @return this
     */
    public ShapeDef sensorEventsEnabled(boolean flag) {
        b2ShapeDef.enableSensorEvents(segment, flag);
        return this;
    }

    /**
     * Check if contact events are enabled for the shape.
     * @return true if enabled
     */
    public boolean contactEventsEnabled() {
        return b2ShapeDef.enableContactEvents(segment);
    }

    /**
     * Enable contact events for the shape. Only applies to kinematic and dynamic bodies.
     * <p>
     * Only one shape involved needs this flag set to true. Ignored for sensors. False by default.
     * @param flag true to enable contact events
     * @return this
     */
    public ShapeDef contactEventsEnabled(boolean flag) {
        b2ShapeDef.enableContactEvents(segment, flag);
        return this;
    }

    /**
     * Check if hit events are enabled for the shape.
     * @return true if enabled
     */
    public boolean hitEventsEnabled() {
        return b2ShapeDef.enableHitEvents(segment);
    }

    /**
     * Enable hit events for the shape. Only applies to kinematic and dynamic bodies.
     * <p>
     * Only one shape involved needs this flag set to true. Ignored for sensors. False by default.
     * @param flag true to enable hit events
     * @return this
     */
    public ShapeDef hitEventsEnabled(boolean flag) {
        b2ShapeDef.enableHitEvents(segment, flag);
        return this;
    }

    /**
     * Check if pre-solve contact events are enabled for the shape.
     * @return true if enabled
     */
    public boolean preSolveEventsEnabled() {
        return b2ShapeDef.enablePreSolveEvents(segment);
    }

    /**
     * Enable pre-solve contact events for the shape. Only applies to dynamic bodies.
     * <p>
     * Only one shape involved needs this flag set to true. Ignored for sensors. False by default.
     * These are expensive and must be carefully handled due to multithreading.
     * @param flag true to enable pre-solve events
     * @return this
     */
    public ShapeDef preSolveEventsEnabled(boolean flag) {
        b2ShapeDef.enablePreSolveEvents(segment, flag);
        return this;
    }

    /**
     * Check if the shape scans the environment for collision when created.
     * @return true if enabled
     */
    public boolean invokeContactCreation() {
        return b2ShapeDef.invokeContactCreation(segment);
    }

    /**
     * Set whether the shape scans the environment for collision the next time step after it is created. True by default.
     * <p>
     * This can significantly slow down static body creation when there are many static shapes.
     * This flag is ignored for dynamic and kinematic shapes which always invoke contact creation.
     * @param flag true to invoke contact creation
     * @return this
     */
    public ShapeDef invokeContactCreation(boolean flag) {
        b2ShapeDef.invokeContactCreation(segment, flag);
        return this;
    }

    /**
     * Check if the body updates its mass properties when the shape is created.
     * @return true if enabled
     */
    public boolean updateBodyMass() {
        return b2ShapeDef.updateBodyMass(segment);
    }

    /**
     * Set whether the body updates its mass properties when the shape is created. True by default.
     * <p>
     * If this is false, {@link Body#updateMassFromShapes()} or {@link Body#massData(org.box2d.collision.MassData)} must be called before simulating the world.
     * @param flag true to update the body mass
     * @return this
     */
    public ShapeDef updateBodyMass(boolean flag) {
        b2ShapeDef.updateBodyMass(segment, flag);
        return this;
    }

    /**
     * Get the memory segment of this {@link ShapeDef}.
     * @return the underlying memory segment
     * @apiNote The native user data pointer is only filled in during shape creation.
     */
    public MemorySegment segment() {
        return segment;
    }

    @Override
    public String toString() {
        return String.format("ShapeDef{material=%s, density=%.3f, filter=%s, sensor=%b, sensorEvents=%b, contactEvents=%b, hitEvents=%b, preSolveEvents=%b, userData=%s}",
                material(), density(), filter(), sensor(), sensorEventsEnabled(), contactEventsEnabled(), hitEventsEnabled(), preSolveEventsEnabled(), userData);
    }
}
