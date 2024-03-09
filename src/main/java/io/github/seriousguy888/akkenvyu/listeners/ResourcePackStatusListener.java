package io.github.seriousguy888.akkenvyu.listeners;

import io.github.seriousguy888.akkenvyu.AkKenVyu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public class ResourcePackStatusListener implements Listener {

    private final AkKenVyu plugin;

    public ResourcePackStatusListener(AkKenVyu plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPackChange(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();
        PlayerResourcePackStatusEvent.Status status = event.getStatus();


        switch (status) {
            case ACCEPTED -> player.sendMessage("Resource pack accepted. Beginning download...");
            case SUCCESSFULLY_LOADED -> player.sendMessage("Resource pack loaded!");
            case FAILED_DOWNLOAD ->
                    player.sendMessage("Resource pack failed to download for some reason. (invalid URL?)");
            case DECLINED -> player.sendMessage("Resource pack declined by client.\n" +
                    "Make sure you have 'Server Resource Packs' set to 'Enabled' or 'Prompt' in the server list.");
            default -> player.sendMessage("Resource pack status: " + status.name());
        }
    }

}
