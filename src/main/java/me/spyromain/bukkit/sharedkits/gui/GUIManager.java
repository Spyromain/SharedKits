package me.spyromain.bukkit.sharedkits.gui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import me.spyromain.bukkit.sharedkits.SharedKits;
import me.spyromain.bukkit.sharedkits.gui.window.KitListWindow;

public class GUIManager implements Listener {
    private final SharedKits plugin;
    private final Map<UUID, GUIPlayer> guiPlayers;

    public GUIManager(SharedKits plugin) {
        this.plugin = plugin;
        guiPlayers = new HashMap<UUID, GUIPlayer>();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public SharedKits getPlugin() {
        return plugin;
    }

    public void open(Player player) {
        GUIPlayer guiPlayer = new GUIPlayer(this, player);
        guiPlayers.put(player.getUniqueId(), guiPlayer);
        guiPlayer.pushWindow(new KitListWindow(guiPlayer));
    }

    public void closeAll() {
        for (GUIPlayer guiPlayer : guiPlayers.values()) {
            guiPlayer.getPlayer().closeInventory();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        for (GUIPlayer guiPlayer : guiPlayers.values()) {
            if (event.getInventory().equals(guiPlayer.getInventory())) {
                guiPlayer.onInventoryClick(event);
                break;
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        for (GUIPlayer guiPlayer : guiPlayers.values()) {
            if (event.getInventory().equals(guiPlayer.getInventory())) {
                guiPlayer.onInventoryDrag(event);
                break;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        for (Iterator<GUIPlayer> it = guiPlayers.values().iterator(); it.hasNext();) {
            GUIPlayer guiPlayer = it.next();
            if (event.getInventory().equals(guiPlayer.getInventory())) {
                guiPlayer.onInventoryClose(event);
                guiPlayers.remove(guiPlayer.getPlayer().getUniqueId());
                break;
            }
        }
    }
}
