package cn.hyrkg.fastspigot3.context.annotation;

import cn.hyrkg.fastspigot3.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * 负责触发生命周期回调
 */
public class BeanLifecycleProcessor {

    public void invokeCreate(Object instance) throws ReflectiveOperationException {
        for (Method annotatedMethod : ReflectionUtils.findAnnotatedMethods(instance.getClass(), OnCreate.class)) {
            ReflectionUtils.invokeVoid(annotatedMethod, instance);
        }
    }

    public void invokeReady(Object instance) throws ReflectiveOperationException {
        for (Method annotatedMethod : ReflectionUtils.findAnnotatedMethods(instance.getClass(), OnReady.class)) {
            ReflectionUtils.invokeVoid(annotatedMethod, instance);
        }
    }

    public void invokeDestroy(Object instance) throws ReflectiveOperationException {
        for (Method annotatedMethod : ReflectionUtils.findAnnotatedMethods(instance.getClass(), OnDestroy.class)) {
            ReflectionUtils.invokeVoid(annotatedMethod, instance);
        }
    }
}


