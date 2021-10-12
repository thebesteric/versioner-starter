package io.github.thebesteric.framework.versioner.core;

import io.github.thebesteric.framework.versioner.annotation.Version;
import io.github.thebesteric.framework.versioner.annotation.Versioner;
import io.github.thebesteric.framework.versioner.annotation.Versions;
import io.github.thebesteric.framework.versioner.utils.ReflectUtils;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class VersionerHandler implements BeanPostProcessor {

    private final VersionerManager versionManager;
    public static final String KEY_SEPARATOR = ":";

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(Controller.class) || bean.getClass().isAnnotationPresent(RestController.class)) {
            for (Method method : bean.getClass().getDeclaredMethods()) {
                Versioner versioner = method.getAnnotation(Versioner.class);
                if (versioner != null) {
                    String version = versioner.value();
                    if (!StringUtils.hasLength(version)) {
                        return bean;
                    }

                    Set<String> uris = getRequestUri(method);
                    String key = versioner.key();
                    Class<?> type = versioner.type();

                    Type genericReturnType = method.getGenericReturnType();
                    Class<?> clazz;
                    try {
                        clazz = Class.forName(genericReturnType.getTypeName());
                    } catch (ClassNotFoundException ex) {
                        log.error("Cannot parse class {}", genericReturnType.getTypeName());
                        return bean;
                    }

                    Set<Pair<String, Field>> includeFields = new HashSet<>();
                    Set<Pair<String, Field>> excludeFields = new HashSet<>();

                    if (StringUtils.hasLength(key)) {
                        try {
                            Field declaredField;
                            if (type == NoneType.class) {
                                declaredField = clazz.getDeclaredField(key);
                                clazz = declaredField.getDeclaringClass();
                            } else {
                                //declaredField = clazz.getDeclaredField(key);
                                clazz = type;
                            }

                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                    }

                    // has @Versions on domain class
                    if (!clazz.isAnnotationPresent(Versions.class)) {
                        return bean;
                    }

                    // collect class fields
                    collectField(clazz, version, "", includeFields, excludeFields);

                    for (String uri : uris) {
                        versionManager.put(key.trim(), clazz, uri, versioner.value(), includeFields, excludeFields);
                    }

                }
            }
        }
        return bean;
    }

    public void collectField(Class<?> clazz, String version, String parentFieldName,
                             Set<Pair<String, Field>> includeFields, Set<Pair<String, Field>> excludeFields) {
        for (Field declaredField : clazz.getDeclaredFields()) {
            Version fieldVersion = declaredField.getAnnotation(Version.class);
            if (fieldVersion != null && !Arrays.asList(fieldVersion.value()).contains(version)) {
                excludeFields.add(new Pair<>(parentFieldName, declaredField));
                continue;
            }
            if (ReflectUtils.isPoJo(declaredField.getType()) && declaredField.getType().isAnnotationPresent(Versions.class)) {
                if (StringUtils.hasLength(parentFieldName)) {
                    parentFieldName += KEY_SEPARATOR + declaredField.getName();
                } else {
                    parentFieldName += declaredField.getName();
                }
                collectField(declaredField.getType(), version, parentFieldName, includeFields, excludeFields);
            } else {
                includeFields.add(new Pair<>(parentFieldName, declaredField));
            }
        }
    }

    private Set<String> getRequestUri(Method method) {
        Set<String> uris = new HashSet<>();

        Set<String> parentUris = new HashSet<>();
        Class<?> declaringClass = method.getDeclaringClass();
        RequestMapping parentRequestMapping = declaringClass.getAnnotation(RequestMapping.class);
        if (parentRequestMapping != null) {
            parentUris = new HashSet<>(Arrays.asList(parentRequestMapping.value()));
        }

        if (parentUris.isEmpty()) {
            parentUris.add("");
        }

        for (String parentUri : parentUris) {
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            if (requestMapping != null) {
                String[] methodUris = requestMapping.value();
                for (String methodUri : methodUris) {
                    uris.add(parentUri + methodUri);
                }
                continue;
            }

            GetMapping getMapping = method.getAnnotation(GetMapping.class);
            if (getMapping != null) {
                String[] methodUris = getMapping.value();
                for (String methodUri : methodUris) {
                    uris.add(parentUri + methodUri);
                }
                continue;
            }

            PostMapping postMapping = method.getAnnotation(PostMapping.class);
            if (postMapping != null) {
                String[] methodUris = postMapping.value();
                for (String methodUri : methodUris) {
                    uris.add(parentUri + methodUri);
                }
                continue;
            }

            PutMapping putMapping = method.getAnnotation(PutMapping.class);
            if (putMapping != null) {
                String[] methodUris = putMapping.value();
                for (String methodUri : methodUris) {
                    uris.add(parentUri + methodUri);
                }
                continue;
            }

            DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
            if (deleteMapping != null) {
                String[] methodUris = deleteMapping.value();
                for (String methodUri : methodUris) {
                    uris.add(parentUri + methodUri);
                }
            }
        }

        return uris;

    }


}
