package cn.hyrkg.fastspigot3.spigot.forgeui;

import com.google.gson.JsonObject;
import org.bukkit.entity.Player;

/**
 * 数据包分发器接口，允许在发送前拦截并自定义数据包内容。
 */
public interface IPacketDistributor {
    /**
     * 处理数据包，返回修改后的JSON
     * @param packetType 包类型
     * @param player 目标玩家
     * @param sourceJson 原始数据包
     * @return 处理后的数据包
     */
    JsonObject handle(PacketType packetType, Player player, JsonObject sourceJson);
}
