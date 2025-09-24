package cn.hyrkg.fastspigot3.plugin;

import cn.hyrkg.fastspigot3.core.BeanManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.Plugin;

@Plugin(name = "fastspigot3", version = "1.0.0")
@Getter
public class AppFastSpigot extends JavaPlugin {
    @Getter
    private static AppFastSpigot instance;
    private BeanManager beanManager;

    @Override
    public void onEnable() {
        instance = this;

        beanManager = new BeanManager();
    }

}
