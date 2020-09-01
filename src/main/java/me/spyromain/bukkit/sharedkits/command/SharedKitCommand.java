package me.spyromain.bukkit.sharedkits.command;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.spyromain.bukkit.sharedkits.SharedKits;
import me.spyromain.bukkit.sharedkits.Utils;
import me.spyromain.bukkit.sharedkits.model.ItemType;
import me.spyromain.bukkit.sharedkits.model.Kit;

public class SharedKitCommand implements CommandExecutor {
    private final SharedKits plugin;

    public SharedKitCommand(SharedKits plugin) {
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
        String sharerPlayerName = args[0];
        String sharedKitName = args[1];
        Map<UUID, Map<String, Kit>> allSharedKits = plugin.getKitPlayerManager().getSharedKits(player);

        @SuppressWarnings("deprecation")
        OfflinePlayer sharerPlayer = plugin.getServer().getOfflinePlayer(sharerPlayerName);

        if (!allSharedKits.containsKey(sharerPlayer.getUniqueId())) {
            plugin.sendMessage(player, sharerPlayerName + " doesn't share any kits with you.");
            return true;
        }

        Map<String, Kit> sharedKits = allSharedKits.get(sharerPlayer.getUniqueId());
        if (!sharedKits.containsKey(sharedKitName)) {
            plugin.sendMessage(player, "Shared kit " + sharedKitName + " not found.");
            return true;
        }

        Kit sharedKit = sharedKits.get(sharedKitName);

        Set<ItemType> itemTypes = new HashSet<ItemType>();
        for (ItemStack itemStack : sharedKit.getContents()) {
            if (!Utils.isEmpty(itemStack)) {
                itemTypes.add(new ItemType(itemStack));
            }
        }

        if (!plugin.getItemPlayerManager().getItemPlayer(player).getItemTypes().containsAll(itemTypes)) {
            plugin.sendMessage(player, "You cannot use this shared kit. You did not obtained some items.");
            return true;
        }

        player.getInventory().setContents(sharedKit.getContents());
        plugin.sendMessage(player, "Shared kit " + sharedKitName + " given.");

        return true;
    }
}
