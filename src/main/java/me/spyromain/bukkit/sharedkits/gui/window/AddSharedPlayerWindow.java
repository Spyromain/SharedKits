package me.spyromain.bukkit.sharedkits.gui.window;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.spyromain.bukkit.sharedkits.gui.GUIPageUtils;
import me.spyromain.bukkit.sharedkits.gui.GUIPlayer;
import me.spyromain.bukkit.sharedkits.gui.GUIUtils;
import me.spyromain.bukkit.sharedkits.gui.GUIWindow;
import me.spyromain.bukkit.sharedkits.model.Kit;

public class AddSharedPlayerWindow implements GUIWindow {
    public static final int BACK_OR_PREVIOUS_SLOT = 45;
    public static final int REFRESH_SLOT = 49;
    public static final int NEXT_SLOT = 53;

    public static final ItemStack BACK_ICON = GUIUtils.newIcon(Material.STAINED_CLAY, (short) 14, "Back");
    public static final ItemStack PREVIOUS_ICON = GUIPageUtils.DEFAULT_PREVIOUS_ICON;
    public static final ItemStack REFRESH_ICON = GUIUtils.newIcon(Material.EMERALD, "Refresh");
    public static final ItemStack NEXT_ICON = GUIPageUtils.DEFAULT_NEXT_ICON;

    private final GUIPlayer guiPlayer;
    private final Kit kit;

    private PageHelper<Player> pageHelper;
    private int currentPage;

    public AddSharedPlayerWindow(final GUIPlayer guiPlayer, final Kit kit) {
        this.guiPlayer = guiPlayer;
        this.kit = kit;

        pageHelper = null;
        currentPage = PageHelper.FIRST_PAGE;
    }

    @Override
    public void init() {
        List<Player> possiblePlayers = new ArrayList<Player>();

        for (Player player : guiPlayer.getPlugin().getServer().getOnlinePlayers()) {
            if (guiPlayer.getPlugin().getKitPlayerManager().getKitPlayer(player).isAcceptingRequests()
                && !kit.getSharedPlayers().contains(player.getUniqueId())
                && !player.equals(guiPlayer.getPlayer())) {
                possiblePlayers.add(player);
            }
        }

        Collections.sort(possiblePlayers, new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        pageHelper = new PageHelper<Player>(Collections.unmodifiableList(possiblePlayers), GUIPageUtils.DEFAULT_MAX_PAGE_SIZE);
        currentPage = pageHelper.getNearestPage(currentPage);
        initPage();
    }

    private void initPage() {
        Inventory inventory = guiPlayer.getInventory();

        for (ListIterator<Player> it = pageHelper.getListPage(currentPage).listIterator(); it.hasNext();) {
            int slot = it.nextIndex();

            inventory.setItem(slot, GUIUtils.newIcon(
                Material.SKULL_ITEM,
                (short) 3,
                it.next().getName()
            ));
        }

        if (!pageHelper.isFirstPage(currentPage)) {
            inventory.setItem(BACK_OR_PREVIOUS_SLOT, PREVIOUS_ICON);
        }
        else {
            inventory.setItem(BACK_OR_PREVIOUS_SLOT, BACK_ICON);
        }

        inventory.setItem(REFRESH_SLOT, REFRESH_ICON);

        if (!pageHelper.isLastPage(currentPage)) {
            inventory.setItem(NEXT_SLOT, NEXT_ICON);
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (GUIUtils.isMainInventory(event.getRawSlot()) && event.getClick() == ClickType.LEFT) {
            List<Player> possiblePlayers = pageHelper.getListPage(currentPage);
            if (event.getSlot() < possiblePlayers.size()) {
                final Player possiblePlayer = possiblePlayers.get(event.getSlot());

                if (!possiblePlayer.isOnline()) {
                    guiPlayer.getPlugin().sendMessage(guiPlayer.getPlayer(), "This player is not online anymore.");
                    return;
                }

                if (kit.hasSharedPlayer(possiblePlayer.getUniqueId())) {
                    guiPlayer.getPlugin().sendMessage(guiPlayer.getPlayer(), "This player already has access to this kit.");
                    return;
                }

                if (kit.hasRequestedSharedPlayer(possiblePlayer.getUniqueId())) {
                    guiPlayer.getPlugin().sendMessage(guiPlayer.getPlayer(), "You already sent a request to this player. Please wait.");
                    return;
                }

                if (!guiPlayer.getPlugin().getKitPlayerManager().getKitPlayer(possiblePlayer).isAcceptingRequests()) {
                    guiPlayer.getPlugin().sendMessage(guiPlayer.getPlayer(), "This player doesn't accept requests.");
                    return;
                }

                kit.addRequestedSharedPlayer(possiblePlayer.getUniqueId());

                guiPlayer.getPlugin().getServer().getScheduler().runTaskLater(guiPlayer.getPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        if (!kit.hasRequestedSharedPlayer(possiblePlayer.getUniqueId())) {
                            return;
                        }

                        kit.removeRequestedSharedPlayer(possiblePlayer.getUniqueId());

                        if (guiPlayer.getPlayer().isOnline()) {
                            guiPlayer.getPlugin().sendMessage(guiPlayer.getPlayer(), "The request sent to " + possiblePlayer.getName() + " has just expired.");
                        }

                        if (possiblePlayer.isOnline()) {
                            guiPlayer.getPlugin().sendMessage(possiblePlayer, "The request sent by " + guiPlayer.getPlayer().getName() + " has just expired.");
                        }
                    }
                }, 5 * 60 * 20); // 5 minutes

                guiPlayer.getPlugin().getServer().dispatchCommand(
                    guiPlayer.getPlugin().getServer().getConsoleSender(),
                    String.format(
                        "tellraw "
                        + "%1$s "
                        + "[{\"text\":\"[%2$s] %3$s would like to share kit \"},"
                        + "{\"text\":\"%4$s\",\"color\":\"gold\"},"
                        + "{\"text\":\" with you.\n \u0020 \u0020 \u0020 \u0020 \u0020\"},"
                        + "{\"text\":\"[Agree]\",\"color\":\"dark_green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/acceptkit %3$s %4$s\"}},"
                        + "{\"text\":\" \u0020 \u0020\"},"
                        + "{\"text\":\"[Ignore]\",\"color\":\"dark_red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/refusekit %3$s %4$s\"}}]",
                        possiblePlayer.getName(),
                        guiPlayer.getPlugin().getName(),
                        guiPlayer.getPlayer().getName(),
                        kit.getName()
                    )
                );

                guiPlayer.getPlugin().sendMessage(guiPlayer.getPlayer(), "Request sent to " + possiblePlayer.getName());
            }
            else if (event.getSlot() == BACK_OR_PREVIOUS_SLOT) {
                if (!pageHelper.isFirstPage(currentPage)) {
                    currentPage -= 1;
                    guiPlayer.getInventory().clear();
                    initPage();
                }
                else {
                    guiPlayer.popWindow();
                }
            }
            else if (event.getSlot() == REFRESH_SLOT) {
                guiPlayer.getInventory().clear();
                currentPage = PageHelper.FIRST_PAGE;
                init();
            }
            else if (event.getSlot() == NEXT_SLOT) {
                if (!pageHelper.isLastPage(currentPage)) {
                    currentPage += 1;
                    guiPlayer.getInventory().clear();
                    initPage();
                }
            }
        }
    }
}
