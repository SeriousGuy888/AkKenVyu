package io.github.seriousguy888.akkenvyu;

import com.google.gson.*;
import org.bukkit.Bukkit;
import io.github.seriousguy888.akkenvyu.utils.HexStringToByteArray;

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

    private String downloadUrl = null;
    private byte[] sha1Hash = null;

    public GithubFetcher(
            @Nonnull AkKenVyu plugin,
            @Nonnull String githubRepo,
            @Nonnull String desiredFileName) throws URISyntaxException {
        this.plugin = plugin;
        this.apiUri = new URI("https://api.github.com/repos/" + githubRepo + "/releases/latest");
        this.desiredFileName = desiredFileName;


        // Periodically poll the GitHub repo for new updates to the resource pack.
        long intervalTicks = plugin.getMainConfig().getGithubPollingIntervalMinutes() * 60 * 20L;
        // If the config option is set to 0, only run it once; otherwise, run it once per this interval.
        if (intervalTicks == 0) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, this::pollForUpdates);
        } else {
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::pollForUpdates, 0, intervalTicks);
        }
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

        for (JsonElement asset : assetsArray) {
            String fileName = asset.getAsJsonObject().get("name").getAsString();

            if (fileName.equals(desiredFileName)) {
                downloadUrl = asset.getAsJsonObject().get("browser_download_url").getAsString();
                sha1Hash = null; // Reset hash (in case the hash is from a previous release)

                plugin.getLogger().info(
                        "Successfully found matching resource pack file. Download URL: " + downloadUrl);

                byte[] hash = null;
                String hashHex = null;
                String body = releaseJsonObject.get("body").getAsString();
                if (body != null) {
                    hashHex = body.split("\\s")[0];

                    // convert hex string to byte array
                    // if it doesn't work, just ignore it and don't bother with the hash
                    try {
                        hash = HexStringToByteArray.convert(hashHex);
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Error while converting resource pack hash: " + e);
                    }
                }

                if (hash == null || hash.length != 20) {
                    plugin.getLogger().warning("Invalid SHA1 hash ("
                            + hashHex + ") was provided in release. Ignoring."
                            + " Players can still download the resource pack if the URL is working,"
                            + " but without the hash, they will have to redownload it each time"
                            + " they join the server, even if no changes have been made.");
                } else {
                    sha1Hash = hash;
                    plugin.getLogger().info("Using SHA1 hash " + hashHex + " for resource pack.");
                }

                return;
            }
        }

        plugin.getLogger().severe(
                "GitHub release contained no file with a name that matches `" + desiredFileName + "`");
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public byte[] getSha1Hash() {
        return sha1Hash;
    }

}
