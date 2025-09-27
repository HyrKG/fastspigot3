package cn.hyrkg.fastspigot3.context.annotation.processor;

import java.lang.annotation.*;

/**
 * 组件解析器：标记一个类为特定组件类型的解析器
 * 被此注解标记的类将被用于处理指定类型的组件
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProcessBeanForAnnotation {

    Class<? extends Annotation> value();

}