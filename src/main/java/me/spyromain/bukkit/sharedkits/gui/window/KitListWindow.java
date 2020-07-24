package me.spyromain.bukkit.sharedkits.gui.window;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.spyromain.bukkit.sharedkits.Utils;
import me.spyromain.bukkit.sharedkits.gui.GUIPlayer;
import me.spyromain.bukkit.sharedkits.gui.GUIUtils;
import me.spyromain.bukkit.sharedkits.gui.GUIWindow;
import me.spyromain.bukkit.sharedkits.model.Kit;

public class KitListWindow implements GUIWindow {
    public static final int PREVIOUS_OR_CLOSE_SLOT = 45;
    public static final int NEXT_SLOT = 53;

    public static final ItemStack CLOSE_ICON = GUIUtils.newIcon(Material.DARK_OAK_DOOR_ITEM, "Close");
    public static final ItemStack PREVIOUS_ICON = GUIUtils.newIcon(Material.STAINED_CLAY, (short) 14, "Previous");
    public static final ItemStack NEXT_ICON = GUIUtils.newIcon(Material.STAINED_CLAY, (short) 5, "Next");

    private final GUIPlayer guiPlayer;
    private final int page;
    private final Collection<Kit> kits;

    private List<Kit> kitsPage;
    private int fromIndex;
    private int toIndex;

    public KitListWindow(GUIPlayer guiPlayer) {
        this(guiPlayer, 0);
    }

    public KitListWindow(GUIPlayer guiPlayer, int page) {
        this.guiPlayer = guiPlayer;
        this.page = page;
        this.kits = guiPlayer.getPlugin().getKitPlayerManager().getKitPlayer(guiPlayer.getPlayer()).getKits();
    }

    @Override
    public void init() {
        Inventory inventory = guiPlayer.getInventory();
        fromIndex = page * Kit.KIT_MAX_SIZE;
        toIndex = Math.min(kits.size(), fromIndex + Kit.KIT_MAX_SIZE);
        kitsPage = new ArrayList<Kit>(kits).subList(fromIndex, toIndex);

        for (Kit kit : kitsPage) {
            List<String> lore = new ArrayList<String>();
            lore.add(ChatColor.WHITE + "Created on");
            lore.add("  " + ChatColor.GRAY + new SimpleDateFormat(Kit.KIT_DATE_FORMAT).format(kit.getCreationDate()));
            lore.add(ChatColor.WHITE + "Contents");
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

            inventory.addItem(GUIUtils.newIcon(guiPlayer.getPlugin().getKitPlayerManager().getIconOrDefault(kit),
                    ChatColor.WHITE + "Kit " + ChatColor.GOLD + kit.getName(), lore));
        }

        inventory.setItem(PREVIOUS_OR_CLOSE_SLOT, page == 0 ? CLOSE_ICON : PREVIOUS_ICON);
        if (kits.size() > fromIndex + Kit.KIT_MAX_SIZE) {
            inventory.setItem(NEXT_SLOT, NEXT_ICON);
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (GUIUtils.isMainInventory(event.getRawSlot()) && event.getClick() == ClickType.LEFT) {
            if (event.getSlot() < kitsPage.size()) {
                Kit kit = kitsPage.get(event.getSlot());
                guiPlayer.pushWindow(new KitContentsWindow(guiPlayer, kit));
            }
            else if (event.getSlot() == PREVIOUS_OR_CLOSE_SLOT) {
                if (page == 0) {
                    guiPlayer.getPlayer().closeInventory();
                }
                else {
                    guiPlayer.popWindow();
                }
            }
            else if (event.getSlot() == NEXT_SLOT && kits.size() > fromIndex + Kit.KIT_MAX_SIZE) {
                guiPlayer.pushWindow(new KitListWindow(guiPlayer, page + 1));
            }
        }
    }
}
