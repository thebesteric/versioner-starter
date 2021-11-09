package io.github.thebesteric.framework.versioner.core;

import lombok.Data;

public class VersionContext {

    private static final ThreadLocal<Context> context = ThreadLocal.withInitial(Context::new);

    public static String getVersion() {
        return context.get().getVersion();
    }

    public static void setVersion(String version) {
        context.get().setVersion(version);
    }

    public static String getURI() {
        return context.get().getUri();
    }

    public static void setURI(String uri) {
        context.get().setUri(uri);
    }

    public static void clear() {
        context.remove();
    }

    @Data
    private static class Context {
        private String version;
        private String uri;
    }
}