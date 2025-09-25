package cn.hyrkg.fastspigot3.test.order;

import cn.hyrkg.fastspigot3.context.annotation.Autowired;
import cn.hyrkg.fastspigot3.stereotype.Component;
import cn.hyrkg.fastspigot3.context.annotation.OnReady;

@Component
public class Order2Test {
    @Autowired
    private OrderTest component;

    @OnReady
    public void onReady() {
        System.out.println("OrderTest2:onReady->component=" + (component != null));
    }
}
