package me.spyromain.bukkit.sharedkits.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("ItemPlayer")
public class ItemPlayer implements ConfigurationSerializable {
    private final UUID playerUuid;
    private Set<ItemType> itemTypes;

    public ItemPlayer(UUID playerUuid) {
        this(playerUuid, new HashSet<ItemType>());
    }

    public ItemPlayer(UUID playerUuid, Set<ItemType> itemTypes) {
        this.playerUuid = playerUuid;
        this.itemTypes = itemTypes;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public Set<ItemType> getItemTypes() {
        return Collections.unmodifiableSet(itemTypes);
    }

    public void addItemType(ItemType itemType) {
        itemTypes.add(itemType);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> values = new LinkedHashMap<String, Object>();
        values.put("player-uuid", playerUuid.toString());
        if (!itemTypes.isEmpty()) {
            values.put("item-types", new ArrayList<ItemType>(itemTypes));
        }
        return values;
    }

    @SuppressWarnings("unchecked")
    public static ItemPlayer deserialize(Map<String, Object> values) {
        Set<ItemType> itemTypes = new HashSet<ItemType>();
        if (values.containsKey("item-types")) {
            itemTypes.addAll((List<ItemType>) values.get("item-types"));
        }

        return new ItemPlayer(
            UUID.fromString((String) values.get("player-uuid")),
            itemTypes
        );
    }
}
