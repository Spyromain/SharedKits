package me.spyromain.bukkit.sharedkits.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.ItemStack;

import me.spyromain.bukkit.sharedkits.SharedKits;
import me.spyromain.bukkit.sharedkits.Utils;
import me.spyromain.bukkit.sharedkits.model.ItemPlayer;
import me.spyromain.bukkit.sharedkits.model.ItemType;

public class ItemObtainedListener implements Listener {
    private final SharedKits plugin;

    public ItemObtainedListener(SharedKits plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        plugin
            .getItemPlayerManager()
            .getItemPlayer(event.getPlayer())
            .addItemType(new ItemType(event.getItem().getItemStack()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        ItemPlayer itemPlayer = plugin
            .getItemPlayerManager()
            .getItemPlayer(event.getWhoClicked().getUniqueId());

        for (ItemStack itemStack : event.getWhoClicked().getInventory()) {
            if (!Utils.isEmpty(itemStack)) {
                itemPlayer.addItemType(new ItemType(itemStack));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldSave(WorldSaveEvent event) {
        plugin.getItemPlayerManager().save();
    }
}
