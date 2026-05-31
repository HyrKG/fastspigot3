package cn.hyrkg.fastspigot3.spigot.forgeui;

import cn.hyrkg.fastspigot3.spigot.easygui.EasyGuiHandler;
import cn.hyrkg.fastspigot3.spigot.channel.SimplePluginChannel;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Forge GUI核心管理器，负责GUI的注册、展示、更新、关闭以及与客户端的PluginChannel通信。
 * 同时监听玩家退出事件以自动清理GUI状态。
 */
public class ForgeGuiHandler implements Listener {
    public static final String CHANNEL_FORGE_GUI = "ffg"; // Forge GUI 通信频道标识

    private HashMap<Player, IForgeGui> viewingForgeGui = new HashMap<>(); // 玩家 -> 正在查看的GUI
    private Set<IForgeGui> guiSet = ConcurrentHashMap.newKeySet(); // 所有活跃GUI集合（线程安全）

    public SimplePluginChannel forgeGuiChannel; // 插件通信频道

    private BukkitRunnable updateTimer = null; // 定时更新任务

    /** 初始化通信频道和定时更新任务 */
    public void init(JavaPlugin plugin, String channelIndex) {
        forgeGuiChannel = SimplePluginChannel.create(plugin, channelIndex + "_" + CHANNEL_FORGE_GUI);
        forgeGuiChannel.listen(this::onPluginMessageReceivedJson);

        if (updateTimer != null) {
            updateTimer.cancel();
        }

        updateTimer = new BukkitRunnable() {
            @Override
            public void run() {
                update();
            }
        };
        updateTimer.runTaskTimerAsynchronously(plugin, 10l, 10l); // 每10tick异步执行
    }

    /** 向GUI的所有观察者发送消息 */
    public void sendMessage(SimpleMsg msg, IForgeGui baseForgeGui) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uuid", baseForgeGui.getUuid().toString());
        jsonObject.add("msg", msg.getJsonObj());


        for (Player viewer : baseForgeGui.getViewers()) {
            String packetStr = jsonObject.toString();
            if (baseForgeGui.getDistributor() != null) {
                JsonObject packet = baseForgeGui.getDistributor().handle(PacketType.message, viewer, jsonObject);
                packetStr = packet.toString();
            }
            forgeGuiChannel.sendMessage(viewer, packetStr);

        }
    }

    /** 向指定玩家发送消息 */
    public void sendMessage(SimpleMsg msg, IForgeGui baseForgeGui, Player player) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uuid", baseForgeGui.getUuid().toString());
        jsonObject.add("msg", msg.getJsonObj());

        String packetStr = jsonObject.toString();
        if (baseForgeGui.getDistributor() != null) {
            JsonObject packet = baseForgeGui.getDistributor().handle(PacketType.message, player, jsonObject);
            packetStr = packet.toString();
        }
        forgeGuiChannel.sendMessage(player, packetStr);

    }

    /**
     * 对所有使用中的界面探测更新
     */
    private void update() {
        for (IForgeGui gui : guiSet) {
            gui.onUpdate();
            if (gui.getSharedProperty().detectChange()) {
                updateChanges(gui);
            }
        }
    }

    /** 将属性增量变更推送给GUI的所有观察者 */
    public void updateChanges(IForgeGui baseForgeGui) {
        JsonObject changes = new JsonObject();
        changes.add("update", baseForgeGui.getSharedProperty().generateAndClearUpdate());
        changes.addProperty("uuid", baseForgeGui.getUuid().toString());
        for (Player viewer : baseForgeGui.getViewers()) {
            String packetStr = changes.toString();
            if (baseForgeGui.getDistributor() != null) {
                JsonObject packet = baseForgeGui.getDistributor().handle(PacketType.update, viewer, changes);
                packetStr = packet.toString();
            }
            forgeGuiChannel.sendMessage(viewer, packetStr);
        }
    }

    /** 将属性增量变更推送给指定玩家 */
    public void updateChanges(IForgeGui baseForgeGui, Player player) {
        JsonObject changes = new JsonObject();
        changes.add("update", baseForgeGui.getSharedProperty().generateAndClearUpdate());
        changes.addProperty("uuid", baseForgeGui.getUuid().toString());
        String packetStr = changes.toString();
        if (baseForgeGui.getDistributor() != null) {
            JsonObject packet = baseForgeGui.getDistributor().handle(PacketType.update, player, changes);
            packetStr = packet.toString();
        }
        forgeGuiChannel.sendMessage(player, packetStr);

    }


    /** 生成界面展示数据包：包含uuid、gui名称和完整属性 */
    public JsonObject generateDisplayPacket(IForgeGui baseForgeGui) {
        JsonObject displayPacket = new JsonObject();
        displayPacket.addProperty("uuid", baseForgeGui.getUuid().toString());
        displayPacket.addProperty("gui", baseForgeGui.getGuiShortName());
        displayPacket.add("property", baseForgeGui.getSharedProperty().generateCompleteJsonAndClearUpdate());
        return displayPacket;
    }

    /** 向所有观察者展示GUI */
    public void display(IForgeGui baseForgeGui) {
        JsonObject packet = generateDisplayPacket(baseForgeGui);
        for (Player viewer : new ArrayList<>(baseForgeGui.getViewers())) {
            display(viewer, baseForgeGui, packet, true);
        }
    }

    /** 向所有观察者展示GUI，可选是否自动关闭其他EasyGui */
    public void display(IForgeGui baseForgeGui, boolean checkAndCloseOtherGui) {
        JsonObject packet = generateDisplayPacket(baseForgeGui);
        for (Player viewer : new ArrayList<>(baseForgeGui.getViewers())) {
            display(viewer, baseForgeGui, packet, checkAndCloseOtherGui);
        }
    }

    /** 向指定玩家展示GUI */
    public void display(Player player, IForgeGui baseForgeGui) {
        JsonObject packet = generateDisplayPacket(baseForgeGui);
        display(player, baseForgeGui, packet, true);
    }

    /** 向指定玩家展示GUI的底层实现：关闭旧GUI、发送数据包、注册状态 */
    public void display(Player player, IForgeGui baseForgeGui, JsonObject packet, boolean checkAndCloseOtherGui) {
        if (isPlayerViewing(player)) {
            removePlayer(player);
        }
        if (checkAndCloseOtherGui) {
            EasyGuiHandler.closePlayerGui(player);
        }

        String packetStr = packet.toString();
        if (baseForgeGui.getDistributor() != null) {
            JsonObject newPacket = baseForgeGui.getDistributor().handle(PacketType.display, player, packet);
            packetStr = newPacket.toString();
        }

        forgeGuiChannel.sendMessage(player, packetStr);
        viewingForgeGui.put(player, baseForgeGui);
        guiSet.add(baseForgeGui);
        baseForgeGui.markDisplayed();
    }

    /** 生成界面关闭数据包 */
    public JsonObject generateClosePacket(IForgeGui baseForgeGui) {
        JsonObject displayPacket = new JsonObject();
        displayPacket.addProperty("uuid", baseForgeGui.getUuid().toString());
        displayPacket.addProperty("close", 0);
        return displayPacket;
    }

    /** 关闭GUI的所有观察者 */
    public void close(IForgeGui baseForgeGui) {
        JsonObject packet = generateClosePacket(baseForgeGui);
        for (Player viewer : new ArrayList<>(baseForgeGui.getViewers())) {
            close(viewer, baseForgeGui, packet);
        }
    }

    /** 关闭指定玩家的GUI */
    public void close(Player player, IForgeGui baseForgeGui) {
        JsonObject packet = generateClosePacket(baseForgeGui);
        close(player, baseForgeGui, packet);
    }

    /** 关闭指定玩家GUI的底层实现：发送关闭包、校验UUID一致性后移除状态 */
    public void close(Player player, IForgeGui baseForgeGui, JsonObject packet) {

        String packetStr = packet.toString();
        if (baseForgeGui.getDistributor() != null) {
            JsonObject newPacket = baseForgeGui.getDistributor().handle(PacketType.close, player, packet);
            packetStr = newPacket.toString();
        }


        forgeGuiChannel.sendMessage(player, packetStr);
        if (!isPlayerViewing(player)) {
            return;
        }
        if (!getPlayerViewing(player).getUuid().equals(baseForgeGui.getUuid())) {
            return;
        }
        removePlayer(player);
    }

    /**
     * 处理客户端发来的消息（由 SimplePluginChannel 回调）
     */
    private void onPluginMessageReceivedJson(Player player, JsonObject jsonObject) {
        if (!jsonObject.has("uuid")) {
            return;
        }
        String uid = jsonObject.get("uuid").getAsString();

        if (!isPlayerViewing(player)) {
            return;
        }

        IForgeGui baseForgeGui = getPlayerViewing(player);

        if (!baseForgeGui.getUuid().toString().equalsIgnoreCase(uid)) {
            return;
        }

        if (jsonObject.has("msg")) {
            baseForgeGui.onMessage(player, jsonObject.getAsJsonObject("msg"));
        } else if (jsonObject.has("close")) {
            removePlayer(player);
        }
    }

    /** 玩家退出时自动清理GUI状态 */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    /** 移除玩家的GUI状态，触发onClose回调，无其他观察者时从活跃集合移除 */
    public void removePlayer(Player player) {
        if (isPlayerViewing(player)) {
            IForgeGui gui = viewingForgeGui.get(player);
            gui.onClose(player);
            viewingForgeGui.remove(player);
            if (!viewingForgeGui.containsValue(gui)) {
                guiSet.remove(gui);
            }

        }
    }

    /** 玩家是否正在查看某个Forge GUI */
    public boolean isPlayerViewing(Player player) {
        return viewingForgeGui.containsKey(player);
    }

    /** 获取玩家正在查看的GUI */
    public IForgeGui getPlayerViewing(Player player) {
        return viewingForgeGui.get(player);
    }

    /** 某个GUI是否仍有玩家在查看 */
    public boolean isViewing(IForgeGui gui) {
        return viewingForgeGui.containsValue(gui);
    }
}
