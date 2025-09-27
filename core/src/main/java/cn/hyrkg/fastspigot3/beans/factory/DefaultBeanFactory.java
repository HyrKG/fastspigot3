package cn.hyrkg.fastspigot3.beans.factory;

import cn.hyrkg.fastspigot3.beans.factory.config.BeanDefinition;
import cn.hyrkg.fastspigot3.beans.factory.support.BeanDefinitionFactory;
import cn.hyrkg.fastspigot3.beans.factory.support.BeanRegistry;
import cn.hyrkg.fastspigot3.context.annotation.AnnotationBeanNameGenerator;
import cn.hyrkg.fastspigot3.context.annotation.BeanLifecycleProcessor;
import cn.hyrkg.fastspigot3.context.annotation.processor.BeanAnnotationProcessor;
import cn.hyrkg.fastspigot3.context.annotation.processor.FieldAnnotationProcessor;
import cn.hyrkg.fastspigot3.context.annotation.processor.ProcessBeanForAnnotation;
import cn.hyrkg.fastspigot3.context.annotation.processor.ProcessFieldForAnnotation;
import cn.hyrkg.fastspigot3.injector.FieldInjector;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

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
    private final Map<Class<? extends Annotation>, String> fieldProcessorBeanNameMap = new HashMap<>();

    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        String beanName = beanDefinition.getBeanName();
        if (beanDefinitionMap.containsKey(beanName)) {
            throw new IllegalArgumentException("Bean name already exists: " + beanName);
        }
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public BeanDefinition registerBean(Class<?> clazz) {
        return registerBean(null, clazz);
    }

    @Override
    public BeanDefinition registerBean(String name, Class<?> clazz) {
        BeanDefinition bean = createBeanDefinition(clazz);
        if (name != null && !name.isEmpty()) {
            bean.setBeanName(name);
        }
        registerBeanDefinition(bean);
        registerProcessors(clazz, bean.getBeanName());
        return bean;
    }

    @Override
    public BeanDefinition registerBeanInstance(Class<?> clazz, Object instance) {
        return registerBeanInstance(null, clazz, instance);
    }

    @Override
    public BeanDefinition registerBeanInstance(String name, Class<?> clazz, Object instance) {
        BeanDefinition beanDefinition = createBeanDefinition(clazz);
        if (name != null && !name.isEmpty()) {
            beanDefinition.setBeanName(name);
        }
        name = beanDefinition.getBeanName();
        if (singletonObjects.containsKey(name)) {
            throw new IllegalArgumentException("Bean name already exists in singleton pool: " + name);
        }
        registerBeanDefinition(beanDefinition);
        registerProcessors(clazz, beanDefinition.getBeanName());
        singletonObjects.put(name, instance);

        try {
            injectBeanInstance(beanDefinition, instance);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to inject bean instance: " + name, e);
        }
        return beanDefinition;
    }

    private void injectBeanInstance(BeanDefinition definition, Object instance) throws ReflectiveOperationException {
        Logger logger=null;
        Class<?> clazz = definition.getBeanClass();
        lifecycle.invokeCreate(instance);
        injector.inject(instance, this, this);
        lifecycle.invokeReady(instance);

        // 处理自定义注解
        for (Annotation annotation : clazz.getAnnotations()) {
            if (processorBeanNameMap.containsKey(annotation.annotationType())) {
                String processorBeanName = processorBeanNameMap.get(annotation.annotationType());
                if (processorBeanName.equals(definition.getBeanName())) {
                    continue;
                }
                Object processorBean = getBean(processorBeanName);
                if (processorBean instanceof BeanAnnotationProcessor) {
                    ((BeanAnnotationProcessor) processorBean).postProcess(annotation, instance);
                }
            }
        }
    }

    public void unregisterBean(String name) {
        processorBeanNameMap.values().removeIf(name::equals);
        fieldProcessorBeanNameMap.values().removeIf(name::equals);
        beanDefinitionMap.remove(name);
        Object bean = singletonObjects.remove(name);
        if (bean != null) {
            try {
                lifecycle.invokeDestroy(bean);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void unregisterBean(Class<?> clazz) {
        BeanDefinition definition = createBeanDefinition(clazz);
        unregisterBean(definition.getBeanName());
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
        try {
            instance = createBean(definition);
            singletonObjects.put(beanName, instance);
            injectBeanInstance(definition, instance);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create bean: " + beanName, e);
        }
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
            if (requiredType.isAssignableFrom(definition.getBeanClass())) {
                return requiredType.cast(getBean(entry.getKey()));
            }
        }
        return null;
    }

    private Object createBean(BeanDefinition definition) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = definition.getBeanClass();
        Object instance = clazz.getConstructor().newInstance();
        return instance;
    }

    @Override
    public BeanDefinition createBeanDefinition(Class<?> clazz) {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanClass(clazz);
        beanDefinition.setBeanName(nameGenerator.generateBeanName(beanDefinition));
        return beanDefinition;
    }

    private void registerProcessors(Class<?> clazz, String beanName) {
        if (clazz.isAnnotationPresent(ProcessBeanForAnnotation.class)) {
            processorBeanNameMap.put(clazz.getAnnotation(ProcessBeanForAnnotation.class).value(), beanName);
        }
        if (clazz.isAnnotationPresent(ProcessFieldForAnnotation.class)) {
            fieldProcessorBeanNameMap.put(clazz.getAnnotation(ProcessFieldForAnnotation.class).value(), beanName);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Annotation> FieldAnnotationProcessor<T> getFieldAnnotationProcessor(Class<T> annotationType) {
        String processorBeanName = fieldProcessorBeanNameMap.get(annotationType);
        if (processorBeanName == null) {
            return null;
        }
        Object processorBean = getBean(processorBeanName);
        if (!(processorBean instanceof FieldAnnotationProcessor)) {
            throw new IllegalStateException("Bean " + processorBeanName + " is not a FieldAnnotationProcessor");
        }
        return (FieldAnnotationProcessor<T>) processorBean;
    }

    public Set<Class<? extends Annotation>> getFieldProcessorAnnotationTypes() {
        return new LinkedHashSet<>(fieldProcessorBeanNameMap.keySet());
    }
}


