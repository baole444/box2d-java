/**
 * Box2D Java - a Java binding for the Box2D (v3) physics library.
 * <p>
 * Native access is required for the binding to load and call into the Box2D native library.
 * Application must run with {@code --enable-native-access=org.box2d}, or {@code ALL-UNNAMED} when used off the module path.
 * <p>
 * <b>Handling invalid input:</b>
 * <ul>
 *     <li>
 *     Box2D validates the values passed to its native objects, such as worlds and bodies.
 *     When an operation is given an invalid value (like NaN, infinity, or a value outside the documented range), it rejects the input and keeps the previous value.
 *     </li>
 *     <li>
 *     An object created from a definition holding an invalid value is invalid from the start, its {@code isValid()} returns false. Each rejection is logged.
 *     </li>
 * </ul>
 * <p>
 * Methods therefore document only their valid range and any value that is clamped rather than rejected.
 * Plain value types, like {@link org.box2d.math.Vec2} or {@link org.box2d.collision.Polygon}, are not validated, with some offer {@code isValid()} to check them.
 * @see org.box2d.Box2D#logStream(java.io.PrintStream) Viewing Box2D's logs
 */
module org.box2d {
    exports org.box2d;
    exports org.box2d.math;
    exports org.box2d.collision;
    exports org.box2d.dynamics;
    // Uncomment these when there are actual API inside them.
    // exports org.box2d.dynamics.joints;
    // exports org.box2d.events;
}