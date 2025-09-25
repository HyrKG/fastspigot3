package cn.hyrkg.fastspigot3.core;

import cn.hyrkg.fastspigot3.annotation.lifecycle.OnCreate;
import cn.hyrkg.fastspigot3.annotation.lifecycle.OnReady;
import cn.hyrkg.fastspigot3.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * 负责触发生命周期回调：@OnCreate 与 @OnReady。
 * - OnCreate：对象构造后调用
 * - OnReady：对象依赖注入/连接完成后调用（每实例仅一次）
 */
public class LifecycleInvoker {

    private final Set<Object> readyInvoked = Collections.newSetFromMap(new IdentityHashMap<>());

    public void invokeCreate(Object instance) {
        for (Method m : instance.getClass().getDeclaredMethods()) {
            if (!m.isAnnotationPresent(OnCreate.class)) continue;
            if (m.getParameterCount() != 0) continue;
            boolean acc = m.isAccessible();
            try {
                m.setAccessible(true);
                m.invoke(instance);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to invoke @OnCreate: " + m.getName() + " on " + instance.getClass().getName(), e);
            } finally {
                m.setAccessible(acc);
            }
        }
    }

    public void invokeReady(Object instance) {
        if (readyInvoked.contains(instance)) {
            return;
        }
        for (Method m : instance.getClass().getDeclaredMethods()) {
            if (!m.isAnnotationPresent(OnReady.class)) continue;
            if (m.getParameterCount() != 0) continue;
            boolean acc = m.isAccessible();
            try {
                m.setAccessible(true);
                m.invoke(instance);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to invoke @OnReady: " + m.getName() + " on " + instance.getClass().getName(), e);
            } finally {
                m.setAccessible(acc);
            }
        }
        readyInvoked.add(instance);
    }
}


