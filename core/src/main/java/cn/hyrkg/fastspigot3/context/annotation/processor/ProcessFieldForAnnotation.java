package cn.hyrkg.fastspigot3.context.annotation.processor;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段解析器：标记一个类用于处理特定注解标记的字段注入逻辑。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProcessFieldForAnnotation {

    Class<? extends Annotation> value();

}

