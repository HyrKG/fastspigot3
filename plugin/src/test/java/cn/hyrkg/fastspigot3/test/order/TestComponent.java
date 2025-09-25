package cn.hyrkg.fastspigot3.test.order;

import cn.hyrkg.fastspigot3.annotation.Component;
import cn.hyrkg.fastspigot3.annotation.lifecycle.OnReady;

@Component
public class TestComponent {

    @OnReady
    public void hello() {
        System.out.println("TestComponent: onReady");
    }

}
