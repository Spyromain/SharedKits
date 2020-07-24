package me.spyromain.bukkit.sharedkits.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.spyromain.bukkit.sharedkits.Utils;
import me.spyromain.bukkit.sharedkits.model.ItemType;

public class GUIUtils {
    public static ItemStack newIcon(ItemType itemType, String title, String... lore) {
        return newIcon(itemType.getType(), itemType.getDamage(), title, lore);
    }

    public static ItemStack newIcon(ItemType itemType, String title, List<String> lore) {
        return newIcon(itemType.getType(), itemType.getDamage(), title, lore);
    }

    public static ItemStack newIcon(Material type, String title, String... lore) {
        return newIcon(type, (short) 0, title, lore);
    }

    public static ItemStack newIcon(Material type, String title, List<String> lore) {
        return newIcon(type, (short) 0, title, lore);
    }

    public static ItemStack newIcon(Material type, short damage, String title, String... lore) {
        ItemStack icon = new ItemStack(type, 1, damage);
        setItemStackAsIcon(icon, title, lore);
        return icon;
    }

    public static ItemStack newIcon(Material type, short damage, String title, List<String> lore) {
        ItemStack icon = new ItemStack(type, 1, damage);
        setItemStackAsIcon(icon, title, lore);
        return icon;
    }

    public static ItemStack newIcon(ItemStack itemStack, String title, String... lore) {
        ItemStack icon = new ItemStack(itemStack);
        setItemStackAsIcon(icon, title, lore);
        return icon;
    }

    public static ItemStack newIcon(ItemStack itemStack, String title, List<String> lore) {
        ItemStack icon = new ItemStack(itemStack);
        setItemStackAsIcon(icon, title, lore);
        return icon;
    }

    public static void setItemStackAsIcon(ItemStack itemStack, String title, String... lore) {
        setItemStackAsIcon(itemStack, title, Arrays.asList(lore));
    }

    public static void setItemStackAsIcon(ItemStack itemStack, String title, List<String> lore) {
        if (Utils.isEmpty(itemStack)) {
            throw new IllegalArgumentException();
        }

        itemStack.setAmount(1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RESET + title);
        List<String> clearedLore = new ArrayList<String>(lore.size());
        for (int i = 0; i < lore.size(); i++) {
            clearedLore.add(ChatColor.RESET + lore.get(i));
        }
        itemMeta.setLore(clearedLore);
        itemMeta.addItemFlags(ItemFlag.values());
        itemStack.setItemMeta(itemMeta);
    }

    public static boolean isMainInventory(int rawSlot) {
        return 0 <= rawSlot && rawSlot < 54;
    }

    public static boolean isPlayerInventory(int rawSlot) {
        return 54 <= rawSlot && rawSlot < 90;
    }
}
