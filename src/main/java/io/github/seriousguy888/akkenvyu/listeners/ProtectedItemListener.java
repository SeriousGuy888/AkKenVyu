package io.github.seriousguy888.akkenvyu.listeners;

import io.github.seriousguy888.akkenvyu.AkKenVyu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ProtectedItemListener implements Listener {

    private final AkKenVyu plugin;

    public ProtectedItemListener(AkKenVyu plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        ItemStack firstSlotItem = event.getInventory().getItem(0);

        if (firstSlotItem == null || event.getResult() == null) {
            return;
        }

        ItemMeta firstMeta = firstSlotItem.getItemMeta();
        ItemMeta resultMeta = event.getResult().getItemMeta();


        if (firstMeta == null || resultMeta == null) {
            return;
        }

        String customItemId = firstMeta
                .getPersistentDataContainer()
                .get(
                        plugin.getSavedItemsConfig().customItemKey,
                        PersistentDataType.STRING);

        String canonicalName = plugin.getSavedItemsConfig().getCanonicalName(customItemId);

        if (!resultMeta.hasDisplayName()) {
            resultMeta.setDisplayName(canonicalName);
            event.getResult().setItemMeta(resultMeta);
        }
    }
}
