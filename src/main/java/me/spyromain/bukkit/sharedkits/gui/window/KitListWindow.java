package me.spyromain.bukkit.sharedkits.gui.window;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.spyromain.bukkit.sharedkits.Utils;
import me.spyromain.bukkit.sharedkits.gui.GUIPageUtils;
import me.spyromain.bukkit.sharedkits.gui.GUIPlayer;
import me.spyromain.bukkit.sharedkits.gui.GUIUtils;
import me.spyromain.bukkit.sharedkits.gui.GUIWindow;
import me.spyromain.bukkit.sharedkits.model.Kit;

public class KitListWindow implements GUIWindow {
    public static final int CLOSE_OR_PREVIOUS_SLOT = 45;
    public static final int SHARED_KIT_LIST_SLOT = 49;
    public static final int NEXT_SLOT = 53;

    public static final ItemStack CLOSE_ICON = GUIUtils.newIcon(Material.DARK_OAK_DOOR_ITEM, "Close");
    public static final ItemStack PREVIOUS_ICON = GUIPageUtils.DEFAULT_PREVIOUS_ICON;
    public static final ItemStack SHARED_KIT_LIST_ICON = GUIUtils.newIcon(Material.STORAGE_MINECART, "Shared kits");
    public static final ItemStack NEXT_ICON = GUIPageUtils.DEFAULT_NEXT_ICON;

    private final GUIPlayer guiPlayer;

    private PageHelper<Kit> pageHelper;
    private int currentPage;

    public KitListWindow(final GUIPlayer guiPlayer) {
        this.guiPlayer = guiPlayer;

        pageHelper = null;
        currentPage = PageHelper.FIRST_PAGE;
    }

    @Override
    public void init() {
        List<Kit> kits = new ArrayList<Kit>(
            guiPlayer
            .getPlugin()
            .getKitPlayerManager()
            .getKitPlayer(guiPlayer.getPlayer())
            .getKits()
        );

        pageHelper = new PageHelper<Kit>(Collections.unmodifiableList(kits), GUIPageUtils.DEFAULT_MAX_PAGE_SIZE);
        currentPage = pageHelper.getNearestPage(currentPage);
        initPage();
    }

    private void initPage() {
        Inventory inventory = guiPlayer.getInventory();

        for (ListIterator<Kit> it = pageHelper.getListPage(currentPage).listIterator(); it.hasNext();) {
            int slot = it.nextIndex();
            Kit kit = it.next();

            List<String> lore = new ArrayList<String>();
            lore.add("Created on");
            lore.add("  " + ChatColor.GRAY + new SimpleDateFormat(Kit.KIT_DATE_FORMAT).format(kit.getCreationDate()));
            lore.add("Contents");
            for (int i = 0; i < Kit.KIT_MAX_SIZE; i++) {
                ItemStack itemStack = kit.getContents()[i];
                if (!Utils.isEmpty(itemStack)) {
                    lore.add("  " + ChatColor.GRAY + itemStack.getAmount() + " × " + itemStack.getType());
                }
            }
            if (lore.size() <= 3) {
                lore.add("  " + ChatColor.GRAY + "Empty");
            } else if (lore.size() >= 21) {
                lore = lore.subList(0, 21);
                lore.add("  " + ChatColor.GRAY + "And more...");
            }

            inventory.setItem(slot, GUIUtils.newIcon(
                guiPlayer.getPlugin().getKitPlayerManager().getIconOrDefault(kit),
                "Kit " + ChatColor.GOLD + kit.getName(),
                lore
            ));
        }

        if (!pageHelper.isFirstPage(currentPage)) {
            inventory.setItem(CLOSE_OR_PREVIOUS_SLOT, PREVIOUS_ICON);
        }
        else {
            inventory.setItem(CLOSE_OR_PREVIOUS_SLOT, CLOSE_ICON);
        }

        inventory.setItem(SHARED_KIT_LIST_SLOT, SHARED_KIT_LIST_ICON);

        if (!pageHelper.isLastPage(currentPage)) {
            inventory.setItem(NEXT_SLOT, NEXT_ICON);
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (GUIUtils.isMainInventory(event.getRawSlot()) && event.getClick() == ClickType.LEFT) {
            List<Kit> kits = pageHelper.getListPage(currentPage);
            if (event.getSlot() < kits.size()) {
                guiPlayer.pushWindow(new KitContentsWindow(guiPlayer, kits.get(event.getSlot())));
            }
            else if (event.getSlot() == CLOSE_OR_PREVIOUS_SLOT) {
                if (!pageHelper.isFirstPage(currentPage)) {
                    currentPage -= 1;
                    guiPlayer.getInventory().clear();
                    initPage();
                }
                else {
                    guiPlayer.getPlugin().getServer().getScheduler().runTask(guiPlayer.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            guiPlayer.getPlayer().closeInventory();
                        }
                    });
                }
            }
            else if (event.getSlot() == SHARED_KIT_LIST_SLOT) {
                guiPlayer.pushWindow(new SharedKitListWindow(guiPlayer));
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
