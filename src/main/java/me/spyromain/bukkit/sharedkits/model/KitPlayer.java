package me.spyromain.bukkit.sharedkits.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("KitPlayer")
public class KitPlayer implements ConfigurationSerializable {
    private final UUID playerUuid;
    private boolean acceptingRequests;
    private SortedMap<String, Kit> kits;

    public KitPlayer(UUID playerUuid) {
        this(playerUuid, true, new TreeMap<String, Kit>());
    }

    public KitPlayer(UUID playerUuid, boolean acceptingRequests, SortedMap<String, Kit> kits) {
        this.playerUuid = playerUuid;
        this.acceptingRequests = acceptingRequests;
        this.kits = kits;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public boolean isAcceptingRequests() {
        return acceptingRequests;
    }

    public void setAcceptingRequests(boolean acceptingRequests) {
        this.acceptingRequests = acceptingRequests;
    }

    public Kit getKit(String kitName) {
        return kits.get(kitName);
    }

    public Collection<Kit> getKits() {
        return kits.values();
    }

    public Set<String> getKitNames() {
        return kits.keySet();
    }

    public boolean hasKit(String kitName) {
        return kits.containsKey(kitName);
    }

    public void addKit(Kit kit) {
        if (hasKit(kit.getName())) {
            throw new IllegalArgumentException("Kit already exists");
        }
        kits.put(kit.getName(), kit);
    }

    public void removeKit(String kitName) {
        kits.remove(kitName);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> values = new LinkedHashMap<String, Object>();
        values.put("player-uuid", playerUuid.toString());
        if (!acceptingRequests) {
            values.put("accepting-requests", acceptingRequests);
        }
        if (!kits.isEmpty()) {
            values.put("kits", new ArrayList<Kit>(kits.values()));
        }
        return values;
    }

    @SuppressWarnings("unchecked")
    public static KitPlayer deserialize(Map<String, Object> values) {
        SortedMap<String, Kit> kits = new TreeMap<String, Kit>();
        if (values.containsKey("kits")) {
            for (Kit kit : (List<Kit>) values.get("kits")) {
                kits.put(kit.getName(), kit);
            }
        }

        return new KitPlayer(
            UUID.fromString((String) values.get("player-uuid")),
            values.containsKey("accepting-requests") ? (Boolean) values.get("accepting-requests") : true,
            kits
        );
    }
}
