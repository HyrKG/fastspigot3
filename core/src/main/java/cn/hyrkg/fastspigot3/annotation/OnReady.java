package cn.hyrkg.fastspigot3.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 生命周期回调：当该对象及其依赖全部注入/连接完成后调用。
 * 适用于依赖就绪后才能执行的正式逻辑（如开始任务、对外提供服务）。
 * 要求：无参方法；可为私有。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnReady {
}


