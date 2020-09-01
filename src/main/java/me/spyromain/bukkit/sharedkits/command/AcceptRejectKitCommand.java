package me.spyromain.bukkit.sharedkits.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.spyromain.bukkit.sharedkits.SharedKits;
import me.spyromain.bukkit.sharedkits.model.Kit;
import me.spyromain.bukkit.sharedkits.model.KitPlayer;

public class AcceptRejectKitCommand implements CommandExecutor {
    private final SharedKits plugin;
    private final boolean accepted;

    public AcceptRejectKitCommand(SharedKits plugin, boolean accepted) {
        this.plugin = plugin;
        this.accepted = accepted;
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
        String requesterPlayerName = args[0];
        String kitName = args[1];

        @SuppressWarnings("deprecation")
        Player requesterPlayer = plugin.getServer().getPlayerExact(requesterPlayerName);

        if (requesterPlayer == null) {
            plugin.sendMessage(player, requesterPlayerName + " is not online.");
            return true;
        }

        KitPlayer kitPlayer = plugin.getKitPlayerManager().getKitPlayer(requesterPlayer);

        if (!kitPlayer.hasKit(kitName)) {
            plugin.sendMessage(player, requesterPlayerName + " doesn't have a kit named " + kitName);
            return true;
        }

        Kit kit = kitPlayer.getKit(kitName);

        if (!kit.hasRequestedSharedPlayer(player.getUniqueId())) {
            plugin.sendMessage(player, requesterPlayerName + " didn't share the kit " + kitName + " with you, or the request expired.");
            return true;
        }

        kit.removeRequestedSharedPlayer(player.getUniqueId());

        if (accepted) {
            kit.addSharedPlayer(player.getUniqueId());
            plugin.getKitPlayerManager().save();

            plugin.sendMessage(player, "Kit " + kitName + " accepted.");
            plugin.sendMessage(requesterPlayer, "Kit " + kitName + " accepted by " + player.getName() + ".");
        }
        else {
            plugin.sendMessage(player, "Kit " + kitName + " ignored.");
            plugin.sendMessage(requesterPlayer, "Kit " + kitName + " ignored by " + player.getName() + ".");
        }

        return true;
    }
}
