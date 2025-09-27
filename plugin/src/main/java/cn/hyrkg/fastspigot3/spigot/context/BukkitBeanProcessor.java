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
@ProcessBeanForAnnotation()
public class BukkitBeanProcessor implements BeanAnnotationProcessor {

    @Override
    public void postProcess(Object annotation, Object bean) {
        JavaPlugin plugin = PluginUtils.getPluginFromObject(bean);
        if (bean instanceof Listener) {
            Bukkit.getPluginManager().registerEvents((Listener) bean, plugin);
        }
    }


}
