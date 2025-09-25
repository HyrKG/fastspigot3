package cn.hyrkg.fastspigot3.context;

import cn.hyrkg.fastspigot3.beans.factory.BeanFactory;
import cn.hyrkg.fastspigot3.beans.factory.DefaultBeanFactory;
import cn.hyrkg.fastspigot3.beans.support.BeanDependencyResolver;
import cn.hyrkg.fastspigot3.scanning.ComponentScanner;

import java.util.List;

/**
 * 应用上下文（门面）：对外提供统一的扫描、装配与 Bean 访问能力。
 * 内部组合 DefaultBeanFactory，负责底层 Bean 的创建与注入。
 */
public class FastApplicationContext {

    private final BeanFactory beanFactory = new DefaultBeanFactory();
    private final ComponentScanner componentScanner = new ComponentScanner();
    private final BeanDependencyResolver dependencyResolver = new BeanDependencyResolver();

    public void scanAndRegister(String basePackage) {
        List<Class<?>> candidates = componentScanner.scan(basePackage);
        List<Class<?>> ordered = dependencyResolver.resolveOrder(candidates);
        for (Class<?> clazz : ordered) {
            beanFactory.registerBean(clazz);
        }
    }

    public <T> T getBean(Class<T> type) {
        return beanFactory.getBean(type);
    }

    public <T> T getOrRegisterBean(Class<T> type) {
        return beanFactory.getOrRegisterBean(type);
    }

    public void registerBeanInstance(Class<?> type, Object instance) {
        beanFactory.registerBeanInstance(type, instance);
    }

    public void unregisterBean(Class<?> type) {
        beanFactory.unregisterBean(type);
    }
}


