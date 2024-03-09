package io.github.seriousguy888.akkenvyu;

import io.github.seriousguy888.akkenvyu.commands.GetResourcePackCommand;
import io.github.seriousguy888.akkenvyu.listeners.ResourcePackStatusListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.UUID;

public final class AkKenVyu extends JavaPlugin {
    // resource pack ids haven't been added yet in 1.20.2
//    private final UUID resourcePackId = UUID.randomUUID();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Objects.requireNonNull(getCommand("getresourcepack"))
                .setExecutor(new GetResourcePackCommand(this));

        getServer().getPluginManager().registerEvents(new ResourcePackStatusListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
