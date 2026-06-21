package org.box2d;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Loader for the Box2D native library file.
 * Most wrapper classes will contain static block to check and load the native library.<br>
 */
public final class NativeLoader {
    private enum Platform {
        Windows("win", "box2d.dll"),
        Linux("linux", "libbox2d.so"),
        Mac("mac", "libbox2d.dylib");

        final String dir;
        final String lib;

        Platform(String dir, String lib) {
            this.dir = dir;
            this.lib = lib;
        }
        static Platform detect() {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains(Windows.dir)) return Windows;
            if (os.contains(Linux.dir)) return Linux;
            if (os.contains(Mac.dir)) {
                String arch = System.getProperty("os.arch").toLowerCase();
                if (arch.contains("aarch64") || arch.contains("arm")) return Mac;
                throw new UnsatisfiedLinkError("Unsupported macOS architecture: " + arch + " (only arm64 is supported)");
            }
            throw new UnsatisfiedLinkError("Unsupported operating system: " + os);
        }
    }

    private static volatile boolean loaded = false;

    /**
     * Load the native Box2D library file by copying it from library resource to the temporary directory.
     * If the file is already extracted and loaded, calling this does nothing.
     * @throws UnsatisfiedLinkError on failed to get the {@link InputStream} or the operating system is not supported
     * @throws RuntimeException on failed to create the temporary file for the native library or on setting file permission on Linux
     */
    public static synchronized void load() {
        if (loaded) return;
        Platform plat = Platform.detect();
        String resource = "/natives/" + plat.dir + "/" + plat.lib;
        try (InputStream inputStream = NativeLoader.class.getResourceAsStream(resource)) {
            if (inputStream == null) {
                throw new UnsatisfiedLinkError("Cannot locate Box2D native library on classpath: " + resource);
            }
            File tmpDir = new File(System.getProperty("java.io.tmpdir"), "box2d-natives");
            if (!tmpDir.exists() && !tmpDir.mkdirs()) {
                throw new RuntimeException("Failed to create temp directory: " + tmpDir);
            }
            File tmpLib = new File(tmpDir, plat.lib);
            Files.copy(inputStream, tmpLib.toPath(), StandardCopyOption.REPLACE_EXISTING);
            if (plat == Platform.Linux) {
                int permission = 0;
                permission += tmpLib.setExecutable(true, false) ? 1 : 0;
                permission += tmpLib.setReadable(true, false) ? 1 : 0;
                permission += tmpLib.setWritable(true, true) ? 1 : 0;
                if (permission < 3) throw new RuntimeException("Failed to set permission for the library's file");
            }
            try {
                System.load(tmpLib.getAbsolutePath());
            } catch (IllegalCallerException e) {
                throw nativeAccessDenied(e);
            }
            loaded = true;
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract Box2D native library: ", e);
        }
    }

    /**
     * Get the load status of the Box2D native library in the system.
     * @return true if loaded
     */
    public static boolean isLoaded() {
        return loaded;
    }

    /**
     * Create guiding message when missing native access to load the native library.
     * <p>
     * This is when the JVM denies native access due to missing {@code --enable-native-access}.
     * Required target is this module's name on the module path, or {@code ALL-UNNAMED} on the classpath.
     * @param cause the exception cause
     * @return the cause with attached guide
     */
    private static UnsatisfiedLinkError nativeAccessDenied(IllegalCallerException cause) {
        Module module = NativeLoader.class.getModule();
        String grant = module.isNamed() ? module.getName() : "ALL-UNNAMED";
        UnsatisfiedLinkError error = new UnsatisfiedLinkError(
                "Box2D Java requires native access, but it is not enabled. "
                        + "Add the JVM argument --enable-native-access=" + grant + " when launching "
                        + "(or set 'Enable-Native-Access: ALL-UNNAMED' in your executable JAR's manifest).");
        error.initCause(cause);
        return error;
    }
}
