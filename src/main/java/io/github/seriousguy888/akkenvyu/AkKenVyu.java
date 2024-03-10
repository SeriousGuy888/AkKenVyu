package io.github.seriousguy888.akkenvyu;

import io.github.seriousguy888.akkenvyu.commands.MainCommand;
import io.github.seriousguy888.akkenvyu.config.MainConfig;
import io.github.seriousguy888.akkenvyu.data.PlayerDataManager;
import io.github.seriousguy888.akkenvyu.listeners.JoinAndQuitListener;
import io.github.seriousguy888.akkenvyu.listeners.ResourcePackStatusListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Objects;

public final class AkKenVyu extends JavaPlugin {
    // resource pack ids haven't been added yet in 1.20.2
//    private final UUID resourcePackId = UUID.randomUUID();

    private MainConfig mainConfig;
    private PlayerDataManager playerDataManager;

    private GithubFetcher githubFetcher;


    @Override
    public void onEnable() {
        try {
            mainConfig = new MainConfig(this, "config", true);
        } catch (FileNotFoundException e) {
            getLogger().severe(e.toString());
            getLogger().severe("Failed to initialise config file. Cannot continue; disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        playerDataManager = new PlayerDataManager(this);

        try {
            githubFetcher = new GithubFetcher(this, mainConfig.getGithubRepoName(), mainConfig.getGithubFileName());
        } catch (URISyntaxException e) {
            getLogger().severe(e.toString());
            getLogger().severe("Malformed URL. Cannot continue; disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        githubFetcher.updateDownloadUrl();


        Objects.requireNonNull(getCommand("resourcepack"))
                .setExecutor(new MainCommand(this));

        getServer().getPluginManager().registerEvents(new ResourcePackStatusListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinAndQuitListener(this), this);
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public GithubFetcher getGithubFetcher() {
        return githubFetcher;
    }
}
