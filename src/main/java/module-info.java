/**
 * Box2D Java - a Java binding for the Box2D (v3) physics library.
 * <p>
 * Native access is required for the binding to load and call into the Box2D native library.
 * User must run with {@code --enable-native-access=org.box2d}, or {@code ALL-UNNAMED} when used off the module path.
 */
module box2d.java {
    exports org.box2d;
    exports org.box2d.math;
    exports org.box2d.collision;
    exports org.box2d.dynamics;
    // Uncomment these when there are actual API inside them.
    // exports org.box2d.dynamics.joints;
    // exports org.box2d.events;
}