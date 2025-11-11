package cn.hyrkg.fastspigot3.spigot.logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DefaultLogger implements Logger {

    private final JavaPlugin plugin;
    private final Class clazz;
    private String pluginName;
    private String path;

    public DefaultLogger(JavaPlugin plugin, Class clazz) {

        this.plugin = plugin;
        this.clazz = clazz;

        setup();
    }

    private void setup() {
        String fullClassName = clazz.getName();
        String[] parts = fullClassName.split("\\.");

        StringBuilder pathBuilder = new StringBuilder();

        // 处理包名部分，每个包名只保留第一个字母
        for (int i = 0; i < parts.length - 1; i++) {
            pathBuilder.append(parts[i].charAt(0)).append(".");
        }

        // 添加类名
        pathBuilder.append(parts[parts.length - 1]);

        // 设置路径
        this.path = pathBuilder.toString();
        this.pluginName = plugin.getName();
    }

    @Override
    public void debug(String message, Object... objects) {
        Bukkit.getConsoleSender().sendMessage(format("debug", String.format(message, objects), "§7"));
    }

    @Override
    public void info(String message, Object... objects) {
        Bukkit.getConsoleSender().sendMessage(format("I", String.format(message, objects), "§a"));
    }

    @Override
    public void notice(String message, Object... objects) {
        Bukkit.getConsoleSender().sendMessage(format("N", String.format(message, objects), "§9"));
    }

    @Override
    public void warn(String message, Object... objects) {
        Bukkit.getConsoleSender().sendMessage(format("W", String.format(message, objects), "§6"));
    }

    @Override
    public void error(String message, Object... objects) {
        Bukkit.getConsoleSender().sendMessage(format("E", String.format(message, objects), "§4"));
    }


    private String format(String prefix, String message, String color) {
        String format = " §7[ %s ]  %s(%s)%s: %s§r";
        return String.format(format, pluginName, color, prefix, path, message);
    }

}
