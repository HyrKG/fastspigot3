package cn.hyrkg.fastspigot3.spigot.command;

import cn.hyrkg.fastspigot3.context.processor.BeanProcessor;
import cn.hyrkg.fastspigot3.context.processor.ComponentProcessor;
import cn.hyrkg.fastspigot3.stereotype.Component;

@Component
@ComponentProcessor(CommandExecutor.class)
public class CommandProcessor implements BeanProcessor<CommandExecutor> {

    @Override
    public void process(CommandExecutor annotation, Object bean) {

    }

}
