package cn.hyrkg.fastspigot3;

import cn.hyrkg.fastspigot3.scanner.ClassScanner;

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
