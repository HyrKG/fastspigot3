package cn.hyrkg.fastspigot3.beans.factory.support;

import cn.hyrkg.fastspigot3.context.annotation.processor.BeanAnnotationProcessor;
import cn.hyrkg.fastspigot3.context.annotation.processor.FieldAnnotationProcessor;

import java.lang.annotation.Annotation;
import java.util.List;

public interface BeanProcessorRegistry {
    <T extends Annotation> List<FieldAnnotationProcessor<T>> getFieldAnnotationProcessor(Class<T> annotationType);

    <T extends Annotation> List<BeanAnnotationProcessor<T>> getBeanAnnotationProcessor(Class<T> annotationType);
}
