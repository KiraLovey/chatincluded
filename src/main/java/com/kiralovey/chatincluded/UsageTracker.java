package com.kiralovey.chatincluded;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracks per-session translation statistics in memory (no persistence).
 * Also polls the DeepL /v2/usage endpoint on startup and every 5 minutes.
 */
public class UsageTracker {

    public enum FilterReason {
        TOO_SHORT,
        EMOTE_ONLY,
        SAME_LANGUAGE,
        DEDUPLICATED,
        BOT_EXCLUDED
    }

    // Session counters
    private final AtomicInteger translationsSent     = new AtomicInteger(0);
    private final AtomicLong    charactersBilled     = new AtomicLong(0);
    private final AtomicInteger messagesFiltered     = new AtomicInteger(0);
    private final AtomicLong    charactersSaved      = new AtomicLong(0);

    // Per-reason filter counters
    private final AtomicInteger filteredTooShort     = new AtomicInteger(0);
    private final AtomicInteger filteredEmoteOnly    = new AtomicInteger(0);
    private final AtomicInteger filteredSameLanguage = new AtomicInteger(0);
    private final AtomicInteger filteredDeduplicated = new AtomicInteger(0);
    private final AtomicInteger filteredBotExcluded  = new AtomicInteger(0);

    // DeepL quota fields (from /v2/usage API)
    private volatile long deeplCharacterCount = -1;
    private volatile long deeplCharacterLimit = -1;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "chatincluded-usage-poller");
                t.setDaemon(true);
                return t;
            });

    private final AtomicBoolean pollingStarted = new AtomicBoolean(false);

    // ── Public recording methods ───────────────────────────────────────────────

    /**
     * Records a completed translation.
     *
     * @param billedChars characters DeepL actually billed
     * @param inputLength the original clean text length before translation
     */
    public void recordTranslation(int billedChars, int inputLength) {
        translationsSent.incrementAndGet();
        charactersBilled.addAndGet(billedChars);
        int saved = inputLength - billedChars;
        if (saved > 0) {
            charactersSaved.addAndGet(saved);
        }
    }

    /**
     * Records a filtered message with its reason.
     */
    public void recordFiltered(FilterReason reason) {
        messagesFiltered.incrementAndGet();
        switch (reason) {
            case TOO_SHORT:      filteredTooShort.incrementAndGet();     break;
            case EMOTE_ONLY:     filteredEmoteOnly.incrementAndGet();    break;
            case SAME_LANGUAGE:  filteredSameLanguage.incrementAndGet(); break;
            case DEDUPLICATED:   filteredDeduplicated.incrementAndGet(); break;
            case BOT_EXCLUDED:   filteredBotExcluded.incrementAndGet();  break;
        }
    }

    /**
     * Triggers an immediate async fetch of DeepL usage stats.
     */
    public void refreshUsage(PluginSettings settings) {
        scheduler.submit(() -> fetchUsage(settings));
    }

    /**
     * Starts the 5-minute polling cycle. Safe to call multiple times — only
     * starts once.
     */
    public void startPolling(PluginSettings settings) {
        if (pollingStarted.compareAndSet(false, true)) {
            // Fetch immediately on startup
            scheduler.submit(() -> fetchUsage(settings));
            // Then every 5 minutes
            scheduler.scheduleAtFixedRate(
                    () -> fetchUsage(settings),
                    5, 5, TimeUnit.MINUTES);
        }
    }

    /**
     * Shuts down the background scheduler.
     */
    public void shutdown() {
        scheduler.shutdownNow();
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public int  getTranslationsSent()      { return translationsSent.get(); }
    public long getCharactersBilled()      { return charactersBilled.get(); }
    public int  getMessagesFiltered()      { return messagesFiltered.get(); }
    public long getCharactersSaved()       { return charactersSaved.get(); }
    public int  getFilteredTooShort()      { return filteredTooShort.get(); }
    public int  getFilteredEmoteOnly()     { return filteredEmoteOnly.get(); }
    public int  getFilteredSameLanguage()  { return filteredSameLanguage.get(); }
    public int  getFilteredDeduplicated()  { return filteredDeduplicated.get(); }
    public int  getFilteredBotExcluded()   { return filteredBotExcluded.get(); }
    public long getDeeplCharacterCount()   { return deeplCharacterCount; }
    public long getDeeplCharacterLimit()   { return deeplCharacterLimit; }

    // ── Private HTTP logic ────────────────────────────────────────────────────

    private void fetchUsage(PluginSettings settings) {
        if (settings == null || settings.deeplApiKey == null || settings.deeplApiKey.isBlank()) {
            return;
        }

        try {
            String host = "pro".equalsIgnoreCase(settings.deeplPlan)
                    ? "api.deepl.com"
                    : "api-free.deepl.com";

            URL url = new URL("https://" + host + "/v2/usage");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "DeepL-Auth-Key " + settings.deeplApiKey);
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(10_000);

            int status = conn.getResponseCode();
            if (status != 200) {
                return; // Fail silently
            }

            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            JsonObject json = JsonParser.parseString(sb.toString()).getAsJsonObject();
            if (json.has("character_count")) {
                deeplCharacterCount = json.get("character_count").getAsLong();
            }
            if (json.has("character_limit")) {
                deeplCharacterLimit = json.get("character_limit").getAsLong();
            }

        } catch (Exception e) {
            // Fail silently — never throw or block
        }
    }
}
