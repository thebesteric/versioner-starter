package io.github.thebesteric.framework.versioner.core;

import io.github.thebesteric.framework.versioner.domain.ArrayIndexPointer;
import io.github.thebesteric.framework.versioner.domain.FieldDefinition;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class VersionManager {

    // Cache for Class
    // Map<Class<?>, Map<Version, VersionInfo>>
    private static final Map<Class<?>, Map<String, VersionInfo>> CACHE = new ConcurrentHashMap<>(64);

    // Uri mapping VersionInfo
    // Map<Uri, VersionInfo>
    private static final Map<String, VersionInfo> VERSIONS = new ConcurrentHashMap<>(64);

    // Control index when array modified
    public final ThreadLocal<ArrayIndexPointer> arrayIndexPointer = ThreadLocal.withInitial(ArrayIndexPointer::new);

    public VersionInfo get(Class<?> clazz, String version) {
        Map<String, VersionInfo> versionInfoMap = CACHE.get(clazz);
        return versionInfoMap != null ? versionInfoMap.get(version) : null;
    }

    public VersionInfo get(String uri) {
        return VERSIONS.get(uri);
    }

    public void put(String key, Class<?> clazz, String uri, String version, Set<FieldDefinition> fieldDefinitions) {
        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setKey(key.trim());
        versionInfo.setClazz(clazz);
        versionInfo.setVersion(version);
        versionInfo.setFieldDefinitions(fieldDefinitions);
        VERSIONS.put(uri, versionInfo);
        putCache(clazz, version, versionInfo);
    }

    public void putCache(Class<?> clazz, String version, VersionInfo versionInfo) {
        Map<String, VersionInfo> map = new HashMap<>();
        map.put(version, versionInfo);
        CACHE.put(clazz, map);
    }

    @Getter
    @Setter
    public static class VersionInfo {
        private String key;
        private Class<?> clazz;
        private String version;
        private Set<FieldDefinition> fieldDefinitions = new HashSet<>();
    }

}
