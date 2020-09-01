package me.spyromain.bukkit.sharedkits.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.spyromain.bukkit.sharedkits.SharedKits;

public class ItemPlayerManager implements ConfigManager {
    private final SharedKits plugin;
    private final File file;

    private Map<UUID, ItemPlayer> itemsPlayers;

    public ItemPlayerManager(SharedKits plugin, File file) {
        this.plugin = plugin;
        this.file = file;

        reload();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void reload() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        itemsPlayers = new HashMap<UUID, ItemPlayer>();

        if (config.contains("item-players")) {
            for (ItemPlayer itemPlayer : (List<ItemPlayer>) config.getList("item-players")) {
                itemsPlayers.put(itemPlayer.getPlayerUuid(), itemPlayer);
            }
        }
    }

    @Override
    public void save() {
        FileConfiguration config = new YamlConfiguration();
        config.set("item-players", new ArrayList<ItemPlayer>(itemsPlayers.values()));

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save item types to " + file, e);
        }
    }

    public ItemPlayer getItemPlayer(Player player) {
        return getItemPlayer(player.getUniqueId());
    }

    public ItemPlayer getItemPlayer(UUID playerUuid) {
        if (!itemsPlayers.containsKey(playerUuid)) {
            itemsPlayers.put(playerUuid, new ItemPlayer(playerUuid));
        }
        return itemsPlayers.get(playerUuid);
    }
}
