package cn.hyrkg.fastspigot3.beans.factory.support;

import cn.hyrkg.fastspigot3.beans.factory.config.BeanDefinition;

public interface BeanDefinitionRegistry {

    BeanDefinition generateBeanDefinition(Class<?> clazz);

    BeanDefinition getBeanDefinition(Class<?> clazz);

    BeanDefinition getBeanDefinition(String name);

    BeanDefinition registerBean(Class<?> clazz);

    BeanDefinition registerBean(Object instance);

    BeanDefinition registerBean(String name, Class<?> clazz);

    BeanDefinition registerBean(String name, Object instance);
}
