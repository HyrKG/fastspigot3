package cn.hyrkg.fastspigot3.spigot.util.command;

import java.util.HashMap;
import java.util.Map;

/**
 * 分号分隔键值对解析工具类
 * <p>
 * 用于将分号分隔的键值对字符串解析为 {@link CommandContext} 对象。
 * 格式为 {@code key=value;key2=value2}。
 *
 * <h3>示例</h3>
 * 假设输入字符串为：
 * <pre>
 * type=zombie;elite=true;hp=500
 * </pre>
 * 解析结果：
 * <ul>
 *     <li><strong>命令名 (Command):</strong> 由调用者指定 (如 null 或 "spawn")</li>
 *     <li><strong>参数 (Parameters):</strong>
 *         <ul>
 *             <li><code>type</code> = <code>"zombie"</code></li>
 *             <li><code>elite</code> = <code>"true"</code></li>
 *             <li><code>hp</code> = <code>"500"</code></li>
 *         </ul>
 *     </li>
 * </ul>
 */
public class CommandSemicolonParser {

    /**
     * 解析分号分隔的键值对字符串（无特定命令名）
     *
     * @param source 包含键值对的源字符串
     * @return 解析后的命令上下文对象，命令名为 null
     */
    public static CommandContext parse(String source) {
        return parse(null, source);
    }

    /**
     * 解析分号分隔的键值对字符串
     *
     * @param command 指定的命令名称
     * @param source  包含键值对的源字符串，格式如 {@code k=v;k2=v2}
     * @return 解析后的命令上下文对象
     */
    public static CommandContext parse(String command, String source) {
        Map<String, String> parameters = new HashMap<>();

        if (source != null && !source.isEmpty()) {
            // 使用分号分割每个键值对
            String[] pairs = source.split(";");
            for (String pair : pairs) {
                if (pair.trim().isEmpty()) {
                    continue;
                }
                // 使用第一个等号分割键和值
                int equalIndex = pair.indexOf('=');
                if (equalIndex > 0) {
                    String key = pair.substring(0, equalIndex).trim();
                    String value = pair.substring(equalIndex + 1).trim();
                    if (!key.isEmpty()) {
                        parameters.put(key, value);
                    }
                } else {
                    // 如果没有等号，可以将整个部分作为 key，value 默认为 "true" (类似命令行选项)
                    // 或者根据严格的 k=v 格式忽略。
                    // 这里采用宽松策略：如果有非空字符串但没有等号，视为 key=true
                    String key = pair.trim();
                    if (!key.isEmpty()) {
                        parameters.put(key, "true");
                    }
                }
            }
        }

        return new CommandContext(command, parameters);
    }
}
