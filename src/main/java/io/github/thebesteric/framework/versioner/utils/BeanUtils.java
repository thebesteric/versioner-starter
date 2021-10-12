package io.github.thebesteric.framework.versioner.utils;

import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.beans.BeanMap;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BeanUtils {

    public static Object create(Object target, Map<String, Class<?>> properties) {
        BeanGenerator beanGenerator = new BeanGenerator();
        properties.forEach(beanGenerator::addProperty);
        Object obj = beanGenerator.create();
        BeanMap beanMap = BeanMap.create(obj);
        BeanCopier copier = BeanCopier.create(target.getClass(), obj.getClass(), false);
        copier.copy(target, obj, null);
        return obj;
    }

    public static Object create(Object target, Set<Field> fields) {
        Map<String, Class<?>> properties = new HashMap<>();
        for (Field field : fields) {
            properties.put(field.getName(), field.getType());
        }
        return create(target, properties);
    }

}
