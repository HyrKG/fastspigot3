package cn.hyrkg.fastspigot3.spigot.command;

import cn.hyrkg.fastspigot3.context.annotation.processor.BeanAnnotationProcessor;
import cn.hyrkg.fastspigot3.context.annotation.processor.ProcessBeanForAnnotation;
import cn.hyrkg.fastspigot3.stereotype.Component;

@Component
@ProcessBeanForAnnotation(CommandExecutor.class)
public class CommandProcessorAnnotation implements BeanAnnotationProcessor<CommandExecutor> {

    @Override
    public void process(CommandExecutor annotation, Object bean) {
        //TODO 解析bean并且注册命令
    }

}
