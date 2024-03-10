package io.github.seriousguy888.akkenvyu;

import io.github.seriousguy888.akkenvyu.commands.GetResourcePackCommand;
import io.github.seriousguy888.akkenvyu.config.MainConfig;
import io.github.seriousguy888.akkenvyu.listeners.ResourcePackStatusListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.UUID;

public final class AkKenVyu extends JavaPlugin {
    // resource pack ids haven't been added yet in 1.20.2
//    private final UUID resourcePackId = UUID.randomUUID();

    private MainConfig mainConfig;


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

        Objects.requireNonNull(getCommand("getresourcepack"))
                .setExecutor(new GetResourcePackCommand(this));

        getServer().getPluginManager().registerEvents(new ResourcePackStatusListener(this), this);

        getServer().broadcastMessage("Repo: " + mainConfig.getGithubRepoName());
        getServer().broadcastMessage("Looking for a file named: " + mainConfig.getGithubFileName());
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }
}
