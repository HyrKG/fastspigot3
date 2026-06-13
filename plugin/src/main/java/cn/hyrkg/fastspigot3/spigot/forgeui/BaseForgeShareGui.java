package cn.hyrkg.fastspigot3.spigot.forgeui;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 共享型Forge GUI基类，支持多个玩家同时查看同一个界面。
 * 观察者集合由外部管理，属性变更会同步到所有观察者。
 */
public abstract class BaseForgeShareGui implements IForgeGui {
    @Getter
    protected UUID uuid = UUID.randomUUID(); // GUI唯一标识
    @Getter
    protected final String guiShortName; // GUI短名称
    @Getter
    protected final ForgeGuiHandler guiHandler; // GUI管理器

    @Setter
    protected HashSet<Player> viewerSets = new HashSet<>(); // 当前观察者集合

    protected boolean isDisplayed = false; // 是否已展示到客户端

    @Getter
    protected SharedProperty sharedProperty = new SharedProperty(); // 共享属性容器

    public BaseForgeShareGui(String guiShortName, ForgeGuiHandler guiHandler) {
        this.guiShortName = guiShortName;
        this.guiHandler = guiHandler;

    }

    /**
     * 添加观察者
     */
    public void addViewer(Player player) {
        viewerSets.add(player);
    }

    /**
     * 移除观察者
     */
    public void removeViewer(Player player) {
        viewerSets.remove(player);
    }

    /**
     * 批量替换观察者集合，自动关闭已移除玩家的界面，可选自动展示给新增玩家
     *
     * @param newViewers   新的观察者集合
     * @param displayToNew 是否自动展示给新增的观察者
     */
    public void setViewers(HashSet<Player> newViewers, boolean displayToNew) {
        // 计算需要关闭的旧观察者
        HashSet<Player> closedViewer = new HashSet<>(viewerSets);
        closedViewer.removeAll(newViewers);

        for (Player oldViewer : closedViewer) {
            close(oldViewer);
        }

        if (displayToNew) {
            // 计算新增的观察者并展示
            HashSet<Player> diffViewers = new HashSet<>(newViewers);
            diffViewers.removeAll(viewerSets);

            viewerSets = newViewers;
            for (Player diffViewer : diffViewers) {
                display(diffViewer);
            }
        } else {
            viewerSets = newViewers;
        }
    }


    @Override
    public void onUpdate() {

    }

    /**
     * 向所有观察者展示GUI
     */
    public void display() {
        guiHandler.display(this);
    }

    /**
     * 向指定玩家展示GUI
     */
    public void display(Player player) {
        viewerSets.add(player);
        guiHandler.display(player, this);
    }

    /**
     * 关闭所有观察者的GUI
     */
    public void close() {
        guiHandler.close(this);
    }

    /**
     * 关闭指定玩家的GUI
     */
    public void close(Player player) {
        guiHandler.close(player, this);
    }

    @Override
    public void onClose(Player viewer) {
        viewerSets.remove(viewer);
    }

    @Override
    public void onClose() {

    }

    @Override
    public void onMessage(Player viewer, JsonObject jsonObject) {

    }

    @Override
    public void onMessage(JsonObject jsonObject) {

    }


    @Override
    public void markDisplayed() {
        isDisplayed = true;
    }


    @Override
    public Set<Player> getViewers() {
        return viewerSets;
    }

    /**
     * 强制同步属性变更到客户端，仅在已展示且有变更时生效
     */
    public void forceSynProperty() {
        if (!isDisplayed) return;
        if (this.getSharedProperty().detectChange()) {
            guiHandler.updateChanges(this);
        }
    }


    /**
     * 创建消息构建器
     */
    public SimpleMsg msg() {
        return SimpleMsg.create(this, guiHandler);
    }


}
