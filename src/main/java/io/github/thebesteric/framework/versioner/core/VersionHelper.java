package io.github.thebesteric.framework.versioner.core;

import lombok.Data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class VersionHelper {

    private static final ThreadLocal<ExcludeInfo> EXCLUDE_FIELDS = ThreadLocal.withInitial(ExcludeInfo::new);

    public static void excludes(String... fieldNames) {
        if (fieldNames == null || fieldNames.length == 0) {
            return;
        }
        ExcludeInfo excludeInfo = get();
        Set<String> collection = excludeInfo.getExcludeFields();
        collection.addAll(Arrays.asList(fieldNames));
        EXCLUDE_FIELDS.set(excludeInfo);
    }

    public static void excludesWithUri(String uri, String... fieldNames) {
        excludes(fieldNames);
        ExcludeInfo excludeInfo = get();
        excludeInfo.setUri(uri);
    }

    public static boolean isEmpty() {
        return EXCLUDE_FIELDS.get().getExcludeFields().isEmpty();
    }

    public static ExcludeInfo get() {
        return EXCLUDE_FIELDS.get();
    }

    public static void clear() {
        EXCLUDE_FIELDS.remove();
    }

    @Data
    public static class ExcludeInfo {
        private String uri;
        private Set<String> excludeFields = new HashSet<>();
    }

}
