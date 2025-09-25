package cn.hyrkg.fastspigot3.bootstrap;

import cn.hyrkg.fastspigot3.beans.BeanManager;
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
        // 扫描并注册当前工程包下的可注入类
        beanManager.scanAndRegister("cn.hyrkg.fastspigot3");
    }

}
