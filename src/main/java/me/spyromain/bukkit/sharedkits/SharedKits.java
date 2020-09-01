package me.spyromain.bukkit.sharedkits;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import me.spyromain.bukkit.sharedkits.command.AddKitCommand;
import me.spyromain.bukkit.sharedkits.command.CreateKitCommand;
import me.spyromain.bukkit.sharedkits.command.CustomKitCommand;
import me.spyromain.bukkit.sharedkits.command.KitBlacklistCommand;
import me.spyromain.bukkit.sharedkits.command.KitGUICommand;
import me.spyromain.bukkit.sharedkits.command.AcceptRejectKitCommand;
import me.spyromain.bukkit.sharedkits.command.RenKitCommand;
import me.spyromain.bukkit.sharedkits.command.ShareKitCommand;
import me.spyromain.bukkit.sharedkits.command.SharedKitCommand;
import me.spyromain.bukkit.sharedkits.gui.GUIManager;
import me.spyromain.bukkit.sharedkits.listener.ItemObtainedListener;
import me.spyromain.bukkit.sharedkits.model.ConfigManager;
import me.spyromain.bukkit.sharedkits.model.ItemPlayer;
import me.spyromain.bukkit.sharedkits.model.ItemPlayerManager;
import me.spyromain.bukkit.sharedkits.model.ItemType;
import me.spyromain.bukkit.sharedkits.model.Kit;
import me.spyromain.bukkit.sharedkits.model.KitBlacklistManager;
import me.spyromain.bukkit.sharedkits.model.KitPlayer;
import me.spyromain.bukkit.sharedkits.model.KitPlayerManager;
import me.spyromain.bukkit.sharedkits.tabcompleter.EmptyCompleter;
import me.spyromain.bukkit.sharedkits.tabcompleter.KitNameCompleter;
import me.spyromain.bukkit.sharedkits.tabcompleter.SharedKitCompleter;
import me.spyromain.bukkit.sharedkits.tabcompleter.ToggleCompleter;

public class SharedKits extends JavaPlugin {
    private List<ConfigManager> configManagers;
    private KitPlayerManager kitPlayerManager;
    private KitBlacklistManager kitBlacklistManager;
    private ItemPlayerManager itemPlayerManager;
    private GUIManager guiManager;

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(Kit.class);
        ConfigurationSerialization.registerClass(KitPlayer.class);
        ConfigurationSerialization.registerClass(ItemType.class);
        ConfigurationSerialization.registerClass(ItemPlayer.class);

        kitPlayerManager = new KitPlayerManager(this, new File(getDataFolder(), "kit-players.yml"));
        kitBlacklistManager = new KitBlacklistManager(this, new File(getDataFolder(), "kit-blacklist.yml"));
        itemPlayerManager = new ItemPlayerManager(this, new File(getDataFolder(), "item-players.yml"));
        configManagers = Arrays.asList(kitPlayerManager, kitBlacklistManager, itemPlayerManager);

        guiManager = new GUIManager(this);
        new ItemObtainedListener(this);

        EmptyCompleter emptyCompleter = new EmptyCompleter();
        KitNameCompleter kitNameCompleter = new KitNameCompleter(this);
        ToggleCompleter toggleCompleter = new ToggleCompleter();

        PluginCommand createkit = getCommand("createkit");
        createkit.setExecutor(new CreateKitCommand(this));
        createkit.setTabCompleter(emptyCompleter);

        PluginCommand addkit = getCommand("addkit");
        addkit.setExecutor(new AddKitCommand(this));
        addkit.setTabCompleter(kitNameCompleter);

        PluginCommand renkit = getCommand("renkit");
        renkit.setExecutor(new RenKitCommand(this));
        renkit.setTabCompleter(kitNameCompleter);

        PluginCommand customkit = getCommand("customkit");
        customkit.setExecutor(new CustomKitCommand(this));
        customkit.setTabCompleter(kitNameCompleter);

        PluginCommand kitgui = getCommand("kitgui");
        kitgui.setExecutor(new KitGUICommand(this));
        kitgui.setTabCompleter(emptyCompleter);

        PluginCommand sharedkit = getCommand("sharedkit");
        sharedkit.setExecutor(new SharedKitCommand(this));
        sharedkit.setTabCompleter(new SharedKitCompleter(this));

        PluginCommand sharekit = getCommand("sharekit");
        sharekit.setExecutor(new ShareKitCommand(this));
        sharekit.setTabCompleter(toggleCompleter);

        PluginCommand acceptKit = getCommand("acceptkit");
        acceptKit.setExecutor(new AcceptRejectKitCommand(this, true));

        PluginCommand rejectKit = getCommand("rejectkit");
        rejectKit.setExecutor(new AcceptRejectKitCommand(this, false));

        PluginCommand kitblacklist = getCommand("kitblacklist");
        kitblacklist.setExecutor(new KitBlacklistCommand(this));
        kitblacklist.setTabCompleter(emptyCompleter);
    }

    @Override
    public void onDisable() {
        guiManager.closeAll();
        itemPlayerManager.save();
    }

    public void reload() {
        for (ConfigManager configManager : configManagers) {
            configManager.reload();
        }
    }

    public KitPlayerManager getKitPlayerManager() {
        return kitPlayerManager;
    }

    public KitBlacklistManager getKitBlacklistManager() {
        return kitBlacklistManager;
    }

    public ItemPlayerManager getItemPlayerManager() {
        return itemPlayerManager;
    }

    public GUIManager getGUIManager() {
        return guiManager;
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage("[" + getName() + "] " + message);
    }
}
