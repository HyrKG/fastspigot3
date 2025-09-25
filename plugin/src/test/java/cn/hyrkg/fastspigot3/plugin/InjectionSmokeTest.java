package cn.hyrkg.fastspigot3.plugin;

import cn.hyrkg.fastspigot3.core.BeanManager;
import cn.hyrkg.fastspigot3.plugin.example.ExampleConsumer;
import cn.hyrkg.fastspigot3.plugin.example.ExampleService;

public class InjectionSmokeTest {

    public static void main(String[] args) {
        BeanManager beanManager = new BeanManager();
        beanManager.scanAndRegister("cn.hyrkg.fastspigot3");

        ExampleConsumer consumer = new ExampleConsumer();
        beanManager.wireInto(consumer);

        System.out.println("Injection check: " + consumer.hello());
        ExampleService svc = beanManager.getBean(ExampleService.class);
        System.out.println("Service in container: " + (svc != null));
    }
}


