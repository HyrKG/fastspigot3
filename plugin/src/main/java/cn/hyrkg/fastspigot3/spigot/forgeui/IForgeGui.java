package cn.hyrkg.fastspigot3.spigot.forgeui;

import com.google.gson.JsonObject;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Forge GUI接口，定义GUI的基本契约，包括生命周期、属性管理和消息通信。
 */
public interface IForgeGui {

    /** GUI短名称，用于客户端识别对应的界面类型 */
    String getGuiShortName();

    /** GUI唯一标识 */
    UUID getUuid();

    /** 定时更新回调 */
    void onUpdate();

    /** 关闭回调 */
    void onClose();

    /** 带观察者的关闭回调，默认委托给无参版本 */
    default void onClose(Player viewer) {
        onClose();
    }

    /** 收到客户端消息回调 */
    void onMessage(JsonObject jsonObject);

    /** 带观察者的消息回调，默认委托给无参版本 */
    default void onMessage(Player viewer, JsonObject jsonObject) {
        onMessage(jsonObject);
    }

    /** 获取共享属性容器 */
    SharedProperty getSharedProperty();

    /** 标记GUI已展示到客户端 */
    void markDisplayed();

    /** 获取主观察者，单人GUI实现此方法 */
    default Player getViewer() {
        return null;
    }

    /** 获取所有观察者集合 */
    default Set<Player> getViewers() {
        HashSet set = new HashSet();
        set.add(getViewer());
        return set;
    }

    /** 获取数据包分发器，用于自定义包处理逻辑 */
    default IPacketDistributor getDistributor() {
        return null;
    }
}
