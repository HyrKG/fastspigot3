package cn.hyrkg.fastspigot3.spigot.easygui;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class EasyGuiHandler implements Listener {
    @Getter
    private static Plugin plugin;
    @Getter
    private static EasyGuiHandler instance = null;

    private static CopyOnWriteArrayList<EasyGui> guis = new CopyOnWriteArrayList<>();


    private EasyGuiHandler() {

    }

    public static void init(Plugin plugin) {
        EasyGuiHandler.plugin = plugin;
        if (instance == null) {
            plugin.getServer().getPluginManager().registerEvents(instance = new EasyGuiHandler(), plugin);
        }
    }

    public static void registerGui(EasyGui gui) {
        closePlayerGui(gui.getViewer());
        guis.add(gui);
    }

    public static void destroyGui(EasyGui gui) {
        guis.remove(gui);
    }

    public static void closePlayerGui(Player player) {
        if (isViewing(player)) {
            player.closeInventory();
        }
    }

    public static boolean isViewing(Player player) {
        return getViewing(player) != null;
    }

    public static EasyGui getViewing(Player player) {
        for (EasyGui gui : guis) {
            if (gui.isInventory(player.getInventory())) {
                return gui;
            }
            if (gui.getViewer() != null && gui.getViewer().equals(player)) {
                return gui;
            }
        }
        return null;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        EasyGui viewing = getViewing((Player) e.getWhoClicked());
        if (viewing == null) return;
        viewing.onInventoryClick(e);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        EasyGui viewing = getViewing((Player) e.getWhoClicked());
        if (viewing == null) return;
        viewing.onDrag(e);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player)) return;

        EasyGui viewing = getViewing((Player) e.getPlayer());
        if (viewing == null) return;
        viewing.onClose();
        guis.remove(viewing);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        closePlayerGui(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onOpen(InventoryOpenEvent e) {
        if (!(e.getPlayer() instanceof Player)) return;

        EasyGui viewing = getViewing((Player) e.getPlayer());
        if (viewing == null) return;
        viewing.onOpen(e);
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent e) {
        for (EasyGui gui : new ArrayList<>(guis)) {
            try {
                gui.onClose();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            guis.remove(gui);
            if (gui.getViewer() != null) gui.getViewer().closeInventory();
        }
    }
}
