package cn.hyrkg.fastspigot3.beans.factory;

import cn.hyrkg.fastspigot3.beans.factory.config.BeanDefinition;
import cn.hyrkg.fastspigot3.beans.factory.support.BeanProcessorRegistry;
import cn.hyrkg.fastspigot3.beans.factory.support.BeanDefinitionRegistry;
import cn.hyrkg.fastspigot3.context.annotation.AnnotationBeanNameGenerator;
import cn.hyrkg.fastspigot3.context.annotation.BeanLifecycleProcessor;
import cn.hyrkg.fastspigot3.context.annotation.processor.BeanAnnotationProcessor;
import cn.hyrkg.fastspigot3.context.annotation.processor.FieldAnnotationProcessor;
import cn.hyrkg.fastspigot3.context.annotation.processor.ProcessBeanForAnnotation;
import cn.hyrkg.fastspigot3.context.annotation.processor.ProcessFieldForAnnotation;
import cn.hyrkg.fastspigot3.injector.FieldInjector;
import cn.hyrkg.fastspigot3.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;

/**
 * 默认 Bean 工厂实现：负责 Bean 的创建、注册、查询、依赖注入与生命周期回调。
 * 不承担扫描与依赖排序的编排（由上层 FastApplicationContext 负责）。
 */
public class DefaultBeanDefinitionRegistry implements BeanFactory, BeanDefinitionRegistry, BeanProcessorRegistry {

    private final FieldInjector fieldInjector = new FieldInjector();
    private final BeanLifecycleProcessor lifecycle = new BeanLifecycleProcessor();
    private final AnnotationBeanNameGenerator nameGenerator = new AnnotationBeanNameGenerator();

    // 存放 BeanDefinition（元数据）
    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    // 单例池（已经实例化好的对象）
    private final Map<String, Object> singletonObjects = new HashMap<>();
    // 存放processor
    private final Map<Class<? extends Annotation>, String> beanProcessorBeanNameMap = new HashMap<>();
    private final Map<Class<? extends Annotation>, String> fieldProcessorBeanNameMap = new HashMap<>();

    // ======================== Bean 定义注册方法 ========================

    @Override
    public BeanDefinition generateBeanDefinition(Class<?> clazz) {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanClass(clazz);
        beanDefinition.setBeanName(nameGenerator.generateBeanName(beanDefinition));
        return beanDefinition;
    }

    private void registerBeanDefinition(BeanDefinition beanDefinition) {
        String beanName = beanDefinition.getBeanName();
        if (beanDefinitionMap.containsKey(beanName)) {
            throw new IllegalArgumentException("Bean name already exists: " + beanName);
        }
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(Class<?> clazz) {
        List<BeanDefinition> candidates = new ArrayList<>();
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            BeanDefinition definition = entry.getValue();
            if (clazz.isAssignableFrom(definition.getBeanClass())) {
                candidates.add(definition);
            }
        }
        if (candidates.isEmpty()) {
            return null;
        }
        candidates.sort(Comparator.comparingInt(def -> ReflectionUtils
                .getTypeDistance(clazz, def.getBeanClass())));
        return candidates.get(0);
    }

    @Override
    public BeanDefinition registerBean(Class<?> clazz) {
        return registerBean((String) null, clazz);
    }

    @Override
    public BeanDefinition registerBean(String name, Class<?> clazz) {
        return registerBean(name, clazz, null);
    }

    @Override
    public BeanDefinition registerBean(Object instance) {
        return registerBean(null, instance.getClass(), instance);
    }

    @Override
    public BeanDefinition registerBean(String name, Object instance) {
        return registerBean(name, instance.getClass(), instance);
    }

    /**
     * 注册Bean到容器中，支持预实例化注册或延迟创建。
     * 如果实例为空则仅注册定义，实际对象将在首次获取时创建。
     * 如果实例不为空则立即注册并执行依赖注入。
     */
    private BeanDefinition registerBean(String name, Class<?> clazz, Object instance) {
        // 生成Bean定义信息，包含类型和默认名称
        BeanDefinition beanDefinition = generateBeanDefinition(clazz);

        // 如果指定了自定义名称，则覆盖默认生成的名称
        if (name != null && !name.isEmpty()) {
            beanDefinition.setBeanName(name);
        }

        // 检查单例池中是否已存在同名Bean实例，避免重复注册
        if (singletonObjects.containsKey(beanDefinition.getBeanName())) {
            throw new IllegalArgumentException(
                    "Bean name already exists in singleton pool: " + beanDefinition.getBeanName());
        }

        // 如果Bean定义已存在，则复用现有定义；否则注册新定义并注册相关处理器
        if (beanDefinitionMap.containsKey(beanDefinition.getBeanName())) {
            // 复用已存在的Bean定义
            beanDefinition = beanDefinitionMap.get(beanDefinition.getBeanName());
        } else {
            // 注册新的Bean定义到定义池
            registerBeanDefinition(beanDefinition);
            // 注册该类相关的注解处理器（如果有的话）
            registerProcessors(clazz, beanDefinition.getBeanName());
        }

        // 如果提供了实例，立即注册到单例池并执行依赖注入
        if (instance != null) {
            // 将实例放入单例池
            singletonObjects.put(beanDefinition.getBeanName(), instance);
            try {
                // 执行依赖注入和生命周期回调
                injectBeanInstance(beanDefinition, instance);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to inject bean instance: " + beanDefinition.getBeanName(), e);
            }
        }
        return beanDefinition;
    }

    // ======================== Bean 注销方法 ========================

    public void unregisterBean(Class<?> clazz) {
        BeanDefinition definition = getBeanDefinition(clazz);
        unregisterBean(definition.getBeanName());
    }

    public void unregisterBean(Object instance) {
        for (Map.Entry<String, Object> entry : singletonObjects.entrySet()) {
            if (entry.getValue() != instance) {
                continue;
            }
            unregisterBean(entry.getKey());
        }
    }

    public void unregisterBean(String name) {
        beanProcessorBeanNameMap.values().removeIf(name::equals);
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
    // ======================== Bean 获取方法 ========================

    public Object loadBean(String name) {
        return getBean(name);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        BeanDefinition definition = getBeanDefinition(requiredType);
        return getBean(definition.getBeanName(), requiredType);
    }

    @Override
    public <T> T getBean(String name, Class<T> clazz) {
        return clazz.cast(getBean(name));
    }

    @Override
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
            registerBean(beanName, definition.getBeanClass(), instance);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to create bean: " + beanName, e);
        }
        return instance;
    }

    // ======================== Bean 创建与注入方法 ========================

    private Object createBean(BeanDefinition definition)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = definition.getBeanClass();
        Object instance = clazz.getConstructor().newInstance();
        return instance;
    }

    private void injectBeanInstance(BeanDefinition definition, Object instance) throws ReflectiveOperationException {
        Class<?> clazz = definition.getBeanClass();
        lifecycle.invokeCreate(instance);
        fieldInjector.inject(instance, this, this, this);
        lifecycle.invokeReady(instance);
        processBeanAnnotations(clazz, instance);
    }

    @SuppressWarnings("unchecked")
    private void processBeanAnnotations(Class<?> clazz, Object instance) {
        for (Annotation annotation : clazz.getAnnotations()) {
            BeanAnnotationProcessor processor = getBeanAnnotationProcessor(annotation.annotationType());
            if (processor != null) {
                processor.postProcess(annotation, instance);
            }
        }
    }

    // ======================== 处理器相关方法 ========================

    private void registerProcessors(Class<?> clazz, String beanName) {
        if (clazz.isAnnotationPresent(ProcessBeanForAnnotation.class)) {
            beanProcessorBeanNameMap.put(clazz.getAnnotation(ProcessBeanForAnnotation.class).value(), beanName);
        }
        if (clazz.isAnnotationPresent(ProcessFieldForAnnotation.class)) {
            fieldProcessorBeanNameMap.put(clazz.getAnnotation(ProcessFieldForAnnotation.class).value(), beanName);
        }
    }

    /**
     * 根据指定的注解类型获取对应的字段注解处理器。
     */
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

    /**
     * 根据指定的注解类型获取对应的类注解处理器。
     */
    @SuppressWarnings("unchecked")
    public <T extends Annotation> BeanAnnotationProcessor<T> getBeanAnnotationProcessor(Class<T> annotationType) {
        String processorBeanName = beanProcessorBeanNameMap.get(annotationType);
        if (processorBeanName == null) {
            return null;
        }
        Object processorBean = getBean(processorBeanName);
        if (!(processorBean instanceof BeanAnnotationProcessor)) {
            throw new IllegalStateException("Bean " + processorBeanName + " is not a BeanAnnotationProcessor");
        }
        return (BeanAnnotationProcessor<T>) processorBean;
    }
}
