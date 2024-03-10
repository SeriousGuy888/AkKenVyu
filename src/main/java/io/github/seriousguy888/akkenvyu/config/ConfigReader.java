package io.github.seriousguy888.akkenvyu.config;
import io.github.seriousguy888.akkenvyu.AkKenVyu;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.logging.Level;

public abstract class ConfigReader {
    protected final AkKenVyu plugin;

    private final String name;
    private final File file;
    private final boolean mustRetainComments;
    protected FileConfiguration config;

    public ConfigReader(AkKenVyu plugin, String name, boolean mustRetainComments)
            throws FileNotFoundException {
        this.plugin = plugin;
        this.name = name;
        this.file = new File(plugin.getDataFolder(), name + ".yml");
        this.mustRetainComments = mustRetainComments;

        loadFromDisk();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void loadFromDisk() throws FileNotFoundException {
        InputStream defaultConfigStream = plugin.getResource(file.getName());

        if (!file.exists()) {
            file.getParentFile().mkdirs();

            if (defaultConfigStream != null) {
                // Copy the file from the jar file into the plugin's folder on the server if it doesn't exist
                plugin.saveResource(file.getName(), false);
            } else {
                plugin.getLogger().severe("Resource `" + file.getName() + "` does not exist. " +
                        "Cannot initialise custom config.");
                throw new FileNotFoundException("Expected resource " + file.getName() + " is missing from the jar.");
            }
        }

        config = YamlConfiguration.loadConfiguration(file);

        if (defaultConfigStream != null) {
            // Write any keys present in the default config (the one included in the jar file)
            // but absent in the one currently on disk in the plugin folder.

            // Grab the default config included inside the jar file
            var defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream));

            // Copy default key values and comments to the keys that aren't set
            defaultConfig
                    .getKeys(true)
                    .stream()
                    .filter(key -> !config.contains(key))
                    .forEach(key -> config.set(key, defaultConfig.get(key)));

            if (mustRetainComments) {
                // Keep the comments at the top up to date with those included in the corresponding file in the jar
                config.options().setHeader(defaultConfig.options().getHeader());

                // Copy all the other comments as well from the default config
                config
                        .getKeys(true)
                        .forEach(key -> {
                            config.setComments(key, defaultConfig.getComments(key));
                            config.setInlineComments(key, defaultConfig.getInlineComments(key));
                        });
            }

            saveToDisk();
        }
    }

    /**
     * Should be called when changes have been made to the rules from in game.
     * Will overwrite what is on disk.
     */
    public void saveToDisk() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Unable to save config file `" + file.getName() + "`!", e);
        }
    }

    protected boolean saveBackupCopy() {
        File backupFile = new File(plugin.getDataFolder(), name + "-" + System.currentTimeMillis() + ".yml.old");

        try {
            config.save(backupFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Tried and failed to back up `" + file.getName() + "` to `" +
                    backupFile.getName() + "`.\n" + e);
            return false;
        }

        plugin.getLogger().info("Successfully backed up `" + file.getName() + "` to `" +
                backupFile.getName() + "`.");
        return true;
    }
}