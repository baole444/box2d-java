package org.box2d.internal;

import java.io.PrintStream;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.charset.StandardCharsets;

/**
 * Bridge from Box2D's log callback ({@code b2SetLogCallback}) to a Java {@link PrintStream}.
 * <p>
 * One upcall stub is installed for the life of the process, since Box2D cannot restore its default handler.
 * Messages are dropped while no stream is set.
 */
public final class LogBridge {
    /**
     * {@code b2Log} formats into a 512-byte buffer.
     */
    private static final long MaxMessageBytes = 512;
    private static final MemorySegment stub = b2LogCallbackFcn.allocate(LogBridge::onLog, Arena.global());
    private static volatile PrintStream stream;

    private LogBridge() {}

    /**
     * Install the log callback into Box2D. Called once, after the native library is loaded.
     */
    public static void install() {
        Box2D.b2SetLogCallback(stub);
    }

    /**
     * Get the stream receiving Box2D's log messages.
     * @return the stream, or null if messages are dropped
     */
    public static PrintStream stream() {
        return stream;
    }

    /**
     * Set the stream receiving Box2D's log messages.
     * @param target the stream, or null to drop messages
     */
    public static void stream(PrintStream target) {
        stream = target;
    }

    /**
     * Upcall target. It must never throw: an exception escaping an upcall terminates the JVM.
     */
    private static void onLog(MemorySegment message) {
        PrintStream target = stream;
        if (target == null) return;
        try {
            String text = message.reinterpret(MaxMessageBytes).getString(0, StandardCharsets.UTF_8);
            target.println(text.stripTrailing());
        } catch (Throwable _) {}
    }
}
