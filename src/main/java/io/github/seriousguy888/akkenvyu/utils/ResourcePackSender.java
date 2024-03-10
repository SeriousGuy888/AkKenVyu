package io.github.seriousguy888.akkenvyu.utils;

import io.github.seriousguy888.akkenvyu.AkKenVyu;
import org.bukkit.entity.Player;

public class ResourcePackSender {

    /**
     *
     */
    public static void sendResourcePack(AkKenVyu plugin, Player player) {
        String dlUrl = plugin.getGithubFetcher().getDownloadUrl();

        if (dlUrl == null) {
            player.sendMessage("Cannot send you the resource pack because"
                    + " the download URL is currently unavailable for some reason.");
            return;
        }

        byte[] hash = plugin.getGithubFetcher().getSha1Hash();
        player.setResourcePack(dlUrl, hash, "Applying latest resource pack...", false);
    }
}
