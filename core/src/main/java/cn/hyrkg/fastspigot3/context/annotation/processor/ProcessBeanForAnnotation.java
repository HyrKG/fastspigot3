package cn.hyrkg.fastspigot3.context.annotation.processor;

import java.lang.annotation.*;

/**
 * 组件解析器：标记一个类为特定组件类型的解析器
 * 被此注解标记的类将被用于处理指定类型的组件
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProcessBeanForAnnotation {

    /**
     * 要处理的注解类型，如果不指定（默认值为Annotation.class），则表示处理所有Bean。
     */
    Class<? extends Annotation> value() default Annotation.class;

}