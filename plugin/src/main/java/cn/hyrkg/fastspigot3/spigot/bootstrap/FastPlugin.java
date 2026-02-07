package cn.hyrkg.fastspigot3.spigot.bootstrap;

import cn.hyrkg.fastspigot3.context.ApplicationContext;
import cn.hyrkg.fastspigot3.beans.factory.config.BeanDefinition;
import cn.hyrkg.fastspigot3.spigot.AppFastSpigot;
import cn.hyrkg.fastspigot3.spigot.logger.Logger;
import cn.hyrkg.fastspigot3.spigot.logger.LoggerProcessor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class FastPlugin extends JavaPlugin {
    private Logger logger;

    @Getter
    private ApplicationContext applicationContext;

    @Override
    public void onLoad() {
        applicationContext = new ApplicationContext();
    }

    @Override
    @SneakyThrows
    public void onEnable() {
        long libStartTime = System.currentTimeMillis(); //记录核心库加载开始时间
        applicationContext.scanAndRegister(AppFastSpigot.class.getPackage().getName());
        logger = applicationContext.getBeanFactory().getBean(LoggerProcessor.class).getLogger(this);
        applicationContext.setLogConsumer(it -> logger.debug(it));

        long pluginStartTime = System.currentTimeMillis(); //记录插件组件加载开始时间
        logger.notice("正在加载" + getDescription().getName() + " v" + getDescription().getVersion() + "...");

        // 修复：先注册定义，确保主类注入时能找到依赖
        List<BeanDefinition> definitions = applicationContext.scanAndRegisterDefinitions(getClass().getPackage().getName(), getClass());
        // 注册插件实例并执行注入
        applicationContext.getBeanFactory().registerBean(this);
        // 加载剩余组件
        applicationContext.loadBeans(definitions);

        long loadCompletedTime = System.currentTimeMillis(); //记录加载结束时间
        logger.notice("加载完成,库耗时 " + (pluginStartTime - libStartTime) + " ms,组件耗时 " + (loadCompletedTime - pluginStartTime) + " ms,共 " + (loadCompletedTime - libStartTime) + " ms");
    }

    @Override
    public void onDisable() {
        logger.notice("正在关闭" + getDescription().getName() + "v" + getDescription().getVersion() + "...");

        long startTime = System.currentTimeMillis(); //记录卸载开始时间
        applicationContext.scanAndUnregister(getClass().getPackage().getName(), getClass());
        applicationContext.getBeanFactory().unregisterBean(this);
        logger.notice(getDescription().getName() + " 关闭完成,耗时 " + (System.currentTimeMillis() - startTime) + " ms");
        applicationContext.scanAndUnregister(AppFastSpigot.class.getPackage().getName());
    }

    public Logger getFastLogger() {
        return logger;
    }
}
