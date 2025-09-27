package cn.hyrkg.fastspigot3.test.order;

import cn.hyrkg.fastspigot3.context.annotation.Autowired;
import cn.hyrkg.fastspigot3.context.annotation.OnReady;
import cn.hyrkg.fastspigot3.stereotype.Component;

@Component
public class Order2Test {
    @Autowired
    private OrderTest component;

    @OnReady
    public void onReady() {
        System.out.println("OrderTest2:onReady->component=" + (component != null));
    }
}
