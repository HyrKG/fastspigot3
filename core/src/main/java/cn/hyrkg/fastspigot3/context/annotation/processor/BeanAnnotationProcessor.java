package cn.hyrkg.fastspigot3.context.annotation.processor;

import cn.hyrkg.fastspigot3.beans.factory.BeanFactory;
import cn.hyrkg.fastspigot3.beans.factory.config.BeanDefinition;

public interface BeanAnnotationProcessor<T> {
    void postProcess(T annotation, Object bean);
}
