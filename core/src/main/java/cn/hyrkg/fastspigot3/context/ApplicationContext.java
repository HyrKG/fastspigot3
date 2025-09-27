package cn.hyrkg.fastspigot3.context;

import cn.hyrkg.fastspigot3.beans.factory.DefaultBeanDefinitionRegistry;
import cn.hyrkg.fastspigot3.beans.factory.config.BeanDefinition;
import cn.hyrkg.fastspigot3.context.annotation.AnnotationComponentClassScanner;
import cn.hyrkg.fastspigot3.context.support.BeanDependencyResolver;
import cn.hyrkg.fastspigot3.scanner.Scanner;

import java.util.Collections;
import java.util.List;

/**
 * 应用上下文（门面）：对外提供统一的扫描、装配与 Bean 访问能力。
 * 内部组合 DefaultBeanFactory，负责底层 Bean 的创建与注入。
 */
public class ApplicationContext {

    private final DefaultBeanDefinitionRegistry beanFactory = new DefaultBeanDefinitionRegistry();
    private final Scanner beanScanner = new AnnotationComponentClassScanner();
    private final BeanDependencyResolver dependencyResolver = new BeanDependencyResolver();

    public void scanAndRegister(String basePackage) {
        scanAndRegister(basePackage, null);
    }

    public void scanAndRegister(String basePackage, Class<?> anchorClass) {
        List<Class<?>> componentClass = null;
        if (anchorClass == null) {
            componentClass = beanScanner.scan(basePackage);
        } else {
            componentClass = beanScanner.scan(basePackage, anchorClass);
        }
        componentClass = dependencyResolver.resolveOrder(componentClass);
        for (Class<?> clazz : componentClass) {
            BeanDefinition beanDefinition = beanFactory.registerBean(clazz);
            beanFactory.loadBean(beanDefinition.getBeanName());
        }
    }

    public void scanAndUnregister(String basePackage) {
        scanAndUnregister(basePackage, null);
    }

    public void scanAndUnregister(String basePackage, Class<?> anchorClass) {
        List<Class<?>> componentClass = null;
        if (anchorClass == null) {
            componentClass = beanScanner.scan(basePackage);
        } else {
            componentClass = beanScanner.scan(basePackage, anchorClass);
        }
        componentClass = dependencyResolver.resolveOrder(componentClass);
        Collections.reverse(componentClass);
        for (Class<?> clazz : componentClass) {
            String beanName = beanFactory.generateBeanDefinition(clazz).getBeanName();
            if (beanName != null) {
                beanFactory.unregisterBean(beanName);
            }
        }
    }

    public DefaultBeanDefinitionRegistry getBeanFactory() {
        return beanFactory;
    }
}


