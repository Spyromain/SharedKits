package me.spyromain.bukkit.sharedkits;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Utils {
    public static boolean isEmpty(ItemStack itemStack) {
        return itemStack == null || itemStack.getType() == Material.AIR;
    }
}
