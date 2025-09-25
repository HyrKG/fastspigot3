package cn.hyrkg.fastspigot3.test.lifecycle;

import cn.hyrkg.fastspigot3.annotation.lifecycle.OnCreate;
import cn.hyrkg.fastspigot3.annotation.lifecycle.OnReady;

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


