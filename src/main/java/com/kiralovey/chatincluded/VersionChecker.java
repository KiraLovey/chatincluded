package com.kiralovey.chatincluded;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Checks GitHub Releases for a newer version of ChatIncluded on startup.
 * Logs a friendly message if an update is available.
 * Never blocks startup -- runs asynchronously and fails silently.
 */
public class VersionChecker {

    public static final String CURRENT_VERSION = "1.0.0";

    private static final String RELEASES_API =
            "https://api.github.com/repos/KiraLovey/chatincluded/releases/latest";

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public static void checkAsync(ChatIncludedPlugin plugin) {
        Thread thread = new Thread(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(RELEASES_API))
                        .timeout(Duration.ofSeconds(8))
                        .header("Accept", "application/vnd.github+json")
                        .header("User-Agent", "ChatIncluded/" + CURRENT_VERSION)
                        .GET()
                        .build();

                HttpResponse<String> response = HTTP.send(
                        request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) return;

                JsonObject json = JsonParser.parseString(response.body())
                        .getAsJsonObject();

                if (!json.has("tag_name")) return;

                String latestTag = json.get("tag_name").getAsString()
                        .replaceFirst("^v", "").trim();

                if (!latestTag.equals(CURRENT_VERSION)) {
                    plugin.getLogger().info("========================================");
                    plugin.getLogger().info(" ChatIncluded update available!");
                    plugin.getLogger().info(" Current version : " + CURRENT_VERSION);
                    plugin.getLogger().info(" Latest version  : " + latestTag);
                    plugin.getLogger().info(" Update guide    : https://chatincluded.live");
                    plugin.getLogger().info("========================================");
                } else {
                    plugin.getLogger().info(
                            "ChatIncluded is up to date (v" + CURRENT_VERSION + ").");
                }

            } catch (Exception e) {
                plugin.getLogger().debug(
                        "ChatIncluded version check failed (non-critical): "
                        + e.getMessage());
            }
        }, "chatincluded-version-check");

        thread.setDaemon(true);
        thread.start();
    }
}
