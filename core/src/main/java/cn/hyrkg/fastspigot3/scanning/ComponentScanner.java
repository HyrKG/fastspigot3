package cn.hyrkg.fastspigot3.scanning;

import cn.hyrkg.fastspigot3.annotation.Component;

import java.lang.reflect.Modifier;
import java.util.List;

/**
 * 封装类扫描与可注册判断逻辑。
 */
public class ComponentScanner {

    private final ClassPathScanner scanner = new ClassPathScanner();

    public List<Class<?>> scan(String basePackage) {
        List<Class<?>> scan = scanner.scan(basePackage);
        scan.removeIf(clazz -> !isRegistrable(clazz));
        return scan;
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
        return false;
    }
}


