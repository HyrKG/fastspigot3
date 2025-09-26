package cn.hyrkg.fastspigot3.spigot.command;

import cn.hyrkg.fastspigot3.context.annotation.processor.BeanAnnotationProcessor;
import cn.hyrkg.fastspigot3.context.annotation.processor.ProcessBeanForAnnotation;
import cn.hyrkg.fastspigot3.spigot.AppFastSpigot;
import cn.hyrkg.fastspigot3.spigot.util.PluginUtils;
import cn.hyrkg.fastspigot3.stereotype.Component;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.Locales;
import co.aikar.commands.annotation.CommandAlias;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

@Component
@ProcessBeanForAnnotation(CommandAlias.class)
public class AikarCommandProcessor implements BeanAnnotationProcessor<CommandAlias> {

    private HashMap<String, BukkitCommandManager> pluginCommandManagers = new HashMap<>();

    @Override
    public void postProcess(CommandAlias annotation, Object bean) {
        JavaPlugin plugin = PluginUtils.getPluginFromObject(bean);
        if (plugin == null) {
            throw new RuntimeException("Cannot find plugin for command class: " + bean.getClass().getName());
        }
        if (!(bean instanceof BaseCommand)) {
            throw new RuntimeException("Bean is not an instance of BaseCommand: " + bean.getClass().getName());
        }
        BukkitCommandManager bukkitCommandManager = getBukkitCommandManager(plugin);
        bukkitCommandManager.registerCommand((BaseCommand) bean);
    }

    public BukkitCommandManager getBukkitCommandManager(JavaPlugin plugin) {
        if (pluginCommandManagers.containsKey(plugin.getName())) {
            return pluginCommandManagers.get(plugin.getName());
        }
        BukkitCommandManager commandManager = new BukkitCommandManager(plugin);
        commandManager.enableUnstableAPI("help");
        commandManager.getLocales().setDefaultLocale(Locales.SIMPLIFIED_CHINESE);
        pluginCommandManagers.put(plugin.getName(), commandManager);
        return commandManager;
    }
}
