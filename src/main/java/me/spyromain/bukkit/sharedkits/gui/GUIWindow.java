package me.spyromain.bukkit.sharedkits.gui;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface GUIWindow {
    void init();
    void onInventoryClick(InventoryClickEvent event);
}
