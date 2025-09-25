package cn.hyrkg.fastspigot3.injector;

import cn.hyrkg.fastspigot3.beans.factory.BeanFactory;
import cn.hyrkg.fastspigot3.beans.factory.support.BeanRegistry;

public interface Injector {
    void inject(Object bean, BeanFactory factory, BeanRegistry registry);
}
