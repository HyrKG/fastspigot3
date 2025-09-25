package cn.hyrkg.fastspigot3.context.annotation;

import cn.hyrkg.fastspigot3.beans.factory.config.BeanDefinition;
import cn.hyrkg.fastspigot3.beans.factory.support.BeanNameGenerator;
import cn.hyrkg.fastspigot3.stereotype.Component;

public class AnnotationBeanNameGenerator implements BeanNameGenerator {
    @Override
    public String generateBeanName(BeanDefinition definition) {
        Class<?> clazz = definition.getBeanClass();
        if (clazz.isAnnotationPresent(Component.class)) {
            Component annotation = clazz.getAnnotation(Component.class);
            if (annotation != null && !annotation.value().isEmpty()) {
                return annotation.value();
            }
        } else if (clazz.isAnnotationPresent(Inject.class)) {
            Inject annotation = clazz.getAnnotation(Inject.class);
            if (annotation != null && !annotation.value().isEmpty()) {
                return annotation.value();
            }
        }
        return resolveBeanNameFromClass(clazz);
    }


    public String resolveBeanNameFromClass(Class<?> clazz) {
        return clazz.getName();
    }
}
