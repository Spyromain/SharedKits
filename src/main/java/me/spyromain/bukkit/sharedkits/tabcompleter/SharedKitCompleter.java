package me.spyromain.bukkit.sharedkits.tabcompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.spyromain.bukkit.sharedkits.SharedKits;
import me.spyromain.bukkit.sharedkits.model.Kit;

public class SharedKitCompleter implements TabCompleter {
    private final SharedKits plugin;

    public SharedKitCompleter(SharedKits plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            String sharerPlayerName = args[0];

            List<String> sharerPlayerNames = new ArrayList<String>();
            for (UUID sharerPlayerUuid : plugin.getKitPlayerManager().getSharedKits(player).keySet()) {
                OfflinePlayer sharerPlayer = plugin.getServer().getOfflinePlayer(sharerPlayerUuid);
                if (sharerPlayer.getName() != null) {
                    sharerPlayerNames.add(sharerPlayer.getName());
                }
            }

            return StringUtil.copyPartialMatches(
                sharerPlayerName,
                sharerPlayerNames,
                new ArrayList<String>()
            );
        }

        if (args.length == 2) {
            String sharerPlayerName = args[0];
            String sharedKitName = args[1];

            Map<UUID, Map<String, Kit>> sharedKits = plugin.getKitPlayerManager().getSharedKits(player);

            for (UUID sharerPlayerUuid : sharedKits.keySet()) {
                OfflinePlayer sharerPlayer = plugin.getServer().getOfflinePlayer(sharerPlayerUuid);

                if (sharerPlayer.getName() == null) {
                    continue;
                }

                if (sharerPlayer.getName().equals(sharerPlayerName)) {
                    return StringUtil.copyPartialMatches(
                        sharedKitName,
                        sharedKits.get(sharerPlayerUuid).keySet(),
                        new ArrayList<String>()
                    );
                }
            }

            return Collections.emptyList();
        }

        return Collections.emptyList();
    }
}
