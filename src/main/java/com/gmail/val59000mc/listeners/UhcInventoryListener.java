package com.gmail.val59000mc.listeners;

import com.gmail.val59000mc.inventory.UhcInventory;
import com.gmail.val59000mc.languages.Lang;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class UhcInventoryListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void on(@NotNull InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;
        if (inventory.getType().getDefaultTitle().equals(Lang.SCENARIO_GLOBAL_INVENTORY)) {
            event.setCancelled(true);
        }
        if (inventory.getHolder() instanceof UhcInventory) {
            event.setCancelled(true);
            ((UhcInventory) inventory.getHolder()).on(event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void on(@NotNull InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof UhcInventory) {
            event.setCancelled(true);
            ((UhcInventory) inventory.getHolder()).on(event);
        }
        if (inventory.getType().getDefaultTitle().equals(Lang.SCENARIO_GLOBAL_INVENTORY)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void on(@NotNull InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof UhcInventory) {
            ((UhcInventory) inventory.getHolder()).on(event);
        }

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void on(@NotNull InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof UhcInventory) {
            ((UhcInventory) inventory.getHolder()).on(event);
        }
    }

}
