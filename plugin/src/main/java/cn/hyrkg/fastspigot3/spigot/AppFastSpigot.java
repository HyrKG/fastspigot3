package cn.hyrkg.fastspigot3.spigot;

import cn.hyrkg.fastspigot3.context.ApplicationContext;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.Plugin;

@Plugin(name = "fastspigot3", version = "1.0.0")
public class AppFastSpigot extends JavaPlugin {
    @Getter
    private static AppFastSpigot instance;

    @Override
    public void onEnable() {
        instance = this;
    }


    @Override
    public void onDisable() {
    }
}
