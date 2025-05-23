package io.github.seriousguy888.akkenvyu;

import io.github.seriousguy888.akkenvyu.commands.MainCommand;
import io.github.seriousguy888.akkenvyu.config.CachedDataConfig;
import io.github.seriousguy888.akkenvyu.config.MainConfig;
import io.github.seriousguy888.akkenvyu.data.PlayerDataManager;
import io.github.seriousguy888.akkenvyu.listeners.JoinAndQuitListener;
import io.github.seriousguy888.akkenvyu.listeners.ResourcePackStatusListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.UUID;

public final class AkKenVyu extends JavaPlugin {
    private MainConfig mainConfig;
    private CachedDataConfig cachedDataConfig;
    private PlayerDataManager playerDataManager;

    private GithubFetcher githubFetcher;


    @Override
    public void onEnable() {
        try {
            mainConfig = new MainConfig(this, "config", true);
            cachedDataConfig = new CachedDataConfig(this, "cache", true);
        } catch (FileNotFoundException e) {
            getLogger().severe(e.toString());
            getLogger().severe("Failed to initialise config file. Cannot continue; disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        playerDataManager = new PlayerDataManager(this);

        try {
            githubFetcher = new GithubFetcher(this,
                    mainConfig.getGithubRepoName(),
                    mainConfig.getGithubFileName());
        } catch (URISyntaxException e) {
            getLogger().severe(e.toString());
            getLogger().severe("Malformed URL. Cannot continue; disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }


        Objects.requireNonNull(getCommand("resourcepack"))
                .setExecutor(new MainCommand(this));

        getServer().getPluginManager().registerEvents(new ResourcePackStatusListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinAndQuitListener(this), this);
    }

    @Override
    public void onDisable() {
        cachedDataConfig.saveToDisk();
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public CachedDataConfig getCachedDataConfig() {
        return cachedDataConfig;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public GithubFetcher getGithubFetcher() {
        return githubFetcher;
    }
}
