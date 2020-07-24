package me.spyromain.bukkit.sharedkits.gui;

import java.util.ArrayDeque;
import java.util.Deque;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

import me.spyromain.bukkit.sharedkits.SharedKits;

public class GUIPlayer {
    private final GUIManager guiManager;
    private final Player player;
    private final Inventory inventory;
    private final Deque<GUIWindow> windows;

    public GUIPlayer(GUIManager guiManager, Player player) {
        this.guiManager = guiManager;
        this.player = player;
        inventory = guiManager.getPlugin().getServer().createInventory(null, 54, "Kits");
        windows = new ArrayDeque<GUIWindow>();

        player.openInventory(inventory);
    }

    public GUIManager getGUIManager() {
        return guiManager;
    }

    public SharedKits getPlugin() {
        return guiManager.getPlugin();
    }

    public void pushWindow(GUIWindow window) {
        inventory.clear();
        window.init();
        windows.push(window);
    }

    public void popWindow() {
        popWindows(1);
    }

    public void popWindows(int number) {
        for (int i = 0; i < number; i++) {
            windows.pop();
        }
        inventory.clear();
        windows.peek().init();
    }

    public Player getPlayer() {
        return player;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
        windows.peek().onInventoryClick(event);
    }

    public void onInventoryDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

    public void onInventoryClose(InventoryCloseEvent event) {
        event.getPlayer().setItemOnCursor(null);
    }
}
