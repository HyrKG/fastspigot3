package cn.hyrkg.fastspigot3.injector;

import cn.hyrkg.fastspigot3.beans.factory.support.BeanDefinitionFactory;
import cn.hyrkg.fastspigot3.beans.factory.support.BeanRegistry;
import cn.hyrkg.fastspigot3.context.annotation.Inject;
import cn.hyrkg.fastspigot3.context.annotation.Instance;
import cn.hyrkg.fastspigot3.context.annotation.Autowired;
import cn.hyrkg.fastspigot3.beans.factory.BeanFactory;
import cn.hyrkg.fastspigot3.util.ReflectionUtils;

import java.lang.reflect.Field;

public class FieldInjector implements Injector {


    @Override
    public void inject(Object bean, BeanFactory factory, BeanRegistry registry) {
        try {
            Class<?> clazz = bean.getClass();
            for (Field annotatedField : ReflectionUtils.findAnnotatedFields(clazz, Instance.class)) {
                if (!annotatedField.getType().isAssignableFrom(clazz)) {
                    throw new RuntimeException(
                            "Field " + annotatedField.getName() + " is not assignable from " + clazz.getName());
                }
                ReflectionUtils.setFieldValue(annotatedField, bean, bean);
            }

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
                        injectBean = factory.getBean(annotatedField.getType());
                    } else {
                        injectBean = factory.getBean(inject.value(), annotatedField.getType());
                    }
                    ReflectionUtils.setFieldValue(annotatedField, bean, injectBean);
                }
            }

            for (Field annotatedField : ReflectionUtils.findAnnotatedFields(clazz, Autowired.class)) {
                Autowired autowired = annotatedField.getAnnotation(Autowired.class);
                Object injectBean;
                if (autowired.value().isEmpty()) {
                    injectBean = factory.getBean(annotatedField.getType());
                } else {
                    injectBean = factory.getBean(autowired.value(), annotatedField.getType());
                }
                ReflectionUtils.setFieldValue(annotatedField, bean, injectBean);
            }

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
