package cn.hyrkg.fastspigot3.spigot.channel;

import com.google.gson.JsonObject;
import org.bukkit.entity.Player;

public interface MessageListener {
    void onMessage(Player player, JsonObject jsonObject);
}
