package cn.hyrkg.fastspigot3.spigot.util.command;

import java.util.HashMap;
import java.util.Map;

/**
 * 命令行解析工具类
 * <p>
 * 用于将字符串形式的命令行解析为 {@link CommandContext} 对象。
 * 支持解析命令名称和带前缀 '-' 的选项参数。
 *
 * <h3>示例</h3>
 * 假设输入字符串为：
 * <pre>
 * spawn zombie -elite -hp 500
 * </pre>
 * 解析结果：
 * <ul>
 *     <li><strong>命令名 (Command):</strong> <code>"spawn zombie"</code> <br>
 *         (遇到第一个以 '-' 开头的参数前的所有部分组成命令名)
 *     </li>
 *     <li><strong>参数 (Parameters):</strong>
 *         <ul>
 *             <li><code>elite</code> = <code>"true"</code> (无值参数默认为 "true")</li>
 *             <li><code>hp</code> = <code>"500"</code> (紧跟在键后的值为参数值)</li>
 *         </ul>
 *     </li>
 * </ul>
 */
public class CommandCliParser {


    /**
     * 解析命令行字符串
     * <p>
     * 解析规则：
     * 1. 命令行被空白字符分割。
     * 2. 第一个以 '-' 开头的部分之前的即为命令名称（可包含空格）。
     * 3. 以 '-' 开头的部分被视为选项键（Key），随后的部分若不以 '-' 开头则视为该选项的值（Value）。
     * 4. 若选项后紧跟另一个选项或已结束，则该选项的值默认为 "true"。
     *
     * @param commandline 命令行字符串
     * @return 解析后的命令上下文对象
     */
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
                        currentOption = null; // 重置当前选项，表示该参数值已被消费
                    }
                }
            }
        }

        // 将构建的指令名转换为字符串
        String commandName = commandNameBuilder.toString();
        return new CommandContext(commandName, parameters);
    }
}
