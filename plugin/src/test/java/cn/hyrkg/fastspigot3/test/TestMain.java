package cn.hyrkg.fastspigot3.test;

import cn.hyrkg.fastspigot3.context.ApplicationContext;

public class TestMain {

    public static void main(String[] args) {
        ApplicationContext context = new ApplicationContext();
        System.out.println(TestMain.class.getPackage().getName());
        context.scanAndRegister(TestMain.class.getPackage().getName());
    }
}
