package cn.hyrkg.fastspigot3.spigot.util.command;

import lombok.Getter;

import java.util.Map;

/**
 * 命令上下文对象
 * <p>
 * 用于封装解析后的命令及其参数，提供便捷的方法获取参数值。
 */
@Getter
public class CommandContext {
    /**
     * 命令名称（主要部分）
     */
    private final String command;
    /**
     * 命令参数映射表
     * <p>
     * 键为参数名（不带前缀 '-'），值为参数值
     */
    private final Map<String, String> parameters;

    /**
     * 构造一个新的命令上下文
     *
     * @param command    命令名称
     * @param parameters 参数映射表
     */
    public CommandContext(String command, Map<String, String> parameters) {
        this.command = command;
        this.parameters = parameters;
    }

    /**
     * 获取指定键的参数值
     * <p>
     * 支持传入多个键名，将返回第一个存在的参数值。
     *
     * @param keys 参数键名列表
     * @return 参数值，如果未找到则返回 null
     */
    public String getParameter(String... keys) {
        for (String key : keys) {
            if (parameters.containsKey(key)) {
                return parameters.get(key);
            }
        }
        return null;
    }

    /**
     * 获取指定键的参数值，如果不存在则返回默认值
     *
     * @param key 参数键名
     * @param def 默认值
     * @return 参数值或默认值
     */
    public String getParameterOrDef(String key, String def) {
        return parameters.getOrDefault(key, def);
    }

    /**
     * 获取指定键列表中的参数值，如果都不存在则返回默认值
     *
     * @param keys 参数键名列表
     * @param def  默认值
     * @return 第一个匹配的参数值或默认值
     */
    public String getParameterOrDef(String[] keys, String def) {
        String parameter = getParameter(keys);
        if (parameter == null) {
            return def;
        }
        return parameter;
    }

    /**
     * 检查是否包含指定的任意一个参数
     *
     * @param keys 参数键名列表
     * @return 如果包含任意一个参数则返回 true，否则返回 false
     */
    public boolean hasParameter(String... keys) {
        for (String s : keys) {
            if (parameters.containsKey(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查参数值是否为布尔真值
     * <p>
     * 通常用于检查开关类型的参数。当参数值为 "true" (忽略大小写) 或 "yes" 时返回 true。
     * 如果参数不存在，返回 false。
     *
     * @param keys 参数键名列表
     * @return 参数是否被视为真值
     */
    public boolean isParameter(String... keys) {
        String parameter = getParameter(keys);
        if (parameter == null) {
            return false;
        }
        parameter = parameter.toLowerCase();
        return parameter.equals("true") || parameter.equals("yes");
    }
}
