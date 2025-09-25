package cn.hyrkg.fastspigot3.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 生命周期回调：当 Bean 由容器构造完成后立即调用。
 * 要求：无参方法；可为私有。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnCreate {
}
