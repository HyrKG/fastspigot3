package cn.hyrkg.fastspigot3.framework.test;

import cn.hyrkg.fastspigot3.framework.scanning.ClassScanner;

import java.util.List;

public class TestScanner {
    public static void main(String[] args) {
        ClassScanner scanner = new ClassScanner();
        List<Class<?>> classes = scanner.scan("cn.hyrkg.fastspigot3");
        for (Class<?> clazz : classes) {
            System.out.println(clazz.getName());
        }
    }
}
