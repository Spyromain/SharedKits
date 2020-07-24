package me.spyromain.bukkit.sharedkits.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import me.spyromain.bukkit.sharedkits.SharedKits;
import me.spyromain.bukkit.sharedkits.model.Kit;
import me.spyromain.bukkit.sharedkits.model.KitPlayer;

public class CreateKitCommand implements CommandExecutor {
    private final SharedKits plugin;

    public CreateKitCommand(SharedKits plugin) {
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
        KitPlayer kitPlayer = plugin.getKitPlayerManager().getKitPlayer(player);
        int maxKitNumber = maxKitNumber(player);

        if (kitPlayer.getKits().size() >= maxKitNumber) {
            plugin.sendMessage(player, "You can't create more than " + maxKitNumber + " kits.");
            return true;
        }

        String kitName = args[0];

        if (kitPlayer.hasKit(kitName)) {
            plugin.sendMessage(player, "Kit " + kitName + " already exists.");
            return true;
        }

        Kit kit = new Kit(kitName);

        kitPlayer.addKit(kit);
        plugin.getKitPlayerManager().save();
        plugin.sendMessage(player, "Kit " + kitName + " created.");

        return true;
    }

    public static int maxKitNumber(Player player) {
        int maxKitNumber = 0;

        for (PermissionAttachmentInfo permissionInfo : player.getEffectivePermissions()) {
            if (!permissionInfo.getPermission().startsWith("sharedkits.createkit.")) {
                continue;
            }

            String permission = permissionInfo.getPermission().substring("sharedkits.createkit.".length());

            if (permission.equals("*")) {
                return Integer.MAX_VALUE;
            }

            int kitNumber;
            try {
                kitNumber = Integer.parseInt(permission);
            }
            catch (NumberFormatException e) {
                continue;
            }

            if (kitNumber > maxKitNumber) {
                maxKitNumber = kitNumber;
            }
        }

        return maxKitNumber;
    }
}
