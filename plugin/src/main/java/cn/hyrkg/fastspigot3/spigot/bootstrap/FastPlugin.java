package cn.hyrkg.fastspigot3.spigot.bootstrap;

import cn.hyrkg.fastspigot3.context.ApplicationContext;
import cn.hyrkg.fastspigot3.spigot.AppFastSpigot;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class FastPlugin extends JavaPlugin {

    @Getter
    private ApplicationContext applicationContext;

    @Override
    public void onLoad() {
        applicationContext = new ApplicationContext();
    }

    @Override
    public void onEnable() {
        applicationContext.scanAndRegister(AppFastSpigot.class.getPackage().getName());
        applicationContext.registerBeanInstance(getClass(), this);
        applicationContext.scanAndRegister(getClass().getPackage().getName(), getClass());
    }

    @Override
    public void onDisable() {
        applicationContext.scanAndUnregister(getClass().getPackage().getName(), getClass());
        applicationContext.unregisterBeanInstance(getClass());
        applicationContext.scanAndUnregister(AppFastSpigot.class.getPackage().getName());
    }
}
