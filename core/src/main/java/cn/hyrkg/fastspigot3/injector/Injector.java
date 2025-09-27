package cn.hyrkg.fastspigot3.injector;

import cn.hyrkg.fastspigot3.beans.factory.BeanFactory;
import cn.hyrkg.fastspigot3.beans.factory.support.BeanDefinitionRegistry;
import cn.hyrkg.fastspigot3.beans.factory.support.BeanProcessorRegistry;

public interface Injector {
    void inject(Object bean, BeanFactory factory, BeanDefinitionRegistry definitionRegistry, BeanProcessorRegistry processorRegistry);
}
