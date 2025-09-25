package cn.hyrkg.fastspigot3.beans.factory;

/**
 * Bean 工厂接口：定义 Bean 的创建、注册、查询与生命周期管理能力。
 */
public interface BeanFactory {

    <T> T registerBean(Class<T> clazz);

    void registerBeanInstance(Class<?> clazz, Object instance);

    void unregisterBean(Class<?> clazz);

    <T> T getOrRegisterBean(Class<T> clazz);

    <T> T getBean(Class<T> clazz);
}
