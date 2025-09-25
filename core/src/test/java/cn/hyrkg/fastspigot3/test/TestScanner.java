package cn.hyrkg.fastspigot3.test;

import cn.hyrkg.fastspigot3.scanning.ClassPathScanner;

import java.util.List;

public class TestScanner {
    public static void main(String[] args) {
        ClassPathScanner scanner = new ClassPathScanner();
        List<Class<?>> classes = scanner.scan("cn.hyrkg.fastspigot3");
        for (Class<?> clazz : classes) {
            System.out.println(clazz.getName());
        }
    }
}
