package cn.hyrkg.fastspigot3.spigot.util.command;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

/**
 * YAML 配置解析工具类
 * <p>
 * 用于将 Bukkit 的 {@link ConfigurationSection} 解析为 {@link CommandContext} 对象。
 *
 * <h3>示例 YAML 配置</h3>
 * <pre>
 * monster-setting:
 *   type: zombie
 *   elite: true
 *   hp: 500
 * </pre>
 * 假设上述配置对应的 ConfigurationSection 传入解析器：
 * <ul>
 *     <li><strong>命令名 (Command):</strong> 由调用者指定 (如 "spawn")</li>
 *     <li><strong>参数 (Parameters):</strong>
 *         <ul>
 *             <li><code>type</code> = <code>"zombie"</code></li>
 *             <li><code>elite</code> = <code>"true"</code></li>
 *             <li><code>hp</code> = <code>"500"</code></li>
 *         </ul>
 *     </li>
 * </ul>
 */
public class CommandYamlParser {
    /**
     * 从配置片段解析命令上下文（无特定命令名）
     *
     * @param parametersSection 包含参数的配置片段
     * @return 解析后的命令上下文对象，命令名为 null
     */
    public static CommandContext parse(ConfigurationSection parametersSection) {
        return parse(null, parametersSection);
    }

    /**
     * 从配置片段解析命令上下文
     *
     * @param command           指定的命令名称
     * @param parametersSection 包含参数的配置片段
     * @return 解析后的命令上下文对象
     */
    public static CommandContext parse(String command, ConfigurationSection parametersSection) {
        Map<String, String> parameters = new HashMap<>();
        // 遍历配置片段中的所有键，将其作为参数
        for (String key : parametersSection.getKeys(false)) {
            parameters.put(key, parametersSection.getString(key));
        }
        return new CommandContext(command, parameters);
    }
}
