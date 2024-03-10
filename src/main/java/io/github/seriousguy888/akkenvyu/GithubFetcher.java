package io.github.seriousguy888.akkenvyu;

import com.google.gson.*;
import org.bukkit.Bukkit;

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

    private String downloadUrl;

    public GithubFetcher(
            @Nonnull AkKenVyu plugin,
            @Nonnull String githubRepo,
            @Nonnull String desiredFileName) throws URISyntaxException {
        this.plugin = plugin;
        this.apiUri = new URI("https://api.github.com/repos/" + githubRepo + "/releases/latest");
        this.desiredFileName = desiredFileName;
    }

    public void updateDownloadUrl() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
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
                    plugin.getLogger().info(
                            "Successfully found matching resource pack file. Download URL: " + downloadUrl);
                    return;
                }
            }

            plugin.getLogger().severe(
                    "GitHub release contained no file with a name that matches `" + desiredFileName + "`");
        });
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
}
