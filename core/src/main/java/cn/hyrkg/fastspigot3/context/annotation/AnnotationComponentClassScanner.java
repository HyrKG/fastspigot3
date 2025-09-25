package cn.hyrkg.fastspigot3.context.annotation;

import cn.hyrkg.fastspigot3.scanner.ClassPathScanner;
import cn.hyrkg.fastspigot3.scanner.Scanner;
import cn.hyrkg.fastspigot3.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


public class AnnotationComponentClassScanner implements Scanner {

    private ClassPathScanner classPathScanner = new ClassPathScanner();

    @Override
    public List<Class<?>> scan(String path) {
        List<Class<?>> classes = new ArrayList<>();
        for (Class<?> aClass : classPathScanner.scan(path)) {
            if (aClass.isAnnotationPresent(Component.class)) {
                classes.add(aClass);
            }
        }
        return classes;
    }

    @Override
    public List<Class<?>> scan(String path, Class<?> anchorClass) {
        List<Class<?>> classes = new ArrayList<>();
        for (Class<?> aClass : classPathScanner.scan(path, anchorClass)) {
            if (aClass.isAnnotationPresent(Component.class)) {
                classes.add(aClass);
            }
        }
        return classes;
    }

    @Override
    public List<Class<?>> scan(String path, ClassLoader preferredLoader) {
        List<Class<?>> classes = new ArrayList<>();
        for (Class<?> aClass : classPathScanner.scan(path, preferredLoader)) {
            if (aClass.isAnnotationPresent(Component.class)) {
                classes.add(aClass);
            }
        }
        return classes;
    }
}
