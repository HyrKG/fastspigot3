package cn.hyrkg.fastspigot3.spigot.bootstrap;

import cn.hyrkg.fastspigot3.spigot.AppFastSpigot;
import org.bukkit.plugin.java.JavaPlugin;

public class FastPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        AppFastSpigot.getApplicationContext().scanAndRegister(getClass().getPackage().getName(), getClass());
    }
}
