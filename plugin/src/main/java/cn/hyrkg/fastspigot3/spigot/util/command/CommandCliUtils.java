package cn.hyrkg.fastspigot3.spigot.util.command;

import java.util.HashMap;
import java.util.Map;

public class CommandCliUtils {


    public static CommandContext parse(String commandline) {
        if (commandline == null || commandline.trim().isEmpty()) {
            return new CommandContext("", new HashMap<>()); // 返回一个空的 Command 对象
        }

        // 使用空格分割整个命令行
        String[] parts = commandline.trim().split("\\s+");
        StringBuilder commandNameBuilder = new StringBuilder();
        Map<String, String> parameters = new HashMap<>();

        String currentOption = null;
        boolean parsingCommandName = true; // 用于标记当前是否在解析指令名

        // 遍历命令的各个部分
        for (String part : parts) {
            if (parsingCommandName) {
                if (part.startsWith("-")) {
                    // 遇到第一个选项时，停止解析指令名
                    parsingCommandName = false;
                    currentOption = part;
                    parameters.put(currentOption.substring(1), "true"); // 默认值为 true
                } else {
                    // 继续构建指令名
                    if (commandNameBuilder.length() > 0) {
                        commandNameBuilder.append(" ");
                    }
                    commandNameBuilder.append(part);
                }
            } else {
                // 正在解析选项和参数
                if (part.startsWith("-")) {
                    currentOption = part;
                    parameters.put(currentOption.substring(1), "true"); // 默认值为 true
                } else {
                    if (currentOption != null) {
                        parameters.put(currentOption.substring(1), part);
                        currentOption = null; // 重置当前选项
                    }
                }
            }
        }

        // 将构建的指令名转换为字符串
        String commandName = commandNameBuilder.toString();
        return new CommandContext(commandName, parameters);
    }
}
