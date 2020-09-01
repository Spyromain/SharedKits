package me.spyromain.bukkit.sharedkits.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.spyromain.bukkit.sharedkits.SharedKits;

public class KitPlayerManager implements ConfigManager {
    private final SharedKits plugin;
    private final File file;

    private Map<UUID, KitPlayer> kitPlayers;

    public KitPlayerManager(SharedKits plugin, File file) {
        this.plugin = plugin;
        this.file = file;

        reload();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void reload() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        kitPlayers = new HashMap<UUID, KitPlayer>();

        if (config.contains("kit-players")) {
            for (KitPlayer kitPlayer : (List<KitPlayer>) config.getList("kit-players")) {
                kitPlayers.put(kitPlayer.getPlayerUuid(), kitPlayer);
            }
        }
    }

    @Override
    public void save() {
        FileConfiguration config = new YamlConfiguration();
        config.set("kit-players", new ArrayList<KitPlayer>(kitPlayers.values()));

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save kits to " + file, e);
        }
    }

    public KitPlayer getKitPlayer(Player player) {
        return getKitPlayer(player.getUniqueId());
    }

    public KitPlayer getKitPlayer(UUID playerUuid) {
        if (!kitPlayers.containsKey(playerUuid)) {
            kitPlayers.put(playerUuid, new KitPlayer(playerUuid));
        }
        return kitPlayers.get(playerUuid);
    }

    public Map<UUID, Map<String, Kit>> getSharedKits(Player player) {
        return getSharedKits(player.getUniqueId());
    }

    public Map<UUID, Map<String, Kit>> getSharedKits(UUID playerUuid) {
        Map<UUID, Map<String, Kit>> sharedKits = new HashMap<UUID, Map<String, Kit>>();

        for (KitPlayer kitPlayer : kitPlayers.values()) {
            for (Kit kit : kitPlayer.getKits()) {
                if (!kit.hasSharedPlayer(playerUuid)) {
                    continue;
                }

                if (!sharedKits.containsKey(kitPlayer.getPlayerUuid())) {
                    sharedKits.put(kitPlayer.getPlayerUuid(), new HashMap<String, Kit>());
                }
                sharedKits.get(kitPlayer.getPlayerUuid()).put(kit.getName(), kit);
            }
        }

        return sharedKits;
    }

    public ItemType getIconOrDefault(Kit kit) {
        if (kit.getIcon().getType() != Material.AIR) {
            return kit.getIcon();
        }

        ItemStack firstItem = kit.firstItemStack();

        if (firstItem != null) {
            return new ItemType(firstItem);
        }

        return new ItemType(Material.GLASS);
    }
}
