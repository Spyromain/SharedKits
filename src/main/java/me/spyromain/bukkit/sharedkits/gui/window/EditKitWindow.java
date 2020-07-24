package me.spyromain.bukkit.sharedkits.gui.window;

import java.util.Arrays;

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

public class EditKitWindow implements GUIWindow {
    public static final int CANCEL_SLOT = 45;
    public static final int INFO_SLOT = 49;
    public static final int SAVE_SLOT = 53;

    public static final ItemStack CANCEL_ICON = GUIUtils.newIcon(
        Material.STAINED_CLAY,
        (short) 14,
        "Cancel"
    );
    public static final ItemStack INFO_ICON = GUIUtils.newIcon(
        Material.BOOK,
        "Commands",
        "Kit inventory",
        "  Left click " + ChatColor.GRAY + "pick or place items",
        "  Right click " + ChatColor.GRAY + "split or place one item",
        "  Drop button " + ChatColor.GRAY + "remove one item",
        "  Drop + CTRL buttons " + ChatColor.GRAY + "remove whole stack",
        "  Shift button + Right click " + ChatColor.GRAY + "increase stack amount",
        "Player inventory",
        "  Left click " + ChatColor.GRAY + "copy whole stack",
        "  Right click " + ChatColor.GRAY + "copy one item",
        "  Shift button + Right click " + ChatColor.GRAY + "copy maximum amount"
    );
    public static final ItemStack SAVE_ICON = GUIUtils.newIcon(
        Material.STAINED_CLAY,
        (short) 5,
        "Save"
    );

    private final GUIPlayer guiPlayer;
    private final Kit kit;

    public EditKitWindow(GUIPlayer guiPlayer, Kit kit) {
        this.guiPlayer = guiPlayer;
        this.kit = kit;
    }

    @Override
    public void init() {
        Inventory inventory = guiPlayer.getInventory();

        inventory.setContents(kit.getContents());

        inventory.setItem(CANCEL_SLOT, CANCEL_ICON);
        inventory.setItem(INFO_SLOT, INFO_ICON);
        inventory.setItem(SAVE_SLOT, SAVE_ICON);
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (GUIUtils.isMainInventory(event.getRawSlot())) {
            if (event.getSlot() < Kit.KIT_MAX_SIZE) {
                if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT) {
                    event.setCancelled(false);
                    return;
                }

                ItemStack clickedItem = event.getCurrentItem();
                ItemStack itemOnCursor = guiPlayer.getPlayer().getItemOnCursor();

                if (!Utils.isEmpty(clickedItem) && Utils.isEmpty(itemOnCursor)) {
                    if (event.getClick() == ClickType.DROP) {
                        int newAmount = clickedItem.getAmount() - 1;
                        if (newAmount <= 0) {
                            guiPlayer.getInventory().setItem(event.getSlot(), null);
                        }
                        else {
                            clickedItem.setAmount(newAmount);
                        }
                    }
                    else if (event.getClick() == ClickType.CONTROL_DROP) {
                        guiPlayer.getInventory().setItem(event.getSlot(), null);
                    }
                    else if (event.getClick() == ClickType.SHIFT_RIGHT) {
                        clickedItem.setAmount(Math.min(clickedItem.getAmount() + 1, clickedItem.getMaxStackSize()));
                    }
                }
            }
            else {
                ItemStack itemOnCursor = guiPlayer.getPlayer().getItemOnCursor();

                if (!Utils.isEmpty(itemOnCursor)) {
                    return;
                }

                if (event.getClick() == ClickType.LEFT) {
                    if (event.getSlot() == CANCEL_SLOT) {
                        guiPlayer.popWindow();
                    }
                    else if (event.getSlot() == SAVE_SLOT) {
                        kit.setContents(Arrays.copyOf(guiPlayer.getInventory().getContents(), Kit.KIT_MAX_SIZE));
                        guiPlayer.getPlugin().getKitPlayerManager().save();
                        guiPlayer.popWindow();
                    }
                }
            }
        }
        else if (GUIUtils.isPlayerInventory(event.getRawSlot())) {
            ItemStack clickedItem = event.getCurrentItem();
            ItemStack itemOnCursor = guiPlayer.getPlayer().getItemOnCursor();

            if (!Utils.isEmpty(clickedItem) && Utils.isEmpty(itemOnCursor)) {
                ItemStack newItem = new ItemStack(clickedItem);

                if (guiPlayer.getPlugin().getKitBlacklistManager().contains(newItem)) {
                    guiPlayer.getPlugin().sendMessage(guiPlayer.getPlayer(), "This item is blacklisted, you can't add it.");
                    return;
                }

                if (event.getClick() == ClickType.LEFT) {
                    guiPlayer.getPlayer().setItemOnCursor(newItem);
                }
                else if (event.getClick() == ClickType.RIGHT) {
                    newItem.setAmount(1);
                    guiPlayer.getPlayer().setItemOnCursor(newItem);
                }
                else if (event.getClick() == ClickType.SHIFT_RIGHT) {
                    newItem.setAmount(newItem.getMaxStackSize());
                    guiPlayer.getPlayer().setItemOnCursor(newItem);
                }
            }
        }
    }
}
