package me.spyromain.bukkit.sharedkits.tabcompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.spyromain.bukkit.sharedkits.SharedKits;
import me.spyromain.bukkit.sharedkits.model.Kit;

public class AcceptRejectKitCompleter implements TabCompleter {
    private final SharedKits plugin;

    public AcceptRejectKitCompleter(SharedKits plugin) {
        this.plugin = plugin;
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return null;
        }

        if (args.length == 2) {
            Player player = (Player) sender;
            String requesterPlayerName = args[0];
            String kitName = args[1];

            Player requesterPlayer = plugin.getServer().getPlayerExact(requesterPlayerName);

            if (requesterPlayer == null) {
                return Collections.emptyList();
            }

            List<String> kitNames = new ArrayList<String>();

            for (Kit kit : plugin.getKitPlayerManager().getKitPlayer(requesterPlayer).getKits()) {
                if (kit.hasRequestedSharedPlayer(player.getUniqueId())) {
                    kitNames.add(kit.getName());
                }
            }

            return StringUtil.copyPartialMatches(
                kitName,
                kitNames,
                new ArrayList<String>()
            );
        }

        return Collections.emptyList();
    }
}
