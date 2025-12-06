package cn.hyrkg.fastspigot3.spigot.channel;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.UnsupportedEncodingException;

@RequiredArgsConstructor
@Getter
public class SimplePluginChannel implements PluginMessageListener {

    private final JavaPlugin plugin;
    private final String channel;

    private boolean listening = false;
    private MessageListener listener = null;
    private JsonParser jsonParser = new JsonParser();

    private void init() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, channel);
    }

    public void listen(MessageListener listener) {
        if (listening) {
            throw new IllegalStateException("listener is already listening");
        }
        this.listener = listener;
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, channel, this);
        listening = true;
    }

    public void listenRaw(PluginMessageListener listener) {
        if (listening) {
            throw new IllegalStateException("listener is already listening");
        }
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, channel, listener);
        listening = true;
    }


    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        if (listener == null) {
            return;
        }
        try {
            String str = new String(bytes, "UTF-8").substring(1);
            JsonObject jsonObject = jsonParser.parse(str).getAsJsonObject();
            listener.onMessage(player, jsonObject);

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendMessage(Player player, String rawMessage) {
        try {
            player.sendPluginMessage(plugin, channel, ("@" + rawMessage).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(Player player, JsonObject jsonObject) {
        sendMessage(player, jsonObject.toString());
    }

    public static SimplePluginChannel create(JavaPlugin plugin, String channel) {
        SimplePluginChannel pluginChannel = new SimplePluginChannel(plugin, channel);
        pluginChannel.init();
        return pluginChannel;
    }

}
