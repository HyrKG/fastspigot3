package cn.hyrkg.fastspigot3.context.processor;

public interface BeanProcessor<T> {
    void process(T annotation, Object bean);
}
