package cn.hyrkg.fastspigot3.spigot.command;

import cn.hyrkg.fastspigot3.context.annotation.Autowired;
import cn.hyrkg.fastspigot3.context.annotation.processor.BeanAnnotationProcessor;
import cn.hyrkg.fastspigot3.context.annotation.processor.ProcessBeanForAnnotation;
import cn.hyrkg.fastspigot3.spigot.bootstrap.FastPlugin;
import cn.hyrkg.fastspigot3.spigot.logger.LoggerProcessor;
import cn.hyrkg.fastspigot3.spigot.util.PluginUtils;
import cn.hyrkg.fastspigot3.stereotype.Component;
import cn.hyrkg.fastspigot3.util.ReflectionUtils;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.Locales;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.CommandAlias;
import com.google.common.collect.Multimap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@ProcessBeanForAnnotation(CommandAlias.class)
public class AikarCommandProcessor implements BeanAnnotationProcessor<CommandAlias>, Listener {

    @Autowired
    private LoggerProcessor loggerProcessor;
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
        if (bean instanceof AikarCommandAware) {
            ((AikarCommandAware) bean).configureCommandManager(bukkitCommandManager);
        }
        BaseCommand baseCommand = (BaseCommand) bean;
        bukkitCommandManager.registerCommand(baseCommand);

        // 输出调试信息
        Set<String> perfCommandSet = new HashSet<>();
        for (RegisteredCommand registeredCommand : baseCommand.getRegisteredCommands()) {
            String prefSubCommand = registeredCommand.getPrefSubCommand();
            if (prefSubCommand == null || prefSubCommand.isEmpty()) {
                continue;
            }
            perfCommandSet.add(prefSubCommand);
        }
        loggerProcessor.getLogger(bean).debug("已注册Aikar命令 [" + annotation.value() + "] 共 " + perfCommandSet.size() + " 个子命令");
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

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        String pluginName = event.getPlugin().getName();
        if (pluginCommandManagers.containsKey(pluginName)) {
            BukkitCommandManager remove = pluginCommandManagers.remove(pluginName);
            remove.unregisterCommands();
        }
    }

}
