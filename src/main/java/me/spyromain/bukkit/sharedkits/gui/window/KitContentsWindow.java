package me.spyromain.bukkit.sharedkits.gui.window;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.spyromain.bukkit.sharedkits.gui.GUIPlayer;
import me.spyromain.bukkit.sharedkits.gui.GUIUtils;
import me.spyromain.bukkit.sharedkits.gui.GUIWindow;
import me.spyromain.bukkit.sharedkits.model.Kit;

public class KitContentsWindow implements GUIWindow {
    public static final int BACK_SLOT = 45;
    public static final int EDIT_NAME_SLOT = 47;
    public static final int EDIT_ICON_SLOT = 48;
    public static final int EDIT_KIT_SLOT = 49;
    public static final int REMOVE_KIT_SLOT = 50;
    public static final int SHARE_KIT_SLOT = 51;

    public static final ItemStack BACK_ICON = GUIUtils.newIcon(
        Material.STAINED_CLAY,
        (short) 14,
        "Back"
    );
    public static final ItemStack EDIT_NAME_ICON = GUIUtils.newIcon(
        Material.NAME_TAG,
        "Edit name",
        ChatColor.GRAY + "Please use the /renkit command"
    );
    public static final ItemStack EDIT_ICON_ICON = GUIUtils.newIcon(
        Material.ITEM_FRAME,
        "Edit icon"
    );
    public static final ItemStack EDIT_KIT_ICON = GUIUtils.newIcon(
        Material.BOOK_AND_QUILL,
        "Edit kit"
    );
    public static final ItemStack REMOVE_KIT_ICON = GUIUtils.newIcon(
        Material.TNT,
        "Remove kit"
    );
    public static final ItemStack SHARE_KIT_ICON = GUIUtils.newIcon(
        Material.SKULL_ITEM,
        (short) 3,
        "Share kit"
    );

    private final GUIPlayer guiPlayer;
    private final Kit kit;

    public KitContentsWindow(GUIPlayer guiPlayer, Kit kit) {
        this.guiPlayer = guiPlayer;
        this.kit = kit;
    }

    @Override
    public void init() {
        Inventory inventory = guiPlayer.getInventory();

        inventory.setContents(kit.getContents());

        inventory.setItem(BACK_SLOT, BACK_ICON);
        inventory.setItem(EDIT_NAME_SLOT, EDIT_NAME_ICON);
        inventory.setItem(EDIT_ICON_SLOT, EDIT_ICON_ICON);
        inventory.setItem(EDIT_KIT_SLOT, EDIT_KIT_ICON);
        inventory.setItem(REMOVE_KIT_SLOT, REMOVE_KIT_ICON);
        inventory.setItem(SHARE_KIT_SLOT, SHARE_KIT_ICON);
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (GUIUtils.isMainInventory(event.getRawSlot()) && event.getClick() == ClickType.LEFT) {
            if (event.getSlot() == BACK_SLOT) {
                guiPlayer.popWindow();
            }
            else if (event.getSlot() == EDIT_ICON_SLOT) {
                guiPlayer.pushWindow(new EditIconWindow(guiPlayer, kit));
            }
            else if (event.getSlot() == EDIT_KIT_SLOT) {
                guiPlayer.pushWindow(new EditKitWindow(guiPlayer, kit));
            }
            else if (event.getSlot() == REMOVE_KIT_SLOT) {
                guiPlayer.pushWindow(new YesNoWindow(
                    guiPlayer,
                    "Do you really want to remove the kit?",
                    new Runnable() {
                        @Override
                        public void run() {
                            guiPlayer.getPlugin().getKitPlayerManager().getKitPlayer(guiPlayer.getPlayer()).removeKit(kit.getName());
                            guiPlayer.getPlugin().getKitPlayerManager().save();
                            guiPlayer.popWindows(2);
                        }
                    },
                    new Runnable() {
                        @Override
                        public void run() {
                            guiPlayer.popWindow();
                        }
                    }
                ));
            }
            else if (event.getSlot() == SHARE_KIT_SLOT) {
                guiPlayer.getPlugin().sendMessage(guiPlayer.getPlayer(), "Coming soon...");
            }
        }
    }
}
