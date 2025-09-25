package cn.hyrkg.fastspigot3.test.lifecycle;

import cn.hyrkg.fastspigot3.context.annotation.OnCreate;
import cn.hyrkg.fastspigot3.context.annotation.OnReady;

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


