package cn.hyrkg.fastspigot3.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;

public class ReflectionUtils {

    /**
     * 查找类中被指定注解标记的全部字段。
     *
     * @param clazz      目标类
     * @param annotation 目标注解类型
     * @return 满足条件的字段集合
     */
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
    

    /**
     * 根据断言筛选类中的字段。
     *
     * @param clazz     目标类
     * @param predicate 字段筛选条件
     * @return 满足条件的字段集合
     */
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

    /**
     * 设置字段值，自动处理可访问性与静态字段。
     *
     * @param field    目标字段
     * @param instance 所属实例，静态字段可为 {@code null}
     * @param value    待写入的值
     * @throws IllegalAccessException 访问字段失败时抛出
     */
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


    
    /**
     * 读取字段值，自动处理可访问性与静态字段。
     *
     * @param field    目标字段
     * @param instance 所属实例，静态字段可为 {@code null}
     * @return 字段当前值
     * @throws IllegalAccessException 访问字段失败时抛出
     */
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

    /**
     * 计算 {@code candidate} 到 {@code target} 的类型距离。
     * <p>
     * 距离越小表示越接近目标类型，若 {@code candidate} 与 {@code target}
     * 相同则距离为 0；若不可赋值则返回 {@link Integer#MAX_VALUE}。
     * </p>
     * <p>
     * 该实现使用广度优先遍历，同时考虑父类与接口层级，以便找到最短路径。
     * </p>
     */
    public static int getTypeDistance(Class<?> target, Class<?> candidate) {
        if (target == null || candidate == null) {
            throw new IllegalArgumentException("Target and candidate must not be null");
        }
        if (!target.isAssignableFrom(candidate)) {
            return Integer.MAX_VALUE;
        }
        if (target.equals(candidate)) {
            return 0;
        }

        Queue<Class<?>> queue = new ArrayDeque<>();
        Set<Class<?>> visited = new HashSet<>();

        queue.add(candidate);
        visited.add(candidate);
        int distance = 0;

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                Class<?> current = queue.poll();
                if (target.equals(current)) {
                    return distance;
                }
                Class<?> superClass = current.getSuperclass();
                if (superClass != null && visited.add(superClass)) {
                    queue.add(superClass);
                }
                for (Class<?> iface : current.getInterfaces()) {
                    if (visited.add(iface)) {
                        queue.add(iface);
                    }
                }
            }
            distance++;
        }
        return Integer.MAX_VALUE;
    }

    /// ////////////////////////////////
    /// methods


    /**
     * 查找类中被指定注解标记的全部方法。
     *
     * @param clazz      目标类
     * @param annotation 目标注解类型
     * @return 满足条件的方法集合
     */
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

    /**
     * 调用方法并返回结果，自动处理可访问性以及静态方法。
     *
     * @param method   目标方法
     * @param instance 所属实例，静态方法可为 {@code null}
     * @param args     方法参数
     * @return 方法返回值
     * @throws ReflectiveOperationException 反射调用失败时抛出
     */
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

    /**
     * 便捷调用方法，仅关心副作用而忽略返回值。
     *
     * @param method   目标方法
     * @param instance 所属实例，静态方法可为 {@code null}
     * @param args     方法参数
     * @throws ReflectiveOperationException 反射调用失败时抛出
     */
    public static void invokeVoid(Method method, Object instance, Object... args) throws ReflectiveOperationException {
        invoke(method, instance, args);
    }

    /**
     * 获取一个类及其所有父类的声明字段
     *
     * @param clazz 要获取字段的类
     * @return 包含类及其所有父类声明字段的数组
     */
    public static Field[] getInheritedDeclaredFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        
        // 遍历类的继承层次结构，获取所有声明的字段
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                fields.add(field);
            }
            currentClass = currentClass.getSuperclass();
        }
        
        return fields.toArray(new Field[0]);
    }
    
    /**
     * 通过字段名获取字段值，自动处理可访问性与静态字段。
     *
     * @param clazz     目标类
     * @param fieldName 字段名称
     * @param instance  所属实例，静态字段可为 {@code null}
     * @return 字段当前值，如果字段不存在则返回null
     * @throws IllegalAccessException 访问字段失败时抛出
     */
    public static Object getFieldValue(Class<?> clazz, String fieldName, Object instance) throws IllegalAccessException {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            return getFieldValue(field, instance);
        } catch (NoSuchFieldException e) {
            // 如果在当前类中找不到字段，尝试在父类中查找
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && superClass != Object.class) {
                return getFieldValue(superClass, fieldName, instance);
            }
            return null;
        }
    }
}
