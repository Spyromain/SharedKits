package me.spyromain.bukkit.sharedkits.gui.window;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.spyromain.bukkit.sharedkits.Utils;
import me.spyromain.bukkit.sharedkits.gui.GUIPlayer;
import me.spyromain.bukkit.sharedkits.gui.GUIUtils;
import me.spyromain.bukkit.sharedkits.gui.GUIWindow;
import me.spyromain.bukkit.sharedkits.model.ItemType;
import me.spyromain.bukkit.sharedkits.model.Kit;
import me.spyromain.bukkit.sharedkits.model.KitPlayer;

public class SharedKitContentsWindow implements GUIWindow {
    public static final int BACK_SLOT = 45;
    public static final int COPY_KIT_SLOT = 49;

    public static final ItemStack BACK_ICON = GUIUtils.newIcon(Material.STAINED_CLAY, (short) 14, "Back");
    public static final ItemStack COPY_KIT_ICON = GUIUtils.newIcon(Material.BOOK_AND_QUILL, "Copy kit");
    public static final ItemStack CANNOT_COPY_KIT_ICON = GUIUtils.newIcon(
        Material.BOOK,
        ChatColor.DARK_RED + "Cannot copy kit",
        ChatColor.GRAY + "You did not obtained some items"
    );

    private final GUIPlayer guiPlayer;
    private final Kit sharedKit;

    public SharedKitContentsWindow(GUIPlayer guiPlayer, Kit sharedKit) {
        this.guiPlayer = guiPlayer;
        this.sharedKit = sharedKit;
    }

    @Override
    public void init() {
        Inventory inventory = guiPlayer.getInventory();
        boolean canCopy = true;

        Set<ItemType> obtainedItems = guiPlayer.getPlugin().getItemPlayerManager().getItemPlayer(guiPlayer.getPlayer()).getItemTypes();
        for (int i = 0; i < sharedKit.getContents().length; i++) {
            ItemStack itemStack = sharedKit.getContents()[i];

            if (!Utils.isEmpty(itemStack) && !obtainedItems.contains(new ItemType(itemStack))) {
                itemStack = new ItemStack(itemStack);
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta.hasLore()) {
                    itemMeta.getLore().add(ChatColor.DARK_RED + "Not obtained");
                }
                else {
                    itemMeta.setLore(Collections.singletonList(ChatColor.DARK_RED + "Not obtained"));
                }
                itemStack.setItemMeta(itemMeta);
                canCopy = false;
            }

            inventory.setItem(i, itemStack);
        }

        inventory.setItem(BACK_SLOT, BACK_ICON);
        inventory.setItem(COPY_KIT_SLOT, canCopy ? COPY_KIT_ICON : CANNOT_COPY_KIT_ICON);
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (GUIUtils.isMainInventory(event.getRawSlot()) && event.getClick() == ClickType.LEFT) {
            if (event.getSlot() == BACK_SLOT) {
                guiPlayer.popWindow();
            } else if (event.getSlot() == COPY_KIT_SLOT) {
                Set<ItemType> itemTypes = new HashSet<ItemType>();
                for (ItemStack itemStack : sharedKit.getContents()) {
                    if (!Utils.isEmpty(itemStack)) {
                        itemTypes.add(new ItemType(itemStack));
                    }
                }

                if (!guiPlayer.getPlugin().getItemPlayerManager().getItemPlayer(guiPlayer.getPlayer()).getItemTypes().containsAll(itemTypes)) {
                    guiPlayer.getPlugin().sendMessage(guiPlayer.getPlayer(), "You cannot copy this shared kit. You did not obtained some items.");
                    return;
                }

                KitPlayer kitPlayer = guiPlayer.getPlugin().getKitPlayerManager().getKitPlayer(guiPlayer.getPlayer());
                Kit newKit = new Kit(getCopyKitName(kitPlayer, sharedKit.getName()));

                newKit.setIcon(sharedKit.getIcon());

                ItemStack[] newContents = new ItemStack[Kit.KIT_MAX_SIZE];
                for (int i = 0; i < Kit.KIT_MAX_SIZE; i++) {
                    ItemStack itemStack = sharedKit.getContents()[i];
                    if (itemStack != null) {
                        newContents[i] = new ItemStack(itemStack);
                    }
                }
                newKit.setContents(newContents);

                kitPlayer.addKit(newKit);
                guiPlayer.getPlugin().getKitPlayerManager().save();

                guiPlayer.getPlugin().sendMessage(guiPlayer.getPlayer(), "Kit copied as " + newKit.getName());
            }
        }
    }

    private static String getCopyKitName(KitPlayer kitPlayer, String oldKitName) {
        String newKitName = oldKitName;

        int number = 2;
        while (kitPlayer.hasKit(newKitName)) {
            newKitName = oldKitName + "(" + number + ")";
            number += 1;
        }

        return newKitName;
    }
}
