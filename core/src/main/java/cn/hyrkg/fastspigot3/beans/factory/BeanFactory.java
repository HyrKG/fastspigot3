package cn.hyrkg.fastspigot3.beans.factory;

import cn.hyrkg.fastspigot3.beans.factory.config.BeanDefinition;

/**
 * Bean 工厂接口：定义 Bean 的创建、注册、查询与生命周期管理能力。
 */
public interface BeanFactory {
    Object getBean(String name);

    Object getBean(String name, Class<?> clazz);

    <T> T getBean(Class<T> requiredType);
}
