package cn.hyrkg.fastspigot3.test.lifecycle;

import cn.hyrkg.fastspigot3.context.annotation.Inject;
import cn.hyrkg.fastspigot3.context.annotation.OnReady;
import cn.hyrkg.fastspigot3.context.annotation.Provide;
import cn.hyrkg.fastspigot3.stereotype.Component;

@Component
public class LifecycleTest {
    @Inject
    ComponentA componentA;

    @OnReady
    public void onReady() {
        System.out.println(componentA);
    }
}
