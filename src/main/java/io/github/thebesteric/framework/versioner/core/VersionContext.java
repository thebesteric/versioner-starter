package io.github.thebesteric.framework.versioner.core;

import lombok.Data;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

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

    public static String getAppVersion() {
        return context.get().getAppVersion();
    }

    public static void setAppVersion(String appVersionName, HttpServletRequest request) {
        String appVersion = request.getHeader(appVersionName);
        if (!StringUtils.hasLength(appVersion)) {
            appVersion = request.getParameter(appVersionName);
        }
        context.get().setAppVersion(appVersion);
    }

    @Data
    private static class Context {
        private String appVersion;
        private String version;
        private String uri;
    }
}