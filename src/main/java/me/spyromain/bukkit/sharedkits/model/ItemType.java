package me.spyromain.bukkit.sharedkits.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

@SerializableAs("ItemType")
final public class ItemType implements Comparable<ItemType>, ConfigurationSerializable {
    private final Material type;
    private final short damage;

    public ItemType() {
        this(Material.AIR);
    }

    public ItemType(Material type) {
        this(type, (short) 0);
    }

    public ItemType(Material type, short damage) {
        this.type = type;
        this.damage = type.getMaxDurability() == 0 ? damage : 0;
    }

    public ItemType(ItemStack itemStack) {
        this(itemStack.getType(), itemStack.getDurability());
    }

    public Material getType() {
        return type;
    }

    public short getDamage() {
        return damage;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int hashCode() {
        return (type.getId() << 8) ^ damage;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ItemType other = (ItemType) obj;
        if (damage != other.damage)
            return false;
        if (type != other.type)
            return false;
        return true;
    }

    @Override
    public int compareTo(ItemType o) {
        if (type != o.type) {
            return type.compareTo(o.type);
        }
        return damage - o.damage;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> values = new LinkedHashMap<String, Object>();
        values.put("type", type.toString());
        if (damage != 0) {
            values.put("damage", damage);
        }
        return values;
    }

    public static ItemType deserialize(Map<String, Object> values) {
        return new ItemType(
            Material.getMaterial((String) values.get("type")),
            values.containsKey("damage") ? ((Integer) values.get("damage")).shortValue() : 0
        );
    }
}
