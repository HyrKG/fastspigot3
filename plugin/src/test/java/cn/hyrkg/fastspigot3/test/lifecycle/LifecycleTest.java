package cn.hyrkg.fastspigot3.test.lifecycle;

import cn.hyrkg.fastspigot3.framework.annotation.Component;
import cn.hyrkg.fastspigot3.framework.annotation.Autowired;
import cn.hyrkg.fastspigot3.framework.annotation.lifecycle.OnReady;

@Component
public class LifecycleTest {
    @Autowired
    ComponentA componentA;

    @OnReady
    public void onReady() {
        System.out.println(componentA);
    }
}
