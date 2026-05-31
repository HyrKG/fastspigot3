package cn.hyrkg.fastspigot3.spigot.forgeui;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;

/**
 * 简单消息构建器，用于向Forge GUI客户端发送自定义JSON消息。采用链式调用模式。
 */
public class SimpleMsg {

    private final ForgeGuiHandler forgeGuiHandler; // GUI管理器引用
    private final IForgeGui forgeGui; // 目标GUI实例
    private final JsonObject jsonObj; // 消息内容

    private SimpleMsg(IForgeGui gui, ForgeGuiHandler guiHandler) {
        this.forgeGui = gui;
        this.forgeGuiHandler = guiHandler;
        jsonObj = new JsonObject();
    }

    /** 添加键值对，支持Number、Character、String、Boolean、JsonElement类型 */
    public SimpleMsg add(String key, Object value) {
        if (value instanceof Number)
            jsonObj.addProperty(key, (Number) value);
        else if (value instanceof Character)
            jsonObj.addProperty(key, (Character) value);
        else if (value instanceof String)
            jsonObj.addProperty(key, (String) value);
        else if (value instanceof Boolean)
            jsonObj.addProperty(key, (Boolean) value);
        else if (value instanceof JsonElement)
            jsonObj.add(key, (JsonElement) value);
        return this;
    }

    /** 发送给GUI的所有观察者 */
    public void sent() {
        forgeGuiHandler.sendMessage(this, forgeGui);
    }

    /** 发送给指定玩家 */
    public void sent(Player player) {
        forgeGuiHandler.sendMessage(this, forgeGui, player);
    }

    public JsonObject getJsonObj() {
        return jsonObj;
    }

    /** 创建SimpleMsg实例 */
    public static SimpleMsg create(IForgeGui gui, ForgeGuiHandler guiHandler) {
        return new SimpleMsg(gui, guiHandler);
    }
}
