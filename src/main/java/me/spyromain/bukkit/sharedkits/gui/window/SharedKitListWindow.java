package me.spyromain.bukkit.sharedkits.gui.window;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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

public class SharedKitListWindow implements GUIWindow {
    public static final int BACK_OR_PREVIOUS_SLOT = 45;
    public static final int NEXT_SLOT = 53;

    public static final ItemStack BACK_ICON = GUIUtils.newIcon(Material.STAINED_CLAY, (short) 14, "Back");
    public static final ItemStack PREVIOUS_ICON = GUIPageUtils.DEFAULT_PREVIOUS_ICON;
    public static final ItemStack NEXT_ICON = GUIPageUtils.DEFAULT_NEXT_ICON;

    private final GUIPlayer guiPlayer;

    private PageHelper<Map.Entry<OfflinePlayer, Kit>> pageHelper;
    private int currentPage;

    public SharedKitListWindow(final GUIPlayer guiPlayer) {
        this.guiPlayer = guiPlayer;

        pageHelper = null;
        currentPage = PageHelper.FIRST_PAGE;
    }

    @Override
    public void init() {
        List<Map.Entry<OfflinePlayer, Kit>> sharedKitList = new ArrayList<Map.Entry<OfflinePlayer, Kit>>();

        for (Map.Entry<UUID, Map<String, Kit>> entry : guiPlayer.getPlugin().getKitPlayerManager().getSharedKits(guiPlayer.getPlayer()).entrySet()) {
            for (Kit kit : entry.getValue().values()) {
                sharedKitList.add(new AbstractMap.SimpleEntry<OfflinePlayer, Kit>(guiPlayer.getPlugin().getServer().getOfflinePlayer(entry.getKey()), kit));
            }
        }

        pageHelper = new PageHelper<Map.Entry<OfflinePlayer, Kit>>(Collections.unmodifiableList(sharedKitList), GUIPageUtils.DEFAULT_MAX_PAGE_SIZE);
        currentPage = pageHelper.getNearestPage(currentPage);
        initPage();
    }

    private void initPage() {
        Inventory inventory = guiPlayer.getInventory();

        for (ListIterator<Map.Entry<OfflinePlayer, Kit>> it = pageHelper.getListPage(currentPage).listIterator(); it.hasNext();) {
            int slot = it.nextIndex();
            Map.Entry<OfflinePlayer, Kit> entry = it.next();
            OfflinePlayer sharedPlayer = entry.getKey();
            Kit sharedKit = entry.getValue();

            List<String> lore = new ArrayList<String>();
            lore.add("Author");
            lore.add("  " + ChatColor.GRAY + (sharedPlayer.getName() != null ? sharedPlayer.getName() : "Unknown"));
            lore.add("Created on");
            lore.add("  " + ChatColor.GRAY + new SimpleDateFormat(Kit.KIT_DATE_FORMAT).format(sharedKit.getCreationDate()));
            lore.add("Contents");
            for (int i = 0; i < Kit.KIT_MAX_SIZE; i++) {
                ItemStack itemStack = sharedKit.getContents()[i];
                if (!Utils.isEmpty(itemStack)) {
                    lore.add("  " + ChatColor.GRAY + itemStack.getAmount() + " × " + itemStack.getType());
                }
            }
            if (lore.size() <= 5) {
                lore.add("  " + ChatColor.GRAY + "Empty");
            } else if (lore.size() >= 21) {
                lore = lore.subList(0, 21);
                lore.add("  " + ChatColor.GRAY + "And more...");
            }

            inventory.setItem(slot, GUIUtils.newIcon(
                guiPlayer.getPlugin().getKitPlayerManager().getIconOrDefault(sharedKit),
                "Shared kit " + ChatColor.DARK_AQUA + sharedKit.getName(),
                lore
            ));
        }

        if (!pageHelper.isFirstPage(currentPage)) {
            inventory.setItem(BACK_OR_PREVIOUS_SLOT, PREVIOUS_ICON);
        }
        else {
            inventory.setItem(BACK_OR_PREVIOUS_SLOT, BACK_ICON);
        }

        if (!pageHelper.isLastPage(currentPage)) {
            inventory.setItem(NEXT_SLOT, NEXT_ICON);
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (GUIUtils.isMainInventory(event.getRawSlot()) && event.getClick() == ClickType.LEFT) {
            List<Map.Entry<OfflinePlayer, Kit>> sharedKits = pageHelper.getListPage(currentPage);
            if (event.getSlot() < sharedKits.size()) {
                Kit sharedKit = sharedKits.get(event.getSlot()).getValue();
                guiPlayer.pushWindow(new SharedKitContentsWindow(guiPlayer, sharedKit));
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
