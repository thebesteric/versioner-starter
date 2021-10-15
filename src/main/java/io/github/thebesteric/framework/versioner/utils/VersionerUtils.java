package io.github.thebesteric.framework.versioner.utils;

public class VersionerUtils {

    private static final ThreadLocal<String> versions = new ThreadLocal<>();

    public static String get() {
        return versions.get();
    }

    public static void set(String version) {
        versions.set(version);
    }

    public static void remove() {
        versions.remove();
    }
}
