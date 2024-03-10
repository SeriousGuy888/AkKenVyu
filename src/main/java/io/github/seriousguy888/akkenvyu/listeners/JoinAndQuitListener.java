package io.github.seriousguy888.akkenvyu.listeners;

import io.github.seriousguy888.akkenvyu.AkKenVyu;
import io.github.seriousguy888.akkenvyu.data.PlayerData;
import io.github.seriousguy888.akkenvyu.utils.ResourcePackSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinAndQuitListener implements Listener {

    private final AkKenVyu plugin;

    public JoinAndQuitListener(AkKenVyu plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);

        if (playerData.isResourcePackEnabled()) {
            ResourcePackSender.sendResourcePack(plugin, player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getPlayerDataManager().removePlayerData(event.getPlayer());
    }
}
