package cn.hyrkg.fastspigot3.spigot.bootstrap;

import cn.hyrkg.fastspigot3.spigot.AppFastSpigot;
import org.bukkit.plugin.java.JavaPlugin;

public class FastPlugin extends JavaPlugin {


    @Override
    public void onEnable() {
        AppFastSpigot.getApplicationContext().registerBeanInstance(getClass(), this);
        AppFastSpigot.getApplicationContext().scanAndRegister(getClass().getPackage().getName(), getClass());
    }

    @Override
    public void onDisable() {
        AppFastSpigot.getApplicationContext().scanAndUnregister(getClass().getPackage().getName(), getClass());
        AppFastSpigot.getApplicationContext().unregisterBeanInstance(getClass());
    }
}
