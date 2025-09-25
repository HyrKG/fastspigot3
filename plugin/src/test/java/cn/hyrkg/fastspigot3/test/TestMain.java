package cn.hyrkg.fastspigot3.test;

import cn.hyrkg.fastspigot3.annotation.Inject;
import cn.hyrkg.fastspigot3.core.BeanManager;

public class TestMain {

    public static void main(String[] args) {
        BeanManager beanManager = new BeanManager();
        beanManager.scanAndRegister("cn.hyrkg.fastspigot3.test");
    }
}
