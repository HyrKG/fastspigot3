package cn.hyrkg.fastspigot3.core;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BeanFactory {
    private final BeanManager manager;

    public <T> T createBean(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create bean for class: " + clazz.getName(), e);
        }
    }
}
