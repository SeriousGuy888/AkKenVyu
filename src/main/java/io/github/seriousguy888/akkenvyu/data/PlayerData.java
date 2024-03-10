package io.github.seriousguy888.akkenvyu.data;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class PlayerData {
    private final File file;

    /**
     * Whether this player wants to use the resource pack.
     * If so, the resource pack should be automatically sent to the player when they join the server.
     * <p>
     * It shouldn't, however, be sent automatically while they are in game and in the world.
     * If the resource pack is updated while a player is in game, the player should be prompted that
     * a new version is available, but they have to run a command or relog to get the new version.
     */
    private boolean resourcePackEnabled = false;

    public PlayerData(File file) {
        this.file = file;
    }

    public PlayerData(File file, YamlConfiguration serialised) {
        this.file = file;
        this.resourcePackEnabled = serialised.getBoolean("resource_pack_enabled", false);
    }

    public YamlConfiguration serialise() {
        YamlConfiguration serialised = new YamlConfiguration();
        serialised.set("resource_pack_enabled", resourcePackEnabled);
        return serialised;
    }

    public File getFile() {
        return file;
    }

    public boolean isResourcePackEnabled() {
        return resourcePackEnabled;
    }

    public void setResourcePackEnabled(boolean resourcePackEnabled) {
        this.resourcePackEnabled = resourcePackEnabled;
    }
}
