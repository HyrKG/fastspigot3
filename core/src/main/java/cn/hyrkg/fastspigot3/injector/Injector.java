package cn.hyrkg.fastspigot3.injector;

import cn.hyrkg.fastspigot3.annotation.Inject;
import cn.hyrkg.fastspigot3.annotation.Instance;
import cn.hyrkg.fastspigot3.annotation.Wire;
import cn.hyrkg.fastspigot3.core.BeanManager;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 负责基于注解的依赖注入：
 * <ul>
 * 	<li>@Inject / @Wire：为目标对象字段注入依赖，必要时创建并注册新实例</li>
 * 	<li>@Instance：为标注的静态字段写入当前实例（仅当类型兼容）</li>
 * </ul>
 * 设计要点：优先从 BeanManager 获取单例；若不存在则反射创建、注册并递归注入。
 */
public class Injector {

    private final BeanManager beanManager;

    /**
     * @param beanManager Bean 管理器，提供单例缓存与注册能力
     */
    public Injector(BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    /**
     * 对目标对象执行注入：
     * 1) 处理 @Instance 静态字段
     * 2) 处理 @Inject/@Wire 的字段依赖
     *
     * @param target 需要注入的对象
     */
    public void injectInto(Object target) {
        if (target == null) {
            return;
        }
        assignInstanceStatics(target);
        injectFields(target);
    }

    /**
     * 遍历目标对象字段，按需从 BeanManager 取依赖，不存在则创建并注册，随后完成赋值。
     */
	private void injectFields(Object target) {
        Class<?> clazz = target.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
			boolean hasInject = field.isAnnotationPresent(Inject.class);
			boolean hasWire = field.isAnnotationPresent(Wire.class);
			if (!hasInject && !hasWire) {
                continue;
            }

            Class<?> dependencyType = field.getType();
            Object dependency = beanManager.getBean(dependencyType);

			// 只有 @Inject 才会在缺失时创建；@Wire 仅连接已有 Bean
            if (dependency == null && hasInject) {
                dependency = beanManager.resolveForInject(dependencyType);
			}

            // 对于 @Wire，如果容器中没有该 Bean，则抛出异常（不创建）
            if (dependency == null && hasWire) {
                throw new RuntimeException("Missing required wired bean: " + dependencyType.getName() + " for field '" + field.getName() + "' in " + clazz.getName());
            }

            try {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                field.set(target, dependency);
                field.setAccessible(accessible);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to inject field: " + field.getName() + " in " + clazz.getName(), e);
            }
        }
    }

    /**
     * 为标注了 @Instance 的静态字段写入当前实例，前提是字段类型可接收该实例类型。
     */
    private void assignInstanceStatics(Object instance) {
        Class<?> clazz = instance.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Instance.class)) {
                continue;
            }
            // 仅处理静态字段
            if ((field.getModifiers() & Modifier.STATIC) == 0) {
                continue;
            }
            if (!field.getType().isAssignableFrom(clazz)) {
                // 仅当静态字段类型可接收该实例类型时赋值
                continue;
            }
            try {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                field.set(null, instance);
                field.setAccessible(accessible);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to assign @Instance static field: " + field.getName() + " in " + clazz.getName(), e);
            }
        }
    }
}


