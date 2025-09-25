package cn.hyrkg.fastspigot3.test.order;

import cn.hyrkg.fastspigot3.framework.annotation.Autowired;
import cn.hyrkg.fastspigot3.framework.annotation.Component;
import cn.hyrkg.fastspigot3.framework.annotation.lifecycle.OnReady;

@Component
public class OrderTest {
    @Autowired
    private TestComponent component;

    @OnReady
    public void onReady() {
        System.out.println("OrderTest:onReady->component=" + (component != null));
    }
}
