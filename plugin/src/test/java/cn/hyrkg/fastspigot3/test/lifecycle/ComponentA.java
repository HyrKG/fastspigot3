package cn.hyrkg.fastspigot3.test.lifecycle;

import cn.hyrkg.fastspigot3.framework.annotation.Autowired;
import cn.hyrkg.fastspigot3.framework.annotation.lifecycle.OnCreate;
import cn.hyrkg.fastspigot3.framework.annotation.lifecycle.OnReady;

public class ComponentA {

    @Autowired
    private ComponentB b;

    @OnCreate
    private void created() {
        LifeRecorder.record("A:onCreate");
    }

    @OnReady
    private void ready() {
        LifeRecorder.record("A:onReady->b=" + (b != null));
    }
}


