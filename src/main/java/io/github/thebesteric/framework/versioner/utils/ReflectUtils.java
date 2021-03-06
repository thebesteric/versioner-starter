package io.github.thebesteric.framework.versioner.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ClassUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * ReflectUtils
 *
 * @author Eric Joe
 * @version 1.0
 * @date 2020-12-10 21:43
 * @since 1.0
 */
@Slf4j
public class ReflectUtils {

    public static final String[] COMPILE_PATHS = new String[]{"target\\classes", "build\\classes\\java\\main"};

    public static String[] getModifiers(Method method) {
        return Modifier.toString(method.getModifiers()).split(" ");
    }

    public static boolean isPublic(Method method) {
        return Modifier.isPublic(method.getModifiers());
    }

    public static boolean isStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    public static boolean isFinal(Method method) {
        return Modifier.isFinal(method.getModifiers());
    }

    public static String[] getModifiers(Class<?> clazz) {
        return Modifier.toString(clazz.getModifiers()).split(" ");
    }

    public static boolean isPublic(Class<?> clazz) {
        return Modifier.isPublic(clazz.getModifiers());
    }

    public static boolean isStatic(Class<?> clazz) {
        return Modifier.isStatic(clazz.getModifiers());
    }

    public static boolean isFinal(Class<?> clazz) {
        return Modifier.isFinal(clazz.getModifiers());
    }

    public static String[] getModifiers(Field field) {
        return Modifier.toString(field.getModifiers()).split(" ");
    }

    public static boolean isPublic(Field field) {
        return Modifier.isPublic(field.getModifiers());
    }

    public static boolean isStatic(Field field) {
        return Modifier.isStatic(field.getModifiers());
    }

    public static boolean isFinal(Field field) {
        return Modifier.isFinal(field.getModifiers());
    }

    public static boolean isWrap(Class<?> clazz) {
        try {
            return ((Class<?>) clazz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isString(Class<?> clazz) {
        return String.class == clazz;
    }

    public static boolean isBigDecimal(Class<?> clazz) {
        return BigDecimal.class == clazz;
    }

    public static boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive();
    }

    public static boolean isComplex(Class<?> clazz) {
        return !isPrimitive(clazz) && !isWrap(clazz)
                && !isArray(clazz) && !isList(clazz) && !isMap(clazz) && !isSet(clazz)
                && clazz != String.class;
    }

    public static boolean isArray(Class<?> clazz) {
        return clazz.isArray();
    }

    public static boolean isList(Class<?> clazz) {
        return List.class.isAssignableFrom(clazz);
    }

    public static boolean isSet(Class<?> clazz) {
        return Set.class.isAssignableFrom(clazz);
    }

    public static boolean isMap(Class<?> clazz) {
        return Map.class.isAssignableFrom(clazz);
    }

    public static boolean isCollection(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz);
    }

    public static boolean isPoJo(Class<?> clazz) {
        return !isPrimitive(clazz) && !isWrap(clazz) && !isString(clazz)
                && !isArray(clazz) && !isCollection(clazz) && !isMap(clazz);
    }

    public static List<Field> getFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (Field field : clazz.getDeclaredFields()) {
                if (isStatic(field) || isFinal(field)) {
                    continue;
                }
                fields.add(field);
            }
        }
        return fields;
    }


    public static List<String> getFieldNames(Class<?> clazz) {
        List<Field> fields = getFields(clazz);
        List<String> fieldNames = new ArrayList<>();
        for (Field field : fields) {
            if (isStatic(field) || isFinal(field)) {
                continue;
            }
            fieldNames.add(field.getName());
        }
        return fieldNames;
    }

    public static Object getFieldValue(Field field, Object object) throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(object);
    }

    public static void setFieldValue(Object object, Field field, Object value) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(object, value);
    }

    public static String getFieldFullName(Field field) {
        return field.getDeclaringClass().getName() + "." + field.getName();
    }

    public static String getCollectionActualType(Field field) {
        if (isList(field.getType()) || isSet(field.getType())) {
            return ((ParameterizedTypeImpl) field.getGenericType()).getActualTypeArguments()[0].getTypeName();
        }
        throw new RuntimeException(field.getName() + " is not collection type");
    }

    public static boolean isAnnotationPresent(Class<?> objectClass, Class<? extends Annotation> annotationClass) {
        if (objectClass.isAnnotationPresent(annotationClass)) {
            return true;
        } else {
            for (Method method : objectClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotationClass)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Constructor<?> getDefaultConstructor(Class<?> clazz) {
        Constructor<?> constructor = determineConstructor(clazz);
        if (constructor != null && constructor.getParameterTypes().length == 0) {
            return constructor;
        }
        return null;
    }

    public static Constructor<?> determineConstructor(Class<?> clazz) {
        Constructor<?>[] rawCandidates = clazz.getDeclaredConstructors();
        if (rawCandidates.length != 0) {
            List<Constructor<?>> constructors = Arrays.asList(rawCandidates);
            constructors.sort((o1, o2) -> {
                if (o1.getParameterCount() != o2.getParameterCount()) {
                    return o1.getParameterCount() > o2.getParameterCount() ? 1 : -1;
                }
                return 0;
            });
            return constructors.get(0);
        }
        return null;
    }

    public static String getProjectPath() {
        ClassLoader defaultClassLoader = ClassUtils.getDefaultClassLoader();
        if (defaultClassLoader != null) {
            String path = Objects.requireNonNull(defaultClassLoader.getResource("")).getPath();
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                path = path.substring(1);
            }
            return path.replaceAll("%20", " ").replace("target/classes/", "");
        }
        return null;
    }


    public static Object newInstance(Constructor<?> constructor) throws Exception {
        constructor.setAccessible(true);
        Parameter[] parameters = constructor.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < args.length; i++) {
            args[i] = ObjectUtils.initialValue(parameters[i].getType());
        }
        return constructor.newInstance(args);
    }

    public static Class<?>[] argumentTypes(Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        if (parameters.length != 0) {
            Class<?>[] argumentTypes = new Class<?>[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Class<?> clazz = parameters[i].getType();
                argumentTypes[i] = clazz;
            }
            return argumentTypes;
        }
        return null;
    }

    public static Object[] argumentValues(Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        if (parameters.length != 0) {
            Object[] argumentValues = new Class<?>[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Class<?> clazz = parameters[i].getType();
                argumentValues[i] = ObjectUtils.initialValue(clazz);
            }
            return argumentValues;
        }
        return null;
    }

    public static List<Class<?>> getInterfaceImpls(Class<?> interfaceClass) {
        return getInterfaceImpls(interfaceClass, COMPILE_PATHS);

    }

    public static List<Class<?>> getInterfaceImpls(Class<?> interfaceClass, String[] compilePaths) {
        ArrayList<Class<?>> implClasses = new ArrayList<>();
        if (!interfaceClass.isInterface()) {
            throw new RuntimeException(interfaceClass.getName() + " is not a interface");
        }
        scan(getProjectPath(), compilePaths, (className) -> {
            try {
                Class<?> clazz = Class.forName(className);
                if (!clazz.isInterface() && clazz.getSuperclass() != null) {
                    Set<Class<?>> interfaces = new HashSet<>(Arrays.asList(clazz.getInterfaces()));
                    if (interfaces.contains(interfaceClass)) {
                        implClasses.add(Class.forName(clazz.getName()));
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        return implClasses;

    }

    public static void scan(String projectPath, Consumer<String> consumer) {
        scan(projectPath, COMPILE_PATHS, consumer);
    }

    public static void scan(String projectPath, String[] compilePaths, Consumer<String> consumer) {
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        if (url != null) {
            if ("file".equals(url.getProtocol())) {
                doScan(new File(projectPath + "/"), compilePaths, consumer);
            } else if ("jar".equals(url.getProtocol())) {
                try {
                    JarURLConnection connection = (JarURLConnection) url.openConnection();
                    JarFile jarFile = connection.getJarFile();
                    Enumeration<JarEntry> jarEntries = jarFile.entries();
                    while (jarEntries.hasMoreElements()) {
                        JarEntry jar = jarEntries.nextElement();
                        if (jar.isDirectory() || !jar.getName().endsWith(".class")) {
                            continue;
                        }
                        String jarName = jar.getName();
                        String classPath = jarName.replaceAll("/", ".");
                        String className = classPath.substring(0, classPath.lastIndexOf("."));
                        consumer.accept(className);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void doScan(File file, Consumer<String> consumer) {
        doScan(file, COMPILE_PATHS, consumer);
    }

    public static void doScan(File file, String[] compilePaths, Consumer<String> consumer) {
        if (file.isDirectory()) {
            for (File _file : Objects.requireNonNull(file.listFiles())) {
                doScan(_file, compilePaths, consumer);
            }
        } else {
            String filePath = file.getPath();
            int index = filePath.lastIndexOf(".");
            if (index != -1 && ".class".equals(filePath.substring(index))) {
                filePath = extractLegalFilePath(filePath, compilePaths);
                if (filePath != null) {
                    String classPath = filePath.replaceAll("\\\\", ".");
                    String className = classPath.substring(0, classPath.lastIndexOf("."));
                    consumer.accept(className);
                }
            }
        }
    }

    private static String extractLegalFilePath(String filePath, String[] compilePaths) {
        for (String compilePath : compilePaths) {
            int index = filePath.indexOf(compilePath);
            if (index != -1) {
                return filePath.substring(index + compilePath.length() + 1);
            }
        }
        return null;
    }

    public static String getMethodSignature(Method method) {
        return method.getDeclaringClass().getName() + "." + method.getName();
    }

    public static String getMethodParametersSignature(Method method) {
        StringBuilder signature = new StringBuilder();
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> parameterType : parameterTypes) {
            signature.append(parameterType.getName()).append(",");
        }
        return signature.length() > 0 ? signature.substring(0, signature.length() - 1) : "";
    }

    /**
     * ????????????
     *
     * @param originClass ?????????
     * @param source      ????????????
     * @param target      ????????????
     * @return java.lang.Object
     * @author Eric
     * @date 2021/5/26 17:57
     */
    public static synchronized Object copyProperties(Class<?> originClass, Object source, Object target) {
        Class<?> currentClass = originClass;
        do {
            for (Field sourceField : currentClass.getDeclaredFields()) {
                sourceField.setAccessible(true);
                try {
                    sourceField.set(target, sourceField.get(source));
                } catch (Exception ex) {
                    log.debug(ex.getMessage());
                }
            }
            currentClass = currentClass.getSuperclass();
        } while (currentClass != null && currentClass != Object.class);
        return target;
    }
}
