package me.spyromain.bukkit.sharedkits.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.spyromain.bukkit.sharedkits.SharedKits;

public class KitGUICommand implements CommandExecutor {
    private final SharedKits plugin;

    public KitGUICommand(SharedKits plugin) {
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
        plugin.getGUIManager().open(player);

        return true;
    }
}
