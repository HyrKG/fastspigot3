package cn.hyrkg.fastspigot3.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 提供一个 Bean：字段非空则注册该实例；字段为空则注册类型并创建后注入。
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Provide {
    String value() default "";
}

