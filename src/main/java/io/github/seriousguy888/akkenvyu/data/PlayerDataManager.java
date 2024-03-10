package io.github.seriousguy888.akkenvyu.data;

import io.github.seriousguy888.akkenvyu.AkKenVyu;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PlayerDataManager {
    private final AkKenVyu plugin;
    private final Map<Player, PlayerData> dataMap;
    private final File playerDataFolder;

    public PlayerDataManager(AkKenVyu plugin) {
        this.plugin = plugin;

        dataMap = new HashMap<>();

        playerDataFolder = new File(plugin.getDataFolder().getAbsolutePath(), "playerdata");
        if (!playerDataFolder.exists()) {
            boolean ignored = playerDataFolder.mkdirs();
        }

        // Just in case the plugin is reloaded while players are online,
        // load the data of all players currently online.
        for (Player player : Bukkit.getOnlinePlayers()) {
            getPlayerData(player);
        }
    }

    public PlayerData getPlayerData(Player player) {
        if (!dataMap.containsKey(player)) {
            dataMap.put(player, loadFromDisk(player));
        }

        return dataMap.get(player);
    }

    // Called when a player logs out
    public void removePlayerData(Player player) {
        saveToDisk(player);
        dataMap.remove(player);
    }


    private PlayerData loadFromDisk(Player player) {
        File file = new File(playerDataFolder, player.getUniqueId() + ".yml");
        YamlConfiguration serialisedYaml = YamlConfiguration.loadConfiguration(file);

        return new PlayerData(file, serialisedYaml);
    }

    private void saveToDisk(Player player) {
        PlayerData playerData = getPlayerData(player);

        try {
            playerData.serialise().save(playerData.getFile());
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save player data of " + player + "!\n" + e);
        }
    }
}
