package me.spyromain.bukkit.sharedkits.tabcompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class ToggleCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        if (args.length != 1) {
            return Collections.emptyList();
        }

        String acceptingRequests = args[0];

        return StringUtil.copyPartialMatches(
            acceptingRequests,
            Arrays.asList("on", "off"),
            new ArrayList<String>()
        );
    }
}
