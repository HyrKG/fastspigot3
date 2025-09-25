package cn.hyrkg.fastspigot3.test.lifecycle;

import cn.hyrkg.fastspigot3.annotation.Inject;
import cn.hyrkg.fastspigot3.annotation.lifecycle.OnReady;

public class LifecycleTest {
    @Inject
    ComponentA componentA;

    @OnReady
    public void onReady() {
        System.out.println(componentA);
    }
}
