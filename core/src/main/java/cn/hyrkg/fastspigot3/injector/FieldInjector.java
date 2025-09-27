package cn.hyrkg.fastspigot3.injector;

import cn.hyrkg.fastspigot3.beans.factory.BeanFactory;
import cn.hyrkg.fastspigot3.beans.factory.DefaultBeanFactory;
import cn.hyrkg.fastspigot3.beans.factory.support.BeanRegistry;
import cn.hyrkg.fastspigot3.context.annotation.Autowired;
import cn.hyrkg.fastspigot3.context.annotation.Inject;
import cn.hyrkg.fastspigot3.context.annotation.Instance;
import cn.hyrkg.fastspigot3.context.annotation.processor.FieldAnnotationProcessor;
import cn.hyrkg.fastspigot3.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * 字段注入器：负责处理 Bean 初始化时的字段级依赖注入工作。
 */
public class FieldInjector implements Injector {


    @Override
    public void inject(Object bean, BeanFactory factory, BeanRegistry registry) {
        try {
            Class<?> clazz = bean.getClass();
            // 注入 @Instance：将当前 Bean 实例写入自身声明的静态字段
            for (Field annotatedField : ReflectionUtils.findAnnotatedFields(clazz, Instance.class)) {
                if (!annotatedField.getType().isAssignableFrom(clazz)) {
                    throw new RuntimeException(
                            "Field " + annotatedField.getName() + " is not assignable from " + clazz.getName());
                }
                ReflectionUtils.setFieldValue(annotatedField, bean, bean);
            }

            // 注入 @Inject：优先复用已有实例，否则触发 BeanFactory 创建并注入
            for (Field annotatedField : ReflectionUtils.findAnnotatedFields(clazz, Inject.class)) {
                Inject inject = annotatedField.getAnnotation(Inject.class);
                Object fieldValue = ReflectionUtils.getFieldValue(annotatedField, bean);
                if (fieldValue != null) {
                    if (inject.value().isEmpty()) {
                        registry.registerBeanInstance(annotatedField.getType(), fieldValue);
                    } else {
                        registry.registerBeanInstance(inject.value(), annotatedField.getType(), fieldValue);
                    }
                } else {
                    Object injectBean;
                    if (inject.value().isEmpty()) {
                        registry.registerBean(annotatedField.getType());
                        injectBean = factory.getBean(annotatedField.getType());
                    } else {
                        registry.registerBean(inject.value(), annotatedField.getType());
                        injectBean = factory.getBean(inject.value(), annotatedField.getType());
                    }
                    ReflectionUtils.setFieldValue(annotatedField, bean, injectBean);
                }
            }

            // 处理默认的 @Autowired 注入，并记录是否已经处理过
            boolean autowiredProcessed = processFieldAnnotation(clazz, Autowired.class, bean, factory, registry);
            // 处理自定义字段注解的注入逻辑
            processFieldWithCustomProcessors(clazz, bean, factory, registry, autowiredProcessed);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 处理指定注解类型的字段注入。
     */
    private <T extends Annotation> boolean processFieldAnnotation(Class<?> clazz,
                                                                  Class<T> annotationType,
                                                                  Object bean,
                                                                  BeanFactory factory,
                                                                  BeanRegistry registry) throws IllegalAccessException {
        if (!Annotation.class.isAssignableFrom(annotationType)) {
            return false;
        }
        boolean processed = false;
        for (Field field : ReflectionUtils.findAnnotatedFields(clazz, annotationType)) {
            processSingleField(field, annotationType, bean, factory, registry);
            processed = true;
        }
        return processed;
    }

    /**
     * 遍历并执行注册到 BeanFactory 上的自定义字段处理器。
     */
    private void processFieldWithCustomProcessors(Class<?> clazz,
                                                  Object bean,
                                                  BeanFactory factory,
                                                  BeanRegistry registry,
                                                  boolean autowiredProcessed) throws IllegalAccessException {
        if (!(factory instanceof DefaultBeanFactory)) {
            return;
        }
        DefaultBeanFactory defaultBeanFactory = (DefaultBeanFactory) factory;
        Set<Class<? extends Annotation>> processorAnnotations = defaultBeanFactory.getFieldProcessorAnnotationTypes();
        for (Class<? extends Annotation> annotationType : processorAnnotations) {
            if (annotationType == Autowired.class && autowiredProcessed) {
                continue;
            }
            processFieldAnnotation(clazz, annotationType, bean, factory, registry);
        }
    }

    /**
     * 处理单个字段的注入逻辑：先尝试交给自定义处理器，否则执行默认注入。
     */
    private <T extends Annotation> void processSingleField(Field field,
                                                           Class<T> annotationType,
                                                           Object bean,
                                                           BeanFactory factory,
                                                           BeanRegistry registry) throws IllegalAccessException {
        Annotation annotation = field.getAnnotation(annotationType);
        if (annotation == null) {
            return;
        }
        // 优先使用自定义处理器
        if (factory instanceof DefaultBeanFactory) {
            DefaultBeanFactory defaultBeanFactory = (DefaultBeanFactory) factory;
            FieldAnnotationProcessor<T> processor = defaultBeanFactory.getFieldAnnotationProcessor(annotationType);
            if (processor != null) {
                processor.postProcess(field, annotationType.cast(annotation), bean, factory, registry);
                return;
            }
        }

        // 默认处理逻辑：目前仅支持 @Autowired
        if (annotationType == Autowired.class) {
            Autowired autowired = (Autowired) annotation;
            Object injectBean = autowired.value().isEmpty()
                    ? factory.getBean(field.getType())
                    : factory.getBean(autowired.value(), field.getType());
            ReflectionUtils.setFieldValue(field, bean, injectBean);
        }
    }

}
