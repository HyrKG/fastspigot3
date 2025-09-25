package cn.hyrkg.fastspigot3.core;

import cn.hyrkg.fastspigot3.annotation.lifecycle.OnCreate;
import cn.hyrkg.fastspigot3.annotation.lifecycle.OnReady;
import cn.hyrkg.fastspigot3.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * 负责触发生命周期回调
 */
public class LifecycleInvoker {

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
}


