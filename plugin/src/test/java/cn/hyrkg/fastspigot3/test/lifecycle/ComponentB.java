package cn.hyrkg.fastspigot3.test.lifecycle;

import cn.hyrkg.fastspigot3.annotation.lifecycle.OnCreate;
import cn.hyrkg.fastspigot3.annotation.lifecycle.OnReady;

public class ComponentB {

    @OnCreate
    private void created() {
        System.out.println("B:onCreate");
    }

    @OnReady
    private void ready() {
        System.out.println("B:onReady");
    }
}


