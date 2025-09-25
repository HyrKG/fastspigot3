package cn.hyrkg.fastspigot3.test.lifecycle;

import cn.hyrkg.fastspigot3.annotation.Inject;
import cn.hyrkg.fastspigot3.annotation.lifecycle.OnCreate;
import cn.hyrkg.fastspigot3.annotation.lifecycle.OnReady;

public class ComponentA {

    @Inject
    private ComponentB b;

    @OnCreate
    private void created() {
        System.out.println("A:onCreate");
    }

    @OnReady
    private void ready() {
        System.out.println("A:onReady->b=" + (b != null));
    }
}


