package me.spyromain.bukkit.sharedkits.gui.window;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.spyromain.bukkit.sharedkits.gui.GUIPageUtils;
import me.spyromain.bukkit.sharedkits.gui.GUIPlayer;
import me.spyromain.bukkit.sharedkits.gui.GUIUtils;
import me.spyromain.bukkit.sharedkits.gui.GUIWindow;
import me.spyromain.bukkit.sharedkits.model.Kit;

public class SharedPlayerListWindow implements GUIWindow {
    public static final int BACK_OR_PREVIOUS_SLOT = 45;
    public static final int ADD_PLAYER_SLOT = 49;
    public static final int NEXT_SLOT = 53;

    public static final ItemStack BACK_ICON = GUIUtils.newIcon(Material.STAINED_CLAY, (short) 14, "Back");
    public static final ItemStack PREVIOUS_ICON = GUIPageUtils.DEFAULT_PREVIOUS_ICON;
    public static final ItemStack ADD_PLAYER_ICON = GUIUtils.newIcon(Material.SKULL_ITEM, (short) 3, "Add player");
    public static final ItemStack NEXT_ICON = GUIPageUtils.DEFAULT_NEXT_ICON;

    private final GUIPlayer guiPlayer;
    private final Kit kit;

    private PageHelper<OfflinePlayer> pageHelper;
    private int currentPage;

    public SharedPlayerListWindow(final GUIPlayer guiPlayer, final Kit kit) {
        this.guiPlayer = guiPlayer;
        this.kit = kit;

        pageHelper = null;
        currentPage = PageHelper.FIRST_PAGE;
    }

    @Override
    public void init() {
        List<OfflinePlayer> sharedPlayers = new ArrayList<OfflinePlayer>();

        for (UUID sharedPlayerUuid : kit.getSharedPlayers()) {
            sharedPlayers.add(guiPlayer.getPlugin().getServer().getOfflinePlayer(sharedPlayerUuid));
        }

        Collections.sort(sharedPlayers, new Comparator<OfflinePlayer>() {
            @Override
            public int compare(OfflinePlayer o1, OfflinePlayer o2) {
                return (o1.getName() != null ? o1.getName() : "Unknown").compareTo(o2.getName() != null ? o2.getName() : "Unknown");
            }
        });

        pageHelper = new PageHelper<OfflinePlayer>(Collections.unmodifiableList(sharedPlayers), GUIPageUtils.DEFAULT_MAX_PAGE_SIZE);
        currentPage = pageHelper.getNearestPage(currentPage);
        initPage();
    }

    private void initPage() {
        Inventory inventory = guiPlayer.getInventory();

        for (ListIterator<OfflinePlayer> it = pageHelper.getListPage(currentPage).listIterator(); it.hasNext();) {
            int slot = it.nextIndex();
            OfflinePlayer sharedPlayer = it.next();

            inventory.setItem(slot, GUIUtils.newIcon(
                Material.SKULL_ITEM,
                (short) 3,
                sharedPlayer.getName() != null ? sharedPlayer.getName() : "Unknown"
            ));
        }

        if (!pageHelper.isFirstPage(currentPage)) {
            inventory.setItem(BACK_OR_PREVIOUS_SLOT, PREVIOUS_ICON);
        }
        else {
            inventory.setItem(BACK_OR_PREVIOUS_SLOT, BACK_ICON);
        }

        inventory.setItem(ADD_PLAYER_SLOT, ADD_PLAYER_ICON);

        if (!pageHelper.isLastPage(currentPage)) {
            inventory.setItem(NEXT_SLOT, NEXT_ICON);
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (GUIUtils.isMainInventory(event.getRawSlot()) && event.getClick() == ClickType.LEFT) {
            List<OfflinePlayer> sharedPlayers = pageHelper.getListPage(currentPage);
            if (event.getSlot() < sharedPlayers.size()) {
                final OfflinePlayer sharedPlayer = sharedPlayers.get(event.getSlot());

                guiPlayer.pushWindow(new YesNoWindow(
                    guiPlayer,
                    new Runnable() {
                        @Override
                        public void run() {
                            kit.removeSharedPlayer(sharedPlayer.getUniqueId());
                            guiPlayer.getPlugin().getKitPlayerManager().save();

                            guiPlayer.popWindow();
                        }
                    },
                    new Runnable() {
                        @Override
                        public void run() {
                            guiPlayer.popWindow();
                        }
                    },
                    "Unshare kit " + kit.getName(),
                    ChatColor.GRAY + "Do you want to unshare the kit " + kit.getName(),
                    ChatColor.GRAY + "for the player " + (sharedPlayer.getName() != null ? sharedPlayer.getName() : "Unknown") + "?"
                ));
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
            else if (event.getSlot() == ADD_PLAYER_SLOT) {
                guiPlayer.pushWindow(new AddSharedPlayerWindow(guiPlayer, kit));
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
