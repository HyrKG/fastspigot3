package cn.hyrkg.fastspigot3.test.order;

import cn.hyrkg.fastspigot3.context.annotation.OnReady;
import cn.hyrkg.fastspigot3.stereotype.Component;

@Component
public class TestComponent {

    @OnReady
    public void hello() {
        System.out.println("TestComponent: onReady");
    }

}
