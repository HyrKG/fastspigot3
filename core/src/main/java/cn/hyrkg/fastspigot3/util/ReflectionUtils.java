package cn.hyrkg.fastspigot3.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ReflectionUtils {

    // 找到被对应注解标记的字段
    public static List<Field> findAnnotatedFields(Class<?> clazz, Class<? extends Annotation> annotation) {
        Field[] fields = clazz.getDeclaredFields();
        List<Field> annotatedFields = new ArrayList<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(annotation)) {
                annotatedFields.add(field);
            }
        }
        return annotatedFields;
    }

    public static List<Field> findFields(Class<?> clazz, Predicate<Field> predicate) {
        Field[] fields = clazz.getDeclaredFields();
        List<Field> matching = new ArrayList<>();
        for (Field field : fields) {
            if (predicate.test(field)) {
                matching.add(field);
            }
        }
        return matching;
    }

    public static void setFieldValue(Field field, Object instance, Object value) throws IllegalAccessException {
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        // 判断是否是static，如果是则设置为null
        if ((field.getModifiers() & Modifier.STATIC) != 0) {
            field.set(null, value);
        } else {
            field.set(instance, value);
        }
        field.setAccessible(accessible);
    }

    public static Object getFieldValue(Field field, Object instance) throws IllegalAccessException {
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        // 判断是否是static，如果是则设置为null
        Object value = null;
        if ((field.getModifiers() & Modifier.STATIC) != 0) {
            value = field.get(null);
        } else {
            value = field.get(instance);
        }
        field.setAccessible(accessible);
        return value;
    }

    /// ////////////////////////////////
    /// methods


    // 找到被对应注解标记的方法
    public static List<Method> findAnnotatedMethods(Class<?> clazz, Class<? extends Annotation> annotation) {
        Method[] methods = clazz.getDeclaredMethods();
        List<Method> annotatedMethods = new java.util.ArrayList<>();
        for (Method method : methods) {
            if (method.isAnnotationPresent(annotation)) {
                annotatedMethods.add(method);
            }
        }
        return annotatedMethods;
    }

    // 安全调用方法（自动处理可访问性与静态方法）
    public static Object invoke(Method method, Object instance, Object... args) throws ReflectiveOperationException {
        boolean accessible = method.isAccessible();
        method.setAccessible(true);
        try {
            Object target = ((method.getModifiers() & Modifier.STATIC) != 0) ? null : instance;
            return method.invoke(target, args);
        } finally {
            method.setAccessible(accessible);
        }
    }

    // 便捷：仅调用不关心返回值
    public static void invokeVoid(Method method, Object instance, Object... args) throws ReflectiveOperationException {
        invoke(method, instance, args);
    }
}
