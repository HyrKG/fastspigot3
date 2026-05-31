package cn.hyrkg.fastspigot3.spigot.forgeui;

import cn.hyrkg.fastspigot3.spigot.easygui.EasyGui;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.UUID;

/**
 * 融合EasyGui和ForgeGui的基类，同时支持Bukkit原生GUI和Forge GUI协议。
 * 展示时先打开Bukkit物品栏GUI，再通过Forge协议发送数据到客户端。
 */
public class BaseForgeEasyGui extends EasyGui implements IForgeGui {

    protected final ForgeGuiHandler guiHandler; // Forge GUI管理器
    protected final String guiShortName; // GUI短名称
    protected UUID uuid = UUID.randomUUID(); // GUI唯一标识
    protected SharedProperty sharedProperty = new SharedProperty(); // 共享属性容器
    protected boolean isDisplayed = false; // 是否已展示到客户端


    public BaseForgeEasyGui(Player p, String guiShortName, ForgeGuiHandler guiHandler) {
        super(p);
        this.guiHandler = guiHandler;
        this.guiShortName = guiShortName;
    }


    @Override
    public void onClickItem(InventoryClickEvent event) {

    }

    @Override
    public void onClose() {
        guiHandler.close(this);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {

    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onMessage(JsonObject jsonObject) {

    }

    /** 展示GUI：先打开Bukkit物品栏，再发送Forge协议数据 */
    @Override
    public void display() {
        super.display();
        this.guiHandler.display(this, false);
        markDisplayed();
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String getGuiShortName() {
        return guiShortName;
    }

    @Override
    public SharedProperty getSharedProperty() {
        return sharedProperty;
    }

    @Override
    public void markDisplayed() {
        this.isDisplayed = true;
    }

    /** 创建消息构建器 */
    public SimpleMsg msg() {
        return SimpleMsg.create(this, this.guiHandler);
    }

    /** 强制同步属性变更到客户端，仅在已展示且有变更时生效 */
    public void forceSynProperty() {
        if (!isDisplayed) return;
        if (this.getSharedProperty().detectChange()) {
            guiHandler.updateChanges(this);
        }
    }


}
