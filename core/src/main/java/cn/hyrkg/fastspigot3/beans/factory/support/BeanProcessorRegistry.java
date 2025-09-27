package cn.hyrkg.fastspigot3.beans.factory.support;

import cn.hyrkg.fastspigot3.context.annotation.processor.BeanAnnotationProcessor;
import cn.hyrkg.fastspigot3.context.annotation.processor.FieldAnnotationProcessor;

import java.lang.annotation.Annotation;

public interface BeanProcessorRegistry {
    <T extends Annotation> FieldAnnotationProcessor<T> getFieldAnnotationProcessor(Class<T> annotationType);

    <T extends Annotation> BeanAnnotationProcessor<T> getBeanAnnotationProcessor(Class<T> annotationType);
}
