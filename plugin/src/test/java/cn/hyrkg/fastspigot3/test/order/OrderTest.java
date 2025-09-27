package cn.hyrkg.fastspigot3.test.order;

import cn.hyrkg.fastspigot3.context.annotation.Autowired;
import cn.hyrkg.fastspigot3.context.annotation.OnReady;
import cn.hyrkg.fastspigot3.stereotype.Component;

@Component
public class OrderTest {
    @Autowired
    private TestComponent component;

    @OnReady
    public void onReady() {
        System.out.println("OrderTest:onReady->component=" + (component != null));
    }
}
