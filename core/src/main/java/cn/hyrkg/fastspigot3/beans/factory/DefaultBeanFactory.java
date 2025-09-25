package cn.hyrkg.fastspigot3.beans.factory;

import cn.hyrkg.fastspigot3.beans.factory.config.BeanDefinition;
import cn.hyrkg.fastspigot3.beans.factory.support.BeanDefinitionFactory;
import cn.hyrkg.fastspigot3.beans.factory.support.BeanRegistry;
import cn.hyrkg.fastspigot3.context.annotation.AnnotationBeanNameGenerator;
import cn.hyrkg.fastspigot3.context.annotation.BeanLifecycleProcessor;
import cn.hyrkg.fastspigot3.context.annotation.processor.BeanAnnotationProcessor;
import cn.hyrkg.fastspigot3.context.annotation.processor.ProcessBeanForAnnotation;
import cn.hyrkg.fastspigot3.injector.FieldInjector;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * 默认 Bean 工厂实现：负责 Bean 的创建、注册、查询、依赖注入与生命周期回调。
 * 不承担扫描与依赖排序的编排（由上层 FastApplicationContext 负责）。
 */
public class DefaultBeanFactory implements BeanFactory, BeanRegistry, BeanDefinitionFactory {

    private final FieldInjector injector = new FieldInjector();
    private final BeanLifecycleProcessor lifecycle = new BeanLifecycleProcessor();
    private final AnnotationBeanNameGenerator nameGenerator = new AnnotationBeanNameGenerator();

    // 存放 BeanDefinition（元数据）
    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    // 单例池（已经实例化好的对象）
    private final Map<String, Object> singletonObjects = new HashMap<>();
    // 存放processor
    private final Map<Class<? extends Annotation>, String> processorBeanNameMap = new HashMap<>();

    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        String beanName = beanDefinition.getBeanName();
        if (beanDefinitionMap.containsKey(beanName)) {
            throw new IllegalArgumentException("Bean name already exists: " + beanName);
        }
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public BeanDefinition registerBean(Class<?> clazz) {
        BeanDefinition bean = createBeanDefinition(clazz);
        registerBeanDefinition(bean);
        if (clazz.isAnnotationPresent(ProcessBeanForAnnotation.class)) {
            processorBeanNameMap.put(clazz.getAnnotation(ProcessBeanForAnnotation.class).value(), bean.getBeanName());
        }
        return bean;
    }

    @Override
    public BeanDefinition registerBeanInstance(Class<?> clazz, Object instance) {
        return registerBeanInstance(null, clazz, instance);
    }

    @Override
    public BeanDefinition registerBeanInstance(String name, Class<?> clazz, Object instance) {
        BeanDefinition beanDefinition = registerBean(clazz);
        if (name != null && !name.isEmpty()) {
            beanDefinition.setBeanName(name);
        }
        if (singletonObjects.containsKey(name)) {
            throw new IllegalArgumentException("Bean instance already exists: " + name);
        }
        registerBeanDefinition(beanDefinition);
        singletonObjects.put(name, instance);
        return beanDefinition;
    }

    public Object loadBean(String name) {
        return getBean(name);
    }

    public Object getBean(String beanName) {
        Object instance = singletonObjects.get(beanName);
        if (instance != null) {
            return instance;
        }
        BeanDefinition definition = beanDefinitionMap.get(beanName);
        if (definition == null) {
            throw new IllegalArgumentException("No such bean definition: " + beanName);
        }
        instance = createBean(definition);
        singletonObjects.put(beanName, instance);
        return instance;
    }

    @Override
    public Object getBean(String name, Class<?> clazz) {
        Object bean = getBean(name);
        if (!clazz.isInstance(bean)) {
            throw new IllegalArgumentException("Bean is not of required type: " + clazz.getName());
        }
        return bean;
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            BeanDefinition definition = entry.getValue();
            if (definition.getBeanClass().isAssignableFrom(requiredType)) {
                return requiredType.cast(getBean(entry.getKey()));
            }
        }
        return null;
    }

    private Object createBean(BeanDefinition definition) {
        Class<?> clazz = definition.getBeanClass();
        try {
            Object instance = clazz.getConstructor().newInstance();
            lifecycle.invokeCreate(instance);
            injector.inject(instance, this, this);
            lifecycle.invokeReady(instance);

            // 处理自定义注解
            for (Annotation annotation : clazz.getAnnotations()) {
                if (processorBeanNameMap.containsKey(annotation.annotationType())) {
                    String processorBeanName = processorBeanNameMap.get(annotation.annotationType());
                    Object processorBean = getBean(processorBeanName);
                    if (processorBean instanceof BeanAnnotationProcessor) {
                        ((BeanAnnotationProcessor) processorBean).postProcess(annotation, instance);
                    }
                }
            }
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to instantiate bean: " + clazz.getName(), e);
        }
    }

    @Override
    public BeanDefinition createBeanDefinition(Class<?> clazz) {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanClass(clazz);
        beanDefinition.setBeanName(nameGenerator.generateBeanName(beanDefinition));
        return beanDefinition;
    }
}


