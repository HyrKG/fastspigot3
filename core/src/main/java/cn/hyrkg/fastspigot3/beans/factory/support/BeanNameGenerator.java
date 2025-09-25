package cn.hyrkg.fastspigot3.beans.factory.support;

import cn.hyrkg.fastspigot3.beans.factory.config.BeanDefinition;

public interface BeanNameGenerator {
    String generateBeanName(BeanDefinition definition);
}
