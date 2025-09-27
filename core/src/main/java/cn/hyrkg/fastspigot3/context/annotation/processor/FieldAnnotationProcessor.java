package cn.hyrkg.fastspigot3.context.annotation.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * 字段注解处理器，用于实现自定义字段注入逻辑。
 */
public interface FieldAnnotationProcessor<T extends Annotation> {

    /**
     * @return 返回true表示拦截该字段其他注入，false表示不拦截
     */
    ProcessorAction preProcess(Field field, T annotation, Object bean);

    default void postProcess(Field field, T annotation, Object bean) {

    }
}

