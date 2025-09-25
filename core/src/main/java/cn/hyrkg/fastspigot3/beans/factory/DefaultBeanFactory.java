package cn.hyrkg.fastspigot3.beans.factory;

import cn.hyrkg.fastspigot3.context.BeanLifecycleProcessor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * 默认 Bean 工厂实现：负责 Bean 的创建、注册、查询、依赖注入与生命周期回调。
 * 不承担扫描与依赖排序的编排（由上层 FastApplicationContext 负责）。
 */
public class DefaultBeanFactory implements BeanFactory {

    private final BeanCreator creator = new BeanCreator();
    private final BeanInjector injector = new BeanInjector(this);
    private final BeanLifecycleProcessor lifecycle = new BeanLifecycleProcessor();

    private final HashMap<Class<?>, Object> registeredBeanMap = new HashMap<>();
    private final ThreadLocal<Set<Class<?>>> constructing = ThreadLocal.withInitial(HashSet::new);

    public <T> T registerBean(Class<T> clazz) {
        try {
            T instance = createGuarded(clazz);
            lifecycle.invokeCreate(instance);
            registerBeanInstance(clazz, instance);
            injector.injectInto(instance);
            lifecycle.invokeReady(instance);
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to instantiate bean: " + clazz.getName(), e);
        }
    }

    public void registerBeanInstance(Class<?> clazz, Object instance) {
        unregisterBean(clazz);
        registeredBeanMap.put(clazz, instance);
    }

    public void unregisterBean(Class<?> clazz) {
        Object instance = registeredBeanMap.remove(clazz);
        if (instance != null) {
            try {
                lifecycle.invokeDestroy(instance);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to destroy bean: " + clazz.getName(), e);
            }
        }
    }

    public <T> T getOrRegisterBean(Class<T> clazz) {
        T bean = getBean(clazz);
        if (bean == null) {
            bean = registerBean(clazz);
        }
        return bean;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz) {
        Object instance = registeredBeanMap.get(clazz);
        if (instance == null) {
            return null;
        }
        if (!clazz.isInstance(instance)) {
            throw new RuntimeException("Registered bean is not of the requested type: " + clazz.getName());
        }
        return (T) instance;
    }

    private <T> T createGuarded(Class<T> clazz) throws ReflectiveOperationException {
        Set<Class<?>> stack = constructing.get();
        if (stack.contains(clazz)) {
            throw new RuntimeException("Circular dependency detected while constructing: " + clazz.getName());
        }
        stack.add(clazz);
        try {
            return creator.createBean(clazz);
        } finally {
            stack.remove(clazz);
        }
    }
}


