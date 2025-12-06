package cn.hyrkg.fastspigot3.spigot.util.command;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class CommandYamlUtils {
    public static CommandContext parse(ConfigurationSection parametersSection) {
        return parse(null, parametersSection);
    }

    public static CommandContext parse(String command, ConfigurationSection parametersSection) {
        Map<String, String> parameters = new HashMap<>();
        for (String key : parametersSection.getKeys(false)) {
            parameters.put(key, parametersSection.getString(key));
        }
        return new CommandContext(command, parameters);
    }
}
