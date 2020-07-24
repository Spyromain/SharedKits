package me.spyromain.bukkit.sharedkits.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.spyromain.bukkit.sharedkits.SharedKits;
import me.spyromain.bukkit.sharedkits.Utils;

public class KitBlacklistCommand implements CommandExecutor {
    private final SharedKits plugin;

    public KitBlacklistCommand(SharedKits plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.sendMessage(sender, "Player command only.");
            return true;
        }

        if (args.length != 0) {
            return false;
        }

        Player player = (Player) sender;
        ItemStack itemStack = player.getItemInHand();

        if (Utils.isEmpty(itemStack)) {
            plugin.sendMessage(player, "Please hold any items in hand.");
            return true;
        }

        if (plugin.getKitBlacklistManager().contains(itemStack)) {
            plugin.sendMessage(player, "This item is already blacklisted.");
            return true;
        }

        plugin.getKitBlacklistManager().add(itemStack);
        plugin.getKitBlacklistManager().save();
        plugin.sendMessage(player, "Item added to blacklist.");

        return true;
    }
}
