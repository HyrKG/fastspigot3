package cn.hyrkg.fastspigot3.spigot.command;

import cn.hyrkg.fastspigot3.context.annotation.OnReady;
import cn.hyrkg.fastspigot3.context.annotation.processor.BeanAnnotationProcessor;
import cn.hyrkg.fastspigot3.context.annotation.processor.ProcessBeanForAnnotation;
import cn.hyrkg.fastspigot3.stereotype.Component;

@Component
@ProcessBeanForAnnotation(CommandHandler.class)
public class CommandHandlerProcessor implements BeanAnnotationProcessor<CommandHandler> {

    @Override
    public void postProcess(CommandHandler annotation, Object bean) {
        System.out.println("Registering command handler: " + bean.getClass().getName());
    }
}
