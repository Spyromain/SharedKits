package me.spyromain.bukkit.sharedkits.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.spyromain.bukkit.sharedkits.SharedKits;
import me.spyromain.bukkit.sharedkits.Utils;
import me.spyromain.bukkit.sharedkits.model.Kit;
import me.spyromain.bukkit.sharedkits.model.KitPlayer;

public class AddKitCommand implements CommandExecutor {
    private final SharedKits plugin;

    public AddKitCommand(SharedKits plugin) {
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

        if (kit.isFull()) {
            plugin.sendMessage(player, "Kit " + kitName + " is full.");
            return true;
        }

        ItemStack itemStack = player.getItemInHand();

        if (Utils.isEmpty(itemStack)) {
            plugin.sendMessage(player, "Please hold any items in hand.");
            return true;
        }

        if (plugin.getKitBlacklistManager().contains(itemStack)) {
            plugin.sendMessage(player, "This item is blacklisted, you can't add it.");
            return true;
        }

        kit.addItemStack(new ItemStack(itemStack));
        plugin.getKitPlayerManager().save();
        plugin.sendMessage(player, itemStack.getAmount() + " × " + itemStack.getType().toString() + " added to kit " + kitName + ".");

        return true;
    }
}
