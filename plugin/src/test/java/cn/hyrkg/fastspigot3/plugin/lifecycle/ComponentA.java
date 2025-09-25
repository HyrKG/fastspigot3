package cn.hyrkg.fastspigot3.plugin.lifecycle;

import cn.hyrkg.fastspigot3.annotation.Inject;
import cn.hyrkg.fastspigot3.annotation.OnCreate;
import cn.hyrkg.fastspigot3.annotation.OnReady;

public class ComponentA {

    @Inject
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


