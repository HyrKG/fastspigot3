package cn.hyrkg.fastspigot3.core;

import cn.hyrkg.fastspigot3.annotation.Inject;
import cn.hyrkg.fastspigot3.annotation.Wire;
import cn.hyrkg.fastspigot3.scanner.ClassScanner;
import cn.hyrkg.fastspigot3.annotation.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 封装类扫描与可注册判断逻辑。
 */
public class ScanService {

    private final ClassScanner scanner = new ClassScanner();

    public Iterable<Class<?>> scan(String basePackage) {
        return scanner.scan(basePackage);
    }

    public boolean isRegistrable(Class<?> clazz) {
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            return false;
        }
        try {
            clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            return false;
        }
        if (clazz.isAnnotationPresent(Component.class)) {
            return true;
        }
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class) || field.isAnnotationPresent(Wire.class)) {
                return true;
            }
        }
        return false;
    }
}


