package cn.hyrkg.fastspigot3.spigot.util;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 插件工具类，提供从 Object 获取插件实例的方法
 */
public class PluginUtils {

    /**
     * 通过 ClassLoader 获取插件实例
     * @param classLoader 类的 ClassLoader
     * @return JavaPlugin 实例，如果找不到则返回 null
     */
    public static JavaPlugin getPluginFromClassLoader(ClassLoader classLoader) {
        try {
            // 通过反射查找插件实例
            for (Plugin plugin : org.bukkit.Bukkit.getPluginManager().getPlugins()) {
                if (plugin.getClass().getClassLoader().equals(classLoader)) {
                    return (JavaPlugin) plugin;
                }
            }
        } catch (Exception e) {
            System.err.println("获取插件实例时出错: " + e.getMessage());
        }
        return null;
    }

    /**
     * 通过对象获取其所属的插件实例
     * @param obj 任意对象
     * @return JavaPlugin 实例，如果找不到则返回 null
     */
    public static JavaPlugin getPluginFromObject(Object obj) {
        if (obj == null) {
            return null;
        }
        return getPluginFromClassLoader(obj.getClass().getClassLoader());
    }

    /**
     * 通过类获取其所属的插件实例
     * @param clazz 类对象
     * @return JavaPlugin 实例，如果找不到则返回 null
     */
    public static JavaPlugin getPluginFromClass(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        return getPluginFromClassLoader(clazz.getClassLoader());
    }
}
