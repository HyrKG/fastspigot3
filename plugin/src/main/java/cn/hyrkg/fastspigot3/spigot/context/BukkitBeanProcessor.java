package cn.hyrkg.fastspigot3.spigot.context;

import cn.hyrkg.fastspigot3.context.annotation.processor.BeanAnnotationProcessor;
import cn.hyrkg.fastspigot3.context.annotation.processor.ProcessBeanForAnnotation;
import cn.hyrkg.fastspigot3.spigot.util.PluginUtils;
import cn.hyrkg.fastspigot3.stereotype.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Bukkit组件处理器：用于处理被@Component注解标记的类
 */
@Component
@ProcessBeanForAnnotation(Component.class)
public class BukkitBeanProcessor implements BeanAnnotationProcessor<Component> {

    @Override
    public void postProcess(Component annotation, Object bean) {
        JavaPlugin plugin = PluginUtils.getPluginFromObject(bean);
        if (plugin != null) {
            if (bean instanceof Listener) {
                Bukkit.getPluginManager().registerEvents((Listener) bean, plugin);
            }
        }
    }
}
