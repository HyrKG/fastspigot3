package cn.hyrkg.fastspigot3.context.processor;

import cn.hyrkg.fastspigot3.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

/**
 * 组件解析器：标记一个类为特定组件类型的解析器
 * 被此注解标记的类将被用于处理指定类型的组件
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentProcessor {
    /**
     * 要解析的组件注解类型
     */
    Class<? extends Annotation> value();
}