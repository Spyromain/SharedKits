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

public class KitNameCompleter implements TabCompleter {
    private final SharedKits plugin;

    public KitNameCompleter(SharedKits plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        if (args.length != 1) {
            return Collections.emptyList();
        }

        Player player = (Player) sender;
        String kitName = args[0];

        return StringUtil.copyPartialMatches(
            kitName,
            plugin.getKitPlayerManager().getKitPlayer(player).getKitNames(),
            new ArrayList<String>()
        );
    }
}
