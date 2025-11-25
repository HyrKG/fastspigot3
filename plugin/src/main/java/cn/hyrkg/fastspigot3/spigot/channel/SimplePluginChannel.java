package cn.hyrkg.fastspigot3.spigot.channel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.UnsupportedEncodingException;

@RequiredArgsConstructor
@Getter
public class SimplePluginChannel {

    private final JavaPlugin plugin;
    private final String channel;


    private void init() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, channel);
    }

    public void registerListener(PluginMessageListener listener) {
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, channel, listener);
    }


    public void sendMessage(Player player, String rawMessage) {
        try {
            player.sendPluginMessage(plugin, channel, ("@" + rawMessage).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static SimplePluginChannel create(JavaPlugin plugin, String channel) {
        return new SimplePluginChannel(plugin, channel);
    }
}
