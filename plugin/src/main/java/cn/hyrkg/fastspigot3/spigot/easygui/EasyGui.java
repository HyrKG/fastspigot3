package cn.hyrkg.fastspigot3.spigot.easygui;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;

@Getter
public abstract class EasyGui {
    protected Inventory inventory = null;
    private Player viewer;

    @Deprecated

    public Inventory createInventory() {
        return null;
    }

    public void createInventory(int size, String title) {
        Inventory created = createInventory();
        if (created == null)
            this.inventory = Bukkit.createInventory(null, size, title);
        else
            this.inventory = created;
    }

    public EasyGui(Player p) {
        this.viewer = p;
    }


    public boolean isInventory(Inventory compareInv) {
        return inventory.equals(compareInv);
    }

    public void display() {
        boolean isConflict = false;
        if (EasyGuiHandler.isViewing(getViewer()) && EasyGuiHandler.getViewing(getViewer()).inventory.getTitle().equalsIgnoreCase(inventory.getTitle())) {
            EasyGuiHandler.closePlayerGui(getViewer());
            isConflict = true;
        }
        Runnable run = () -> {
            if (inventory != null) {
                viewer.openInventory(inventory);
                EasyGuiHandler.registerGui(this);
            } else
                viewer.closeInventory();
        };
        if (!isConflict) {
            run.run();
        } else {
            Bukkit.getScheduler().runTaskLater(EasyGuiHandler.getPlugin(), run, 10L);
        }
    }

    public final void close() {
        getViewer().closeInventory();
    }

    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        if (event.getAction().equals(InventoryAction.NOTHING)) {
            return;
        }
        onClickItem(event);
    }


    public abstract void onClickItem(InventoryClickEvent event);

    public void onDrag(InventoryDragEvent event) {

    }

    public void onClose() {

    }

    public void onOpen(InventoryOpenEvent event) {

    }
}
