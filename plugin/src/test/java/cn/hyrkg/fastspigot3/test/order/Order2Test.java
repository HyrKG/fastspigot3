package cn.hyrkg.fastspigot3.test.order;

import cn.hyrkg.fastspigot3.framework.annotation.Autowired;
import cn.hyrkg.fastspigot3.framework.annotation.Component;
import cn.hyrkg.fastspigot3.framework.annotation.lifecycle.OnReady;

@Component
public class Order2Test {
    @Autowired
    private OrderTest component;

    @OnReady
    public void onReady() {
        System.out.println("OrderTest2:onReady->component=" + (component != null));
    }
}
