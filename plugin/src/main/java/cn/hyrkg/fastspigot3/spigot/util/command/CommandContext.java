package cn.hyrkg.fastspigot3.spigot.util.command;

import lombok.Getter;

import java.util.Map;

@Getter
public class CommandContext {
    private final String command;
    private final Map<String, String> parameters;

    public CommandContext(String command, Map<String, String> parameters) {
        this.command = command;
        this.parameters = parameters;
    }

    public String getParameter(String... keys) {
        for (String key : keys) {
            if (parameters.containsKey(key)) {
                return parameters.get(key);
            }
        }
        return null;
    }

    public String getParameterOrDef(String key, String def) {
        return parameters.getOrDefault(key, def);
    }

    public String getParameterOrDef(String[] keys, String def) {
        String parameter = getParameter(keys);
        if (parameter == null) {
            return def;
        }
        return parameter;
    }

    public boolean hasParameter(String... keys) {
        for (String s : keys) {
            if (parameters.containsKey(s)) {
                return true;
            }
        }
        return false;
    }

    public boolean isParameter(String... keys) {
        String parameter = getParameter(keys);
        if (parameter == null) {
            return false;
        }
        parameter = parameter.toLowerCase();
        return parameter.equals("true") || parameter.equals("yes");
    }
}
