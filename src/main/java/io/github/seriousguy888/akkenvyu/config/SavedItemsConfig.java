package io.github.seriousguy888.akkenvyu.config;

import io.github.seriousguy888.akkenvyu.AkKenVyu;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SavedItemsConfig extends ConfigReader {
    public final NamespacedKey customItemKey = new NamespacedKey(plugin, "custom_item_id");

    public final Map<String, String> canonicalNameCache;

    public SavedItemsConfig(AkKenVyu plugin, String name, boolean mustRetainComments) throws FileNotFoundException {
        super(plugin, name, mustRetainComments);

        canonicalNameCache = new HashMap<>();
    }


    public ItemStack saveItem(ItemStack item, String id) {
        if (getItem(id) != null) {
            return null;
        }

        // Prevent changes the saved copy being coupled to the original.
        ItemStack itemClone = item.clone();

        ItemMeta meta = itemClone.getItemMeta();
        if (meta == null) {
            return null;
        }

        itemClone.setAmount(1);
        meta.getPersistentDataContainer().set(customItemKey, PersistentDataType.STRING, id);
        itemClone.setItemMeta(meta);

        config.set("items." + id.toLowerCase(), itemClone);
        return itemClone;
    }

    @Nullable
    public ItemStack getItem(String id) {
        if (!config.contains("items." + id.toLowerCase())) {
            return null;
        }

        return config.getItemStack("items." + id.toLowerCase());
    }

    public boolean deleteItem(String id) {
        if (!config.contains("items." + id.toLowerCase())) {
            return false;
        }

        config.set("items." + id.toLowerCase(), null);
        canonicalNameCache.remove(id);
        return true;
    }

    public List<String> getSavedItemIds() {
        ConfigurationSection section = config.getConfigurationSection("items");
        if (section == null) {
            return List.of();
        }

        return section.getKeys(false).stream().toList();
    }

    public String getCanonicalName(String id) {
        if (canonicalNameCache.containsKey(id)) {
            return canonicalNameCache.get(id);
        }

        if (id == null) {
            return null;
        }

        ItemStack item = plugin.getSavedItemsConfig().getItem(id);
        if (item == null) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }

        String canonicalName = meta.getDisplayName();

        canonicalNameCache.put(id, canonicalName);
        return canonicalName;
    }
}
