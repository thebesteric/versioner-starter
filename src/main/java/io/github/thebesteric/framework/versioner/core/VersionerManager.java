package io.github.thebesteric.framework.versioner.core;

import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VersionerManager {

    private static final Map<String, VersionInfo> versions = new HashMap<>(64);

    public VersionInfo get(String uri) {
        return versions.get(uri);
    }

    public void put(String key, Class<?> type, String uri, String version,
                    Set<Pair<String, Field>> includeFields, Set<Pair<String, Field>> excludeFields) {
        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setKey(key.trim());
        versionInfo.setType(type);
        versionInfo.setVersion(version);
        versionInfo.setIncludeFields(includeFields);
        versionInfo.setExcludeFields(excludeFields);
        versions.put(uri, versionInfo);
    }

    @Getter
    @Setter
    public static class VersionInfo {
        private String key;
        private Class<?> type;
        private String version;
        private Set<Pair<String, Field>> includeFields = new HashSet<>();
        private Set<Pair<String, Field>> excludeFields = new HashSet<>();
    }

}
