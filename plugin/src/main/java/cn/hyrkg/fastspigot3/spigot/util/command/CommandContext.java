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

    public String getParameter(String key) {
        return getParameter(key, null);
    }

    public String getParameter(String key, String def) {
        return parameters.getOrDefault(key, def);
    }

    public boolean hasParameter(String key) {
        return parameters.containsKey(key);
    }

    public boolean isParameter(String key) {
        String parameter = getParameter(key);
        if (parameter == null) {
            return false;
        }
        parameter = parameter.toLowerCase();
        return parameter.equals("true") || parameter.equals("yes");
    }
}
