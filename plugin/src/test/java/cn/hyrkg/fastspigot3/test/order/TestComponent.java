package cn.hyrkg.fastspigot3.test.order;

import cn.hyrkg.fastspigot3.stereotype.Component;
import cn.hyrkg.fastspigot3.context.annotation.OnReady;

@Component
public class TestComponent {

    @OnReady
    public void hello() {
        System.out.println("TestComponent: onReady");
    }

}
