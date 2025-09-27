package cn.hyrkg.fastspigot3.spigot.logger;

import cn.hyrkg.fastspigot3.context.annotation.Autowired;
import cn.hyrkg.fastspigot3.context.annotation.processor.FieldAnnotationProcessor;
import cn.hyrkg.fastspigot3.context.annotation.processor.ProcessFieldForAnnotation;
import cn.hyrkg.fastspigot3.context.annotation.processor.ProcessorAction;
import cn.hyrkg.fastspigot3.spigot.util.PluginUtils;
import cn.hyrkg.fastspigot3.stereotype.Component;
import cn.hyrkg.fastspigot3.util.ReflectionUtils;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

@Component
@ProcessFieldForAnnotation(Autowired.class)
public class LoggerProcessor implements FieldAnnotationProcessor<Autowired> {

    @Override
    @SneakyThrows
    public ProcessorAction preProcess(Field field, Autowired annotation, Object bean) {
        if (field.getType().equals(Logger.class)) {
            Logger logger = getLogger(field, bean);
            ReflectionUtils.setFieldValue(field, bean, logger);
            return ProcessorAction.STOP_PROCESSING;
        }
        return ProcessorAction.CONTINUE;
    }

    public Logger getLogger(Field field, Object bean) {
        JavaPlugin pluginFromObject = PluginUtils.getPluginFromObject(bean);
        return new DefaultLogger(pluginFromObject, field);
    }
}
