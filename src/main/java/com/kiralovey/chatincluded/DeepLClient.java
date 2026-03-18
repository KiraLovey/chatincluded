package com.kiralovey.chatincluded;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Thin async wrapper around the DeepL v2 /translate endpoint.
 *
 * Imports use plain com.google.gson – Maven Shade relocates these to
 * com.kiralovey.chatincluded.shaded.gson at package time, after compilation.
 */
public class DeepLClient {

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final PluginSettings settings;

    public DeepLClient(PluginSettings settings) {
        this.settings = settings;
    }

    /**
     * Translate {@code text} to {@code targetLang} asynchronously.
     *
     * @return a TranslationResult, or throws on failure.
     */
    public CompletableFuture<TranslationResult> translate(String text, String targetLang) {
        JsonObject body = new JsonObject();
        JsonArray texts = new JsonArray();
        texts.add(text);
        body.add("text", texts);
        body.addProperty("target_lang", targetLang);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(settings.getDeeplEndpoint()))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json")
                .header("Authorization", "DeepL-Auth-Key " + settings.deeplApiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        return HTTP.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        throw new RuntimeException(
                                "DeepL API error: HTTP " + response.statusCode()
                                        + " – " + response.body());
                    }
                    return parseResponse(response.body());
                });
    }

    private TranslationResult parseResponse(String responseBody) {
        JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();
        JsonArray translations = root.getAsJsonArray("translations");

        if (translations == null || translations.size() == 0) {
            throw new RuntimeException("DeepL returned an empty translations array.");
        }

        JsonObject first = translations.get(0).getAsJsonObject();
        String translatedText     = first.get("text").getAsString();
        String detectedSourceLang = first.has("detected_source_language")
                ? first.get("detected_source_language").getAsString()
                : "UNKNOWN";

        return new TranslationResult(translatedText, detectedSourceLang);
    }

    // Result record

    public static final class TranslationResult {
        public final String translatedText;
        public final String detectedSourceLanguage;

        public TranslationResult(String translatedText, String detectedSourceLanguage) {
            this.translatedText = translatedText;
            this.detectedSourceLanguage = detectedSourceLanguage.toUpperCase();
        }
    }
}
