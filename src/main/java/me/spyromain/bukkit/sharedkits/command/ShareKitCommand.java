package me.spyromain.bukkit.sharedkits.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.spyromain.bukkit.sharedkits.SharedKits;
import me.spyromain.bukkit.sharedkits.model.KitPlayer;

public class ShareKitCommand implements CommandExecutor {
    private final SharedKits plugin;

    public ShareKitCommand(SharedKits plugin) {
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
        String acceptingRequests = args[0];
        KitPlayer kitPlayer = plugin.getKitPlayerManager().getKitPlayer(player);

        if (acceptingRequests.equals("on")) {
            if (kitPlayer.isAcceptingRequests()) {
                plugin.sendMessage(player, "You already accept to receive requests.");
            }
            else {
                plugin.sendMessage(player, "You will now receive requests if someone wants to share a kit with you.");
                kitPlayer.setAcceptingRequests(true);
                plugin.getKitPlayerManager().save();
            }
            return true;
        }

        if (acceptingRequests.equals("off")) {
            if (!kitPlayer.isAcceptingRequests()) {
                plugin.sendMessage(player, "You already refuse to receive requests.");
            }
            else {
                plugin.sendMessage(player, "You will no longer receive requests if someone wants to share a kit with you.");
                kitPlayer.setAcceptingRequests(false);
                plugin.getKitPlayerManager().save();
            }
            return true;
        }

        return false;
    }
}
