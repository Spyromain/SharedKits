package me.spyromain.bukkit.sharedkits.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.spyromain.bukkit.sharedkits.SharedKits;
import me.spyromain.bukkit.sharedkits.model.Kit;
import me.spyromain.bukkit.sharedkits.model.KitPlayer;

public class RenKitCommand implements CommandExecutor {
    private final SharedKits plugin;

    public RenKitCommand(SharedKits plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.sendMessage(sender, "Player command only.");
            return true;
        }

        if (args.length != 2) {
            return false;
        }

        Player player = (Player) sender;
        String oldName = args[0];
        String newName = args[1];
        KitPlayer kitPlayer = plugin.getKitPlayerManager().getKitPlayer(player);

        if (!kitPlayer.hasKit(oldName)) {
            plugin.sendMessage(player, "Kit " + oldName + " not found.");
            return true;
        }

        if (kitPlayer.hasKit(newName)) {
            plugin.sendMessage(player, "Kit " + newName + " already exists.");
            return true;
        }

        Kit newKit = new Kit(newName, kitPlayer.getKit(oldName));
        kitPlayer.removeKit(oldName);
        kitPlayer.addKit(newKit);
        plugin.getKitPlayerManager().save();
        plugin.sendMessage(player, oldName + " renamed as " + newName);

        return true;
    }
}
