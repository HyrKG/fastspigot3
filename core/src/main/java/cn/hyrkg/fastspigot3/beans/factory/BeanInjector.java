package cn.hyrkg.fastspigot3.beans.factory;

import cn.hyrkg.fastspigot3.annotation.Inject;
import cn.hyrkg.fastspigot3.annotation.Instance;
import cn.hyrkg.fastspigot3.annotation.Autowired;
import cn.hyrkg.fastspigot3.beans.BeanManager;
import cn.hyrkg.fastspigot3.util.ReflectionUtils;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
/**
 * 负责基于注解的依赖注入：
 * <ul>
 * <li>@Inject / @Wire：为目标对象字段注入依赖，必要时创建并注册新实例</li>
 * <li>@Instance：为标注的静态字段写入当前实例（仅当类型兼容）</li>
 * </ul>
 * 设计要点：优先从 BeanManager 获取单例；若不存在则反射创建、注册并递归注入。
 */
@RequiredArgsConstructor
public class BeanInjector {

    private final BeanManager beanManager;

    /**
     * 对目标对象执行注入
     */
    public void injectInto(Object target) {

        if (target == null) {
            return;
        }

        try {
            Class<?> clazz = target.getClass();

            for (Field annotatedField : ReflectionUtils.findAnnotatedFields(clazz, Instance.class)) {
                if (!annotatedField.getType().isAssignableFrom(clazz)) {
                    throw new RuntimeException(
                            "Field " + annotatedField.getName() + " is not assignable from " + clazz.getName());
                }
                ReflectionUtils.setFieldValue(annotatedField, target, target);
            }

            for (Field annotatedField : ReflectionUtils.findAnnotatedFields(clazz, Inject.class)) {
                Object fieldValue = ReflectionUtils.getFieldValue(annotatedField, target);
                if (fieldValue != null) {
                    beanManager.registerBeanInstance(annotatedField.getType(), fieldValue);
                } else {
                    Object bean = beanManager.getOrRegisterBean(annotatedField.getType());
                    ReflectionUtils.setFieldValue(annotatedField, target, bean);
                }
            }

            for (Field annotatedField : ReflectionUtils.findAnnotatedFields(clazz, Autowired.class)) {
                Object bean = beanManager.getBean(annotatedField.getType());
                ReflectionUtils.setFieldValue(annotatedField, target, bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
