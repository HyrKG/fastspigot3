package cn.hyrkg.fastspigot3.context.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bean 卸载（销毁）阶段回调标记。
 * 被该注解标记的无参方法将在 Bean 从容器卸载前被调用。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnDestroy {
}


