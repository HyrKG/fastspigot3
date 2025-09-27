package cn.hyrkg.fastspigot3.context.annotation.processor;

public interface BeanAnnotationProcessor<T> {
    void postProcess(T annotation, Object bean);
}
