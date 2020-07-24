package me.spyromain.bukkit.sharedkits.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.spyromain.bukkit.sharedkits.SharedKits;
import me.spyromain.bukkit.sharedkits.model.Kit;
import me.spyromain.bukkit.sharedkits.model.KitPlayer;

public class CustomKitCommand implements CommandExecutor {
    private final SharedKits plugin;

    public CustomKitCommand(SharedKits plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.sendMessage(sender, "Player command only.");
            return true;
        }

        if (args.length != 1) {
            return false;
        }

        Player player = (Player) sender;
        String kitName = args[0];
        KitPlayer kitPlayer = plugin.getKitPlayerManager().getKitPlayer(player);

        if (!kitPlayer.hasKit(kitName)) {
            plugin.sendMessage(player, "Kit " + kitName + " not found.");
            return true;
        }

        Kit kit = kitPlayer.getKit(kitName);
        player.getInventory().setContents(kit.getContents());
        plugin.sendMessage(player, "Kit " + kitName + " given.");

        return true;
    }
}
