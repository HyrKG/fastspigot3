package cn.hyrkg.fastspigot3.test.lifecycle;

import cn.hyrkg.fastspigot3.context.annotation.OnCreate;
import cn.hyrkg.fastspigot3.context.annotation.OnReady;
import cn.hyrkg.fastspigot3.context.annotation.Provide;

public class ComponentA {

    @Provide
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


