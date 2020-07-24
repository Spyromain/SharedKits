package me.spyromain.bukkit.sharedkits.gui.window;

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
import me.spyromain.bukkit.sharedkits.model.ItemType;
import me.spyromain.bukkit.sharedkits.model.Kit;

public class EditIconWindow implements GUIWindow {
    public static final int INFO_SLOT = 13;
    public static final int ICON_SLOT = 31;
    public static final int BACK_SLOT = 45;
    public static final int REMOVE_ICON_SLOT = 49;
    public static final int SAVE_SLOT = 53;

    public static final ItemStack INFO_ICON = GUIUtils.newIcon(
        Material.BOOK,
        "Edit icon",
        ChatColor.GRAY + "Please select an item in your inventory",
        ChatColor.GRAY + "to use it as icon"
    );
    public static final ItemStack BACK_ICON = GUIUtils.newIcon(
        Material.STAINED_CLAY,
        (short) 14,
        "Back"
    );
    public static final ItemStack REMOVE_ICON_ICON = GUIUtils.newIcon(
        Material.TNT,
        "Remove icon",
        ChatColor.GRAY + "This will use the first item of the kit as icon",
        ChatColor.GRAY + "or a glass block if the kit is empty"
    );
    public static final ItemStack SAVE_ICON = GUIUtils.newIcon(
        Material.STAINED_CLAY,
        (short) 5,
        "Save icon"
    );

    private final GUIPlayer guiPlayer;
    private final Kit kit;

    private ItemType newIcon;

    public EditIconWindow(GUIPlayer guiPlayer, Kit kit) {
        this.guiPlayer = guiPlayer;
        this.kit = kit;
    }

    @Override
    public void init() {
        Inventory inventory = guiPlayer.getInventory();
        newIcon = kit.getIcon();

        inventory.setItem(INFO_SLOT, INFO_ICON);

        if (newIcon.getType() != Material.AIR) {
            inventory.setItem(ICON_SLOT, GUIUtils.newIcon(newIcon, "Icon"));
        }

        inventory.setItem(BACK_SLOT, BACK_ICON);
        inventory.setItem(REMOVE_ICON_SLOT, REMOVE_ICON_ICON);
        inventory.setItem(SAVE_SLOT, SAVE_ICON);
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (GUIUtils.isMainInventory(event.getRawSlot()) && event.getClick() == ClickType.LEFT) {
            if (event.getSlot() == BACK_SLOT) {
                guiPlayer.popWindow();
            }
            else if (event.getSlot() == REMOVE_ICON_SLOT) {
                newIcon = new ItemType();
                guiPlayer.getInventory().setItem(ICON_SLOT, null);
            }
            else if (event.getSlot() == SAVE_SLOT) {
                kit.setIcon(newIcon);
                guiPlayer.getPlugin().getKitPlayerManager().save();
                guiPlayer.popWindow();
            }
        }
        else if (GUIUtils.isPlayerInventory(event.getRawSlot()) && event.getClick() == ClickType.LEFT) {
            ItemStack itemStack = event.getCurrentItem();

            if (Utils.isEmpty(itemStack)) {
                return;
            }

            newIcon = new ItemType(itemStack);
            guiPlayer.getInventory().setItem(31, GUIUtils.newIcon(newIcon, "Icon"));
        }
    }
}
