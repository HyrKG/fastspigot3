package cn.hyrkg.fastspigot3.beans.factory;

/**
 * Bean 工厂接口：定义 Bean 的创建、注册、查询与生命周期管理能力。
 */
public interface BeanFactory {
    Object getBean(String name);

    <T> T getBean(Class<T> requiredType);

    <T> T getBean(String name, Class<T> clazz);
}
