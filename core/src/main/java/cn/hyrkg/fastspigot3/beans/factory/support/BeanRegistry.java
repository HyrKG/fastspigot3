package cn.hyrkg.fastspigot3.beans.factory.support;

import cn.hyrkg.fastspigot3.beans.factory.config.BeanDefinition;

public interface BeanRegistry {
    BeanDefinition registerBean(Class<?> clazz);

    BeanDefinition registerBean(String name, Class<?> clazz);


    BeanDefinition registerBeanInstance(Class<?> clazz, Object instance);

    BeanDefinition registerBeanInstance(String name, Class<?> clazz, Object instance);
}
