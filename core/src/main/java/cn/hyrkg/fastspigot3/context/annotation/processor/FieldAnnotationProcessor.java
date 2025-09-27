package cn.hyrkg.fastspigot3.context.annotation.processor;

import cn.hyrkg.fastspigot3.beans.factory.BeanFactory;
import cn.hyrkg.fastspigot3.beans.factory.support.BeanRegistry;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * 字段注解处理器，用于实现自定义字段注入逻辑。
 */
public interface FieldAnnotationProcessor<T extends Annotation> {

    void postProcess(Field field, T annotation, Object bean, BeanFactory factory, BeanRegistry registry);

}

