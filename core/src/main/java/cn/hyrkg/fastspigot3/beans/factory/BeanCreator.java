package cn.hyrkg.fastspigot3.beans.factory;

/**
 * Bean 创建器：负责通过反射创建 Bean 实例。
 */
public class BeanCreator {

    public <T> T createBean(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create bean for class: " + clazz.getName(), e);
        }
    }
}
