package cn.hyrkg.fastspigot3.context;

import cn.hyrkg.fastspigot3.beans.factory.DefaultBeanDefinitionRegistry;
import cn.hyrkg.fastspigot3.beans.factory.config.BeanDefinition;
import cn.hyrkg.fastspigot3.context.annotation.AnnotationComponentClassScanner;
import cn.hyrkg.fastspigot3.context.support.BeanDependencyResolver;
import cn.hyrkg.fastspigot3.scanner.Scanner;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * 应用上下文（门面）：对外提供统一的扫描、装配与 Bean 访问能力。
 * 内部组合 DefaultBeanFactory，负责底层 Bean 的创建与注入。
 */
public class ApplicationContext {

    @Getter
    private final DefaultBeanDefinitionRegistry beanFactory = new DefaultBeanDefinitionRegistry();
    private final Scanner beanScanner = new AnnotationComponentClassScanner();
    private final BeanDependencyResolver dependencyResolver = new BeanDependencyResolver();

    private Consumer<String> logConsumer = null;

    public void setLogConsumer(Consumer<String> logConsumer) {
        this.logConsumer = logConsumer;
    }

    public void scanAndRegister(String basePackage) {
        scanAndRegister(basePackage, null);
    }

    public void scanAndRegister(String basePackage, Class<?> anchorClass) {
        List<BeanDefinition> definitions = scanAndRegisterDefinitions(basePackage, anchorClass);
        loadBeans(definitions);
    }

    /**
     * 扫描并注册指定包路径下的所有组件定义，但不立即实例化。
     *
     * @param basePackage 扫描的基础包名
     * @param anchorClass 锚点类（用于确定扫描位置）
     * @return 注册成功的 Bean 定义列表（已按依赖顺序排序）
     */
    public List<BeanDefinition> scanAndRegisterDefinitions(String basePackage, Class<?> anchorClass) {
        List<Class<?>> componentClasses;
        if (anchorClass == null) {
            componentClasses = beanScanner.scan(basePackage);
        } else {
            componentClasses = beanScanner.scan(basePackage, anchorClass);
        }
        componentClasses = dependencyResolver.resolveOrder(componentClasses);

        List<BeanDefinition> definitions = new ArrayList<>();
        for (Class<?> clazz : componentClasses) {
            definitions.add(beanFactory.registerBean(clazz));
        }
        return definitions;
    }

    /**
     * 根据提供的 Bean 定义列表，按顺序实例化并注入所有 Bean。
     *
     * @param definitions 要加载的 Bean 定义列表
     */
    public void loadBeans(List<BeanDefinition> definitions) {
        for (BeanDefinition definition : definitions) {
            long start = System.currentTimeMillis();
            beanFactory.loadBean(definition.getBeanName());
            log("..." + definition.getBeanName() + " (" + (System.currentTimeMillis() - start) + " ms)");
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

    private void log(String message) {
        if (logConsumer != null) {
            logConsumer.accept(message);
        }
    }

}


