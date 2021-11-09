package io.github.thebesteric.framework.versioner.core;

import io.github.thebesteric.framework.versioner.annotation.Version;
import io.github.thebesteric.framework.versioner.annotation.Versioner;
import io.github.thebesteric.framework.versioner.annotation.Versions;
import io.github.thebesteric.framework.versioner.domain.FieldDefinition;
import io.github.thebesteric.framework.versioner.domain.NoneType;
import io.github.thebesteric.framework.versioner.utils.ReflectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class VersionHandler implements BeanPostProcessor {

    private final VersionManager versionManager;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(Controller.class) || bean.getClass().isAnnotationPresent(RestController.class)) {
            for (Method method : bean.getClass().getDeclaredMethods()) {
                if (!method.isAnnotationPresent(ResponseBody.class)
                        && !method.getDeclaringClass().isAnnotationPresent(RestController.class)) {
                    return bean;
                }
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
                    VersionManager.VersionInfo versionInfo = versionManager.get(clazz, version);
                    Set<FieldDefinition> fieldDefinitions = versionInfo == null ? collect(version, clazz, new HashSet<>()) : versionInfo.getFieldDefinitions();

                    for (String uri : uris) {
                        versionManager.put(key.trim(), clazz, uri, versioner.value(), fieldDefinitions);
                    }

                }
            }
        }
        return bean;
    }

    public Set<FieldDefinition> collect(String version, Class<?> clazz, Set<FieldDefinition> fieldDefinitions) {
        for (Field declaredField : clazz.getDeclaredFields()) {
            Version fieldVersion = declaredField.getAnnotation(Version.class);
            FieldDefinition.FieldType fieldType;
            FieldDefinition fieldDefinition = null;
            boolean showBeRemove = false;
            String[] versions = fieldVersion == null ? null : fieldVersion.value();
            if (fieldVersion != null && !Arrays.asList(versions).contains(version)) {
                showBeRemove = true;
            }
            if (ReflectUtils.isPoJo(declaredField.getType())) {
                fieldType = FieldDefinition.FieldType.POJO;
                Set<FieldDefinition> subFieldDefinitions = collect(version, declaredField.getType(), new HashSet<>());
                fieldDefinition = new FieldDefinition(versions, declaredField, fieldType, subFieldDefinitions, showBeRemove);
            } else if (ReflectUtils.isCollection(declaredField.getType())) {
                fieldType = FieldDefinition.FieldType.COLLECTION;
                Type genericType = declaredField.getGenericType();
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) genericType;
                    Class<?> actualTypeArgument = (Class<?>) pt.getActualTypeArguments()[0];
                    Set<FieldDefinition> subFieldDefinitions = collect(version, actualTypeArgument, new HashSet<>());
                    fieldDefinition = new FieldDefinition(versions, declaredField, fieldType, subFieldDefinitions, showBeRemove);
                }
            } else if (ReflectUtils.isMap(declaredField.getType())) {
                fieldType = FieldDefinition.FieldType.MAP;
                Type genericType = declaredField.getGenericType();
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) genericType;
                    Class<?> actualKeyTypeArgument = (Class<?>) pt.getActualTypeArguments()[0];
                    Class<?> actualValueTypeArgument = (Class<?>) pt.getActualTypeArguments()[1];
                    Set<FieldDefinition> subKeyFieldDefinitions = collect(version, actualKeyTypeArgument, new HashSet<>());
                    Set<FieldDefinition> subValueFieldDefinitions = collect(version, actualValueTypeArgument, new HashSet<>());
                    fieldDefinition = new FieldDefinition(versions, declaredField, fieldType, subValueFieldDefinitions, showBeRemove);
                }
            } else {
                fieldType = FieldDefinition.FieldType.BASIC;
                fieldDefinition = new FieldDefinition(versions, declaredField, fieldType, null, showBeRemove);
            }
            fieldDefinitions.add(fieldDefinition);
        }
        return fieldDefinitions;
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
