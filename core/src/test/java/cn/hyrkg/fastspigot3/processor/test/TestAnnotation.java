package cn.hyrkg.fastspigot3.processor.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 测试用的自定义注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestAnnotation {
    /**
     * 测试值
     */
    String value() default "test";
    
    /**
     * 优先级
     */
    int priority() default 0;
}
