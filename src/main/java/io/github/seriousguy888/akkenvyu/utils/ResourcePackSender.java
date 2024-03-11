package io.github.seriousguy888.akkenvyu.utils;

import io.github.seriousguy888.akkenvyu.AkKenVyu;
import org.bukkit.entity.Player;

public class ResourcePackSender {

    public static void sendResourcePack(AkKenVyu plugin, Player player) {
        String dlUrl = plugin.getCachedDataConfig().getDownloadUrl();

        if (dlUrl == null || dlUrl.isBlank()) {
            player.sendMessage("Cannot send you the resource pack because"
                    + " the download URL is currently unavailable for some reason.");
            return;
        }

        byte[] hash = HexStringToByteArray.convert(plugin.getCachedDataConfig().getHash());
        if(hash.length != 20) {
            // The hash must be 20 bytes long, and it will throw an error otherwise.
            hash = null;
        }

        player.setResourcePack(dlUrl, hash, "Applying latest resource pack...", false);
    }
}
