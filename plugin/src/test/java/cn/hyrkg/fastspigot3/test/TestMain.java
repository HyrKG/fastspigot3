package cn.hyrkg.fastspigot3.test;

import cn.hyrkg.fastspigot3.context.FastApplicationContext;

public class TestMain {

    public static void main(String[] args) {
        FastApplicationContext context = new FastApplicationContext();
        context.scanAndRegister("cn.hyrkg.fastspigot3.test");
    }
}
