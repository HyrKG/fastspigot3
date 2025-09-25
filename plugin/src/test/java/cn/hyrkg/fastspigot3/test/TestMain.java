package cn.hyrkg.fastspigot3.test;

import cn.hyrkg.fastspigot3.framework.beans.BeanManager;

public class TestMain {

    public static void main(String[] args) {
        BeanManager beanManager = new BeanManager();
        beanManager.scanAndRegister("cn.hyrkg.fastspigot3.test");
    }
}
