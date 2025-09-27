package cn.hyrkg.fastspigot3.context.annotation.processor;

public interface BeanAnnotationProcessor<T> {
    default void preProcess(T annotation, Object bean) {
    }

    default void postProcess(T annotation, Object bean) {
    }
}
