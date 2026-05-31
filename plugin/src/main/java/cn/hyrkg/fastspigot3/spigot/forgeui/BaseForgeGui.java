package cn.hyrkg.fastspigot3.spigot.forgeui;

import com.google.gson.JsonObject;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * 单玩家Forge GUI基类，绑定唯一观察者。消息和关闭事件统一委托给无参方法处理。
 */
public class BaseForgeGui extends BaseForgeShareGui {
    @Getter
    private final Player viewer; // 绑定的唯一观察者

    public BaseForgeGui(Player viewer, String guiShortName, ForgeGuiHandler guiHandler) {
        super(guiShortName, guiHandler);
        this.viewer = viewer;
        this.addViewer(viewer);
    }

    /** 消息事件委托给无参版本，单人GUI无需区分发送者 */
    @Override
    public void onMessage(Player viewer, JsonObject jsonObject) {
        onMessage(jsonObject);
    }

    /** 关闭事件委托给无参版本 */
    @Override
    public void onClose(Player viewer) {
        onClose();
    }

}
