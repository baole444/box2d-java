package org.box2d;

import org.box2d.internal.LogBridge;
import org.box2d.internal.nBox2D;
import org.box2d.math.CosSin;
import org.box2d.math.Rot;
import org.box2d.math.Vec2;

import java.io.PrintStream;

/**
 * Public API of Box2D, a collection of static methods from native implement, with additional safety check.
 * <p>
 * The trigonometric functions here are Java ports of Box2D's own, which are hand coded for cross-platform determinism.
 * They give the same results as Box2D's native functions on every platform, unlike {@link Math#atan2(double, double)}, {@link Math#cos(double)} and {@link Math#sin(double)}.
 */
public final class Box2D {
    static {
        if (!NativeLoader.isLoaded()) NativeLoader.load();
    }

    /**
     * Box2D's single precision value of pi.
     */
    public static final float Pi = 3.14159265359f;

    private Box2D() {}

    /**
     * Get the stream receiving Box2D's log messages.
     * @return the log stream, or null if log messages are discarded
     * @see #logStream(PrintStream)
     */
    public static PrintStream logStream() {
        return LogBridge.stream();
    }

    /**
     * Set the stream receiving Box2D's log messages, one message per line. Logging is off by default.
     * <p>
     * Box2D logs warnings, such as invalid input it rejects, an unstable body whose velocity became NaN or infinite during a step, or denormal flushing that breaks determinism.
     * <p>
     * Log messages may arrive on any thread calling into Box2D, including inside {@code World.step}. An exception thrown by the stream is discarded.
     * @param stream the stream to print to, such as {@link System#err}, or null to discard log messages
     * @see org.box2d/ Handling invalid input
     */
    public static void logStream(PrintStream stream) {
        LogBridge.stream(stream);
    }

    /**
     * Compute an approximate arctangent in the range [-pi, pi].
     * <p>
     * This is hand coded for cross-platform determinism. Accurate to around 0.0023 degrees.
     * Returns 0 when both {@code y} and {@code x} are 0.
     * @param y the y coordinate
     * @param x the x coordinate
     * @return the angle in radians, in the range [-pi, pi]
     */
    @SuppressWarnings("ManualMinMaxCalculation")
    public static float atan2(float y, float x) {
        // Check for (0,0) to match atan2f and avoid NaN
        if (x == 0.0f && y == 0.0f) return 0.0f;

        float ax = abs(x);
        float ay = abs(y);
        float mx = ay > ax ? ay : ax;
        float mn = ay < ax ? ay : ax;
        float a = mn / mx;

        // Minimax polynomial approximation to atan(a) on [0,1]
        float s = a * a;
        float c = s * a;
        float q = s * s;
        float r = 0.024840285f * q + 0.18681418f;
        float t = -0.094097948f * q - 0.33213072f;
        r = r * s + t;
        r = r * c + a;

        // Map to full circle
        if (ay > ax) r = 1.57079637f - r;
        if (x < 0) r = 3.14159274f - r;
        if (y < 0) r = -r;
        return r;
    }

    /**
     * Compute the cosine and sine of an angle in radians.
     * <p>
     * Implemented for cross-platform determinism.
     * The pair is normalised and each component is accurate to about 0.1 degrees (0.002).
     * @param radians the angle in radians, best kept within (-10000, 10000), see {@link #unwindAngle(float)}
     * @return the cosine and sine of the angle
     */
    public static CosSin computeCosSin(float radians) {
        float x = unwindAngle(radians);
        float pi2 = Pi * Pi;

        // cosine needs angle in [-pi/2, pi/2]
        float c;
        if (x < -0.5f * Pi) {
            float y = x + Pi;
            float y2 = y * y;
            c = -(pi2 - 4.0f * y2) / (pi2 + y2);
        } else if (x > 0.5f * Pi) {
            float y = x - Pi;
            float y2 = y * y;
            c = -(pi2 - 4.0f * y2) / (pi2 + y2);
        } else {
            float y2 = x * x;
            c = (pi2 - 4.0f * y2) / (pi2 + y2);
        }

        // sine needs angle in [0, pi]
        float s;
        if (x < 0.0f) {
            float y = x + Pi;
            s = -16.0f * y * (Pi - y) / (5.0f * pi2 - 4.0f * y * (Pi - y));
        } else {
            s = 16.0f * x * (Pi - x) / (5.0f * pi2 - 4.0f * x * (Pi - x));
        }

        float mag = (float) Math.sqrt(s * s + c * c);
        float invMag = mag > 0.0f ? 1.0f / mag : 0.0f;
        return new CosSin(c * invMag, s * invMag);
    }

    /**
     * Convert any angle into the range [-pi, pi].
     * <p>
     * This doesn't work well with huge angles, so unwind your angles regularly: keep them within (-10000, 10000).
     * Angles beyond ±1,000,000 are clamped before unwinding.
     * @param radians the angle in radians
     * @return the equivalent angle in radians, in the range [-pi, pi]
     */
    @SuppressWarnings("ManualMinMaxCalculation")
    public static float unwindAngle(float radians) {
        float x = radians < -1.0e6f ? -1.0e6f : (radians > 1.0e6f ? 1.0e6f : radians);

        // Work in doubles. Adding and removing this constant rounds to the nearest integer.
        double twoPi = 2.0f * Pi;
        double roundToNearest = 6755399441055744.0;
        double a = x;
        double k = (a / twoPi + roundToNearest) - roundToNearest;
        return (float) (a - k * twoPi);
    }

    /**
     * Mirror of Box2D's {@code b2AbsFloat}, which keeps the sign of negative zero unlike {@link Math#abs(float)}.
     */
    private static float abs(float a) {
        return a < 0 ? -a : a;
    }

    /**
     * Check if a rotation is valid.
     * A valid rotation is normalised and its components are not NaN or infinite.
     * @param rotation the rotation to validate
     * @return true if valid
     */
    public static boolean isValidRotation(Rot rotation) {
        if (rotation == null) return false;
        return nBox2D.nIsValidRot(rotation.segment());
    }

    /**
     * Check if a vector is valid.
     * A valid vector's components are not NaN or infinite.
     * @param vector the vector to validate
     * @return true if valid
     */
    public static boolean isValidVec2(Vec2 vector) {
        if (vector == null) return false;
        return nBox2D.nIsValidVec2(vector.segment());
    }

    /**
     * Check if a float value is valid.
     * A valid float value is not NaN or infinite.
     * @param value the float value to validate
     * @return true if valid
     */
    public static boolean isValidFloat(float value) {
        return nBox2D.nIsValidFloat(value);
    }
}
