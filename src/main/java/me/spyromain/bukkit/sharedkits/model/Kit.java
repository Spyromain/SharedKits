package me.spyromain.bukkit.sharedkits.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import me.spyromain.bukkit.sharedkits.Utils;

@SerializableAs("Kit")
public class Kit implements ConfigurationSerializable {
    public static final int KIT_MAX_SIZE = 9 + 3 * 9;
    public static final String KIT_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

    private final String name;
    private Date creationDate;
    private ItemType icon;
    private ItemStack[] contents;
    private Set<UUID> sharedPlayers;

    private Set<UUID> requestedSharedPlayers;

    public Kit(String name) {
        this(name, Calendar.getInstance().getTime(), new ItemType(), new ItemStack[KIT_MAX_SIZE], new HashSet<UUID>());
    }

    public Kit(String name, Date creationDate, ItemType icon, ItemStack[] contents, Set<UUID> sharedPlayers) {
        this.name = name;
        this.creationDate = creationDate;
        this.icon = icon;
        if (contents.length != KIT_MAX_SIZE) {
            throw new IllegalArgumentException();
        }
        this.contents = contents;
        this.sharedPlayers = sharedPlayers;

        requestedSharedPlayers = new HashSet<UUID>();
    }

    public String getName() {
        return name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public ItemType getIcon() {
        return icon;
    }

    public void setIcon(ItemType icon) {
        this.icon = icon;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public void setContents(ItemStack[] contents) {
        this.contents = contents;
    }

    public boolean isEmpty() {
        return firstItemStack() == null;
    }

    public boolean isFull() {
        for (int i = 0; i < KIT_MAX_SIZE; i++) {
            if (Utils.isEmpty(contents[i])) {
                return false;
            }
        }
        return true;
    }

    public ItemStack firstItemStack() {
        for (int i = 0; i < KIT_MAX_SIZE; i++) {
            if (!Utils.isEmpty(contents[i])) {
                return contents[i];
            }
        }
        return null;
    }

    public int lastItemStackIndex() {
        for (int i = KIT_MAX_SIZE - 1; i >= 0; i--) {
            if (!Utils.isEmpty(contents[i])) {
                return i;
            }
        }
        return -1;
    }

    public void addItemStack(ItemStack itemStack) {
        for (int i = 0; i < KIT_MAX_SIZE; i++) {
            if (Utils.isEmpty(contents[i])) {
                contents[i] = itemStack;
                return;
            }
        }
        throw new IllegalStateException("Kit is full");
    }

    public Set<UUID> getSharedPlayers() {
        return Collections.unmodifiableSet(sharedPlayers);
    }

    public boolean hasSharedPlayer(UUID playerUuid) {
        return sharedPlayers.contains(playerUuid);
    }

    public void addSharedPlayer(UUID playerUuid) {
        sharedPlayers.add(playerUuid);
    }

    public void removeSharedPlayer(UUID playerUuid) {
        sharedPlayers.remove(playerUuid);
    }

    public Set<UUID> getRequestedSharedPlayers() {
        return Collections.unmodifiableSet(requestedSharedPlayers);
    }

    public boolean hasRequestedSharedPlayer(UUID playerUuid) {
        return requestedSharedPlayers.contains(playerUuid);
    }

    public void addRequestedSharedPlayer(UUID playerUuid) {
        requestedSharedPlayers.add(playerUuid);
    }

    public void removeRequestedSharedPlayer(UUID playerUuid) {
        requestedSharedPlayers.remove(playerUuid);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> values = new LinkedHashMap<String, Object>();
        values.put("name", name);
        values.put("creation-date", new SimpleDateFormat(KIT_DATE_FORMAT).format(creationDate));
        if (icon.getType() != Material.AIR) {
            values.put("icon", icon);
        }
        int last = lastItemStackIndex();
        if (last != -1) {
            values.put("contents", Arrays.copyOf(contents, last + 1));
        }
        if (!sharedPlayers.isEmpty()) {
            List<String> sharedPlayersList = new ArrayList<String>();
            for (UUID playerUuid : sharedPlayers) {
                sharedPlayersList.add(playerUuid.toString());
            }
            values.put("shared-players", sharedPlayersList);
        }
        return values;
    }

    @SuppressWarnings("unchecked")
    public static Kit deserialize(Map<String, Object> values) throws ParseException {
        Set<UUID> sharedPlayers = new HashSet<UUID>();
        if (values.containsKey("shared-players")) {
            for (String playerUuid : (List<String>) values.get("shared-players")) {
                sharedPlayers.add(UUID.fromString(playerUuid));
            }
        }

        return new Kit(
            (String) values.get("name"),
            values.containsKey("creation-date")
                ? new SimpleDateFormat(KIT_DATE_FORMAT).parse((String) values.get("creation-date"))
                : Calendar.getInstance().getTime(),
            values.containsKey("icon")
                ? (ItemType) values.get("icon")
                : new ItemType(),
            values.containsKey("contents")
                ? ((List<ItemStack>) values.get("contents")).toArray(new ItemStack[KIT_MAX_SIZE])
                : new ItemStack[KIT_MAX_SIZE],
            sharedPlayers
        );
    }
}
