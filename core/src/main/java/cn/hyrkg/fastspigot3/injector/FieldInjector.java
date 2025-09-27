package cn.hyrkg.fastspigot3.injector;

import cn.hyrkg.fastspigot3.beans.factory.BeanFactory;
import cn.hyrkg.fastspigot3.beans.factory.config.BeanDefinition;
import cn.hyrkg.fastspigot3.beans.factory.support.BeanDefinitionRegistry;
import cn.hyrkg.fastspigot3.beans.factory.support.BeanProcessorRegistry;
import cn.hyrkg.fastspigot3.context.annotation.Autowired;
import cn.hyrkg.fastspigot3.context.annotation.Inject;
import cn.hyrkg.fastspigot3.context.annotation.Instance;
import cn.hyrkg.fastspigot3.context.annotation.processor.FieldAnnotationProcessor;
import cn.hyrkg.fastspigot3.context.annotation.processor.ProcessorAction;
import cn.hyrkg.fastspigot3.util.ReflectionUtils;
import com.google.common.base.Preconditions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * 字段注入器：负责处理 Bean 初始化时的字段级依赖注入工作。
 */
public class FieldInjector implements Injector {

    @Override
    public void inject(Object bean, BeanFactory factory, BeanDefinitionRegistry definitionRegistry, BeanProcessorRegistry processorRegistry) {
        try {
            Class<?> clazz = bean.getClass();

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {

                boolean stopInternalProcessing = false;
                // Pre-process field annotations
                for (Annotation annotation : field.getAnnotations()) {
                    FieldAnnotationProcessor fieldAnnotationProcessor = processorRegistry.getFieldAnnotationProcessor(annotation.annotationType());
                    if (fieldAnnotationProcessor != null && fieldAnnotationProcessor.preProcess(field, annotation, bean) == ProcessorAction.STOP_PROCESSING) {
                        stopInternalProcessing = true;
                    }
                }

                if (!stopInternalProcessing) {
                    if (field.isAnnotationPresent(Instance.class)) {
                        Preconditions.checkArgument(field.getType().isAssignableFrom(clazz), "Field %s is not assignable from %s", field.getName(), clazz.getName());
                        ReflectionUtils.setFieldValue(field, bean, bean);
                    }

                    if (field.isAnnotationPresent(Inject.class)) {
                        Inject injectAnno = field.getAnnotation(Inject.class);
                        Object fieldValue = ReflectionUtils.getFieldValue(field, bean);
                        if (fieldValue != null) {
                            definitionRegistry.registerBean(injectAnno.value(), fieldValue);
                        } else {
                            BeanDefinition beanDefinition = definitionRegistry.registerBean(injectAnno.value(), field.getType());
                            Object beanToInject = factory.getBean(beanDefinition.getBeanName());
                            ReflectionUtils.setFieldValue(field, bean, beanToInject);
                        }
                    }

                    if (field.isAnnotationPresent(Autowired.class)) {
                        Autowired autowiredAnno = field.getAnnotation(Autowired.class);
                        BeanDefinition definition = autowiredAnno.value().isEmpty() ? definitionRegistry.getBeanDefinition(field.getType()) : definitionRegistry.getBeanDefinition(autowiredAnno.value());
                        Preconditions.checkArgument(definition != null, "No qualifying bean of type %s found for dependency %s in %s", field.getType().getName(), field.getName(), clazz.getName());
                        Object beanToWire = factory.getBean(definition.getBeanName());
                        ReflectionUtils.setFieldValue(field, bean, beanToWire);
                    }
                }

                // Post-process field annotations
                for (Annotation annotation : field.getAnnotations()) {
                    FieldAnnotationProcessor fieldAnnotationProcessor = processorRegistry.getFieldAnnotationProcessor(annotation.annotationType());
                    if (fieldAnnotationProcessor != null) {
                        fieldAnnotationProcessor.postProcess(field, annotation, bean);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
