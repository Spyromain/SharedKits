package me.spyromain.bukkit.sharedkits.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import me.spyromain.bukkit.sharedkits.SharedKits;

public class KitBlacklistManager implements ConfigManager {
    private final SharedKits plugin;
    private final File file;

    private SortedSet<ItemType> blacklist;

    public KitBlacklistManager(SharedKits plugin, File file) {
        this.plugin = plugin;
        this.file = file;

        reload();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void reload() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        blacklist = new TreeSet<ItemType>();

        if (config.contains("kit-blacklist")) {
            blacklist = new TreeSet<ItemType>((List<ItemType>) config.getList("kit-blacklist"));
        }
    }

    @Override
    public void save() {
        FileConfiguration config = new YamlConfiguration();
        config.set("kit-blacklist", new ArrayList<ItemType>(blacklist));

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save blacklist to " + file, e);
        }
    }

    public boolean contains(ItemStack itemStack) {
        return blacklist.contains(new ItemType(itemStack));
    }

    public void add(ItemStack itemStack) {
        blacklist.add(new ItemType(itemStack));
    }

    public void remove(ItemStack itemStack) {
        blacklist.remove(new ItemType(itemStack));
    }
}
