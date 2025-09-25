package cn.hyrkg.fastspigot3.beans.factory.support;

import cn.hyrkg.fastspigot3.beans.factory.config.BeanDefinition;

public interface BeanDefinitionFactory {
    BeanDefinition createBeanDefinition(Class<?> clazz);
}
