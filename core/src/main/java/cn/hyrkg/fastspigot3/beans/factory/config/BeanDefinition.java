package cn.hyrkg.fastspigot3.beans.factory.config;

import lombok.Data;

@Data
public class BeanDefinition {
    private String beanName;
    private Class<?> beanClass;
}
