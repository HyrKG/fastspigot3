package cn.hyrkg.fastspigot3.spigot.command;

import co.aikar.commands.BukkitCommandManager;

/**
 * 实现此接口的命令类可以在被注册前配置CommandManager
 */
public interface AikarCommandAware {
    /**
     * 在命令被注册到CommandManager之前调用
     * @param commandManager 命令管理器
     */
    void configureCommandManager(BukkitCommandManager commandManager);
}
