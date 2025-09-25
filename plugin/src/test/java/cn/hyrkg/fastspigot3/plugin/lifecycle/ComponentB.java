package cn.hyrkg.fastspigot3.plugin.lifecycle;

import cn.hyrkg.fastspigot3.annotation.OnCreate;
import cn.hyrkg.fastspigot3.annotation.OnReady;

public class ComponentB {

    @OnCreate
    private void created() {
        LifeRecorder.record("B:onCreate");
    }

    @OnReady
    private void ready() {
        LifeRecorder.record("B:onReady");
    }
}


