package com.kiralovey.chatincluded;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * DeepL supported target language codes.
 *
 * On first chat event, initAsync() fetches the authoritative list from the
 * DeepL /v2/languages endpoint and replaces activeCodes. Until then (and on
 * fetch failure), the hardcoded FALLBACK list is used so validation still works
 * with no network dependency at startup.
 */
public class SupportedLanguages {

    private static final Logger LOG = Logger.getLogger(SupportedLanguages.class.getName());

    /** Hardcoded fallback — used before the API responds or if the fetch fails. */
    private static final Set<String> FALLBACK = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "AR", "BG", "CS", "DA", "DE", "EL", "EN", "EN-GB", "EN-US",
            "ES", "ET", "FI", "FR", "HI", "HU", "ID", "IT", "JA", "KO",
            "LT", "LV", "NB", "NL", "PL", "PT", "PT-BR", "PT-PT",
            "RO", "RU", "SK", "SL", "SV", "TR", "UK", "ZH"
    )));

    private static volatile Set<String> activeCodes = FALLBACK;
    private static final AtomicBoolean  initialized = new AtomicBoolean(false);

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // Common mistakes and their corrections
    private static final String[][] SUGGESTIONS = {
            {"IN", "ID"},    // Indonesian
            {"CN", "ZH"},    // Chinese
            {"JP", "JA"},    // Japanese
            {"KR", "KO"},    // Korean
            {"BR", "PT-BR"}, // Brazilian Portuguese
            {"GR", "EL"},    // Greek
            {"CZ", "CS"},    // Czech
    };

    /**
     * Fetches the supported language list from the DeepL API asynchronously.
     * Safe to call on every message — runs only once per JVM session.
     * On failure, activeCodes remains the FALLBACK list.
     */
    public static void initAsync(PluginSettings settings) {
        if (!initialized.compareAndSet(false, true)) return;

        if (settings.deeplApiKey == null || settings.deeplApiKey.isBlank()) {
            LOG.warning("SupportedLanguages: no API key set — using fallback language list.");
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(settings.getDeeplLanguagesEndpoint()))
                .timeout(Duration.ofSeconds(10))
                .header("Authorization", "DeepL-Auth-Key " + settings.deeplApiKey)
                .GET()
                .build();

        HTTP.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() != 200) {
                        LOG.warning("SupportedLanguages: API returned HTTP "
                                + response.statusCode() + " — using fallback list.");
                        return;
                    }
                    try {
                        Set<String> fetched = new HashSet<>();
                        JsonArray array = JsonParser.parseString(response.body()).getAsJsonArray();
                        for (JsonElement el : array) {
                            String code = el.getAsJsonObject().get("language").getAsString().toUpperCase();
                            fetched.add(code);
                        }
                        if (fetched.isEmpty()) {
                            LOG.warning("SupportedLanguages: API returned empty list — using fallback.");
                            return;
                        }
                        activeCodes = Collections.unmodifiableSet(fetched);
                        LOG.info("SupportedLanguages: loaded " + fetched.size()
                                + " language codes from DeepL API.");
                    } catch (Exception e) {
                        LOG.warning("SupportedLanguages: parse error — using fallback list. " + e.getMessage());
                    }
                })
                .exceptionally(ex -> {
                    LOG.warning("SupportedLanguages: fetch failed — using fallback list. " + ex.getMessage());
                    return null;
                });
    }

    public static boolean isSupported(String code) {
        if (code == null) return false;
        return activeCodes.contains(code.toUpperCase());
    }

    /**
     * Returns a suggestion if the code is a common mistake, or null if no suggestion.
     */
    public static String getSuggestion(String code) {
        if (code == null) return null;
        String upper = code.toUpperCase();
        for (String[] pair : SUGGESTIONS) {
            if (pair[0].equals(upper)) return pair[1];
        }
        return null;
    }
}
