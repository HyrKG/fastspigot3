package cn.hyrkg.fastspigot3.core;

import java.util.HashMap;

public class BeanManager {

    private HashMap<Class<?>, Object> registeredBeanMap = new HashMap<>();


    public void registerBean(Class<?> clazz) {
        if (registeredBeanMap.containsKey(clazz)) {
            unregisterBean(clazz, registeredBeanMap.get(clazz));
        }


    }

    public void unregisterBean(Class<?> clazz, Object bean) {
        registeredBeanMap.remove(clazz);
    }
}
