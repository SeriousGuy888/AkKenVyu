package io.github.seriousguy888.akkenvyu;

import com.google.gson.*;
import io.github.seriousguy888.akkenvyu.config.CachedDataConfig;
import io.github.seriousguy888.akkenvyu.data.PlayerDataManager;
import org.bukkit.Bukkit;
import io.github.seriousguy888.akkenvyu.utils.HexStringToByteArray;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class GithubFetcher {

    private final AkKenVyu plugin;
    private final URI apiUri;
    private final String desiredFileName;

    public GithubFetcher(
            @Nonnull AkKenVyu plugin,
            @Nonnull String githubRepo,
            @Nonnull String desiredFileName) throws URISyntaxException {
        this.plugin = plugin;
        this.apiUri = new URI("https://api.github.com/repos/" + githubRepo + "/releases/latest");
        this.desiredFileName = desiredFileName;


        // Periodically poll the GitHub repo for new updates to the resource pack.
        int intervalMinutes = plugin.getMainConfig().getGithubPollingIntervalMinutes();
        long intervalTicks = intervalMinutes * 60 * 20L;
        // If the config option is set to 0, only run it once; otherwise, run it once per this interval.
        if (intervalTicks == 0) {
            plugin.getLogger().info(
                    "Periodic polling is disabled. Only polling for resource pack updates once at startup.");

            pollForUpdatesAsync();
        } else {
            plugin.getLogger().info("AkKenVyu will poll " + githubRepo
                    + " for resource pack updates every " + intervalMinutes + " minutes.");

            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::pollForUpdates, 0, intervalTicks);
        }
    }

    public void pollForUpdatesAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::pollForUpdates);
    }

    private void pollForUpdates() {
        plugin.getLogger().info("Attempting to fetch latest resource pack from " + apiUri);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(apiUri)
                .timeout(Duration.of(10, ChronoUnit.SECONDS))
                .GET()
                .build();

        HttpResponse<String> response;
        try {
            response = HttpClient
                    .newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            plugin.getLogger().severe("Failed to fetch latest release from GitHub.\n" + e);
            return;
        }

        if (response.statusCode() != 200) {
            plugin.getLogger().severe("Response gave " + response.statusCode() + " status code. Quitting.");
            return;
        }

        Gson gson = new Gson();
        JsonObject releaseJsonObject;
        try {
            releaseJsonObject = gson.fromJson(response.body(), JsonObject.class);
        } catch (JsonSyntaxException e) {
            plugin.getLogger().severe("Response gave malformed JSON. Quitting.\n" + e);
            return;
        }

        JsonArray assetsArray = releaseJsonObject.getAsJsonArray("assets");
        if (assetsArray.isEmpty()) {
            plugin.getLogger().severe("Latest release on GitHub has no assets attached.");
            return;
        }

        CachedDataConfig cachedDataConfig = plugin.getCachedDataConfig();

        for (JsonElement asset : assetsArray) {
            String fileName = asset.getAsJsonObject().get("name").getAsString();

            if (fileName.equals(desiredFileName)) {
                String newDownloadUrl = asset.getAsJsonObject().get("browser_download_url").getAsString();

                if (newDownloadUrl.equalsIgnoreCase(cachedDataConfig.getDownloadUrl())) {
                    plugin.getLogger().info("Found latest release on GitHub,"
                            + " but the download URL is the same as the current one.");
                    return;
                }

                cachedDataConfig.setDownloadUrl(newDownloadUrl);
                cachedDataConfig.setHash(null); // Reset hash (in case the hash is from a previous release)

                plugin.getLogger().info(
                        "Successfully found matching resource pack file. Download URL: " + newDownloadUrl);

                byte[] hash = null;
                String hashString = null;
                String body = releaseJsonObject.get("body").getAsString();
                if (body != null) {
                    hashString = body.split("\\s")[0];

                    // convert hex string to byte array
                    // if it doesn't work, just ignore it and don't bother with the hash
                    try {
                        hash = HexStringToByteArray.convert(hashString);
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Error while converting resource pack hash: " + e);
                    }
                }

                if (hash == null || hash.length != 20) {
                    plugin.getLogger().warning("Invalid SHA1 hash ("
                            + hashString + ") was provided in release. Ignoring."
                            + " Players can still download the resource pack if the URL is working,"
                            + " but without the hash, they will have to redownload it each time"
                            + " they join the server, even if no changes have been made.");
                } else {
                    cachedDataConfig.setHash(hashString);
                    plugin.getLogger().info("Using SHA1 hash " + hashString + " for resource pack.");
                }

                announceUpdatedPack();

                return;
            }
        }

        plugin.getLogger().severe(
                "GitHub release contained no file with a name that matches `" + desiredFileName + "`");
    }

    private void announceUpdatedPack() {
        PlayerDataManager pdm = plugin.getPlayerDataManager();
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (pdm.getPlayerData(player).isResourcePackEnabled()) {
                player.sendMessage(ChatColor.GREEN + "\n---\nA new version of the server resource pack is available."
                        + " Relog or use '/rp enable' to update.\n---\n");
            }
        });
    }
}
