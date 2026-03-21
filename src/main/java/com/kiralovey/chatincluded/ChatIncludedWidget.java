package com.kiralovey.chatincluded;

import co.casterlabs.caffeinated.pluginsdk.widgets.Widget;
import co.casterlabs.caffeinated.pluginsdk.widgets.WidgetInstanceMode;
import co.casterlabs.caffeinated.pluginsdk.widgets.WidgetSettings;
import co.casterlabs.caffeinated.pluginsdk.widgets.settings.WidgetSettingsItem;
import co.casterlabs.caffeinated.pluginsdk.widgets.settings.WidgetSettingsLayout;
import co.casterlabs.caffeinated.pluginsdk.widgets.settings.WidgetSettingsSection;

import java.text.NumberFormat;
import java.util.Locale;

public class ChatIncludedWidget extends Widget {

    @Override
    public void onInit() {
        this.setSettingsLayout(buildFullLayout(null, null));
    }

    @Override
    public void onNameUpdate() {}

    @Override
    public String getWidgetBasePath(WidgetInstanceMode mode) {
        return null;
    }

    // ── Settings layout construction ──────────────────────────────────────────

    private WidgetSettingsLayout buildFullLayout(UsageTracker tracker, PluginSettings settings) {
        // General
        WidgetSettingsSection general = new WidgetSettingsSection("general", "General")
                .addItem(WidgetSettingsItem.asCheckbox("enabled",
                        "Enable ChatIncluded", true))
                .addItem(WidgetSettingsItem.asCheckbox("showAttribution",
                        "Show sender username in translations (e.g. [ES->EN] @username: message)", true));

        // DeepL API
        WidgetSettingsSection api = new WidgetSettingsSection("api", "DeepL API")
                .addItem(WidgetSettingsItem.asPassword("deeplApiKey", "DeepL API Key", "", ""))
                .addItem(WidgetSettingsItem.asDropdown("deeplPlan", "API Plan", "free", "free", "pro"));

        // Language
        WidgetSettingsSection lang = new WidgetSettingsSection("language", "Language")
                .addItem(WidgetSettingsItem.asText("targetLanguage",
                        "Target Language Code (e.g. EN, ES, FR)", "EN", "EN"));

        // Performance
        WidgetSettingsSection perf = new WidgetSettingsSection("performance", "Performance")
                .addItem(WidgetSettingsItem.asNumber("cooldownMs",
                        "Translation Cooldown (ms)", 500, 100, 0, 10000))
                .addItem(WidgetSettingsItem.asNumber("burstLimit",
                        "Burst Limit (per cooldown window)", 5, 1, 1, 50))
                .addItem(WidgetSettingsItem.asNumber("deduplicationWindowSeconds",
                        "Deduplication Window (seconds)", 10, 1, 1, 300))
                .addItem(WidgetSettingsItem.asNumber("minimumMessageLength",
                        "Minimum message length to translate (characters)", 5, 1, 1, 50));

        // Two-way conversation
        WidgetSettingsSection twoWay = new WidgetSettingsSection("twoway", "Two-Way Conversation")
                .addItem(WidgetSettingsItem.asCheckbox("twoWayEnabled",
                        "Enable Two-Way Translation (translate streamer @mentions back to viewer language)", true))
                .addItem(WidgetSettingsItem.asNumber("conversationMemoryMinutes",
                        "Remember viewer language for (minutes)", 30, 5, 5, 120));

        // Commands
        WidgetSettingsSection commands = new WidgetSettingsSection("commands", "Commands")
                .addItem(WidgetSettingsItem.asTextArea("chatIncludedMessage",
                        "!chatincluded response message",
                        "ChatIncluded automatically translates multilingual chat in real time across Twitch, Kick, YouTube, and Trovo! Works for multi-streamers. Commands & language codes: https://chatincluded.live",
                        "ChatIncluded automatically translates multilingual chat in real time across Twitch, Kick, YouTube, and Trovo! Works for multi-streamers. Commands & language codes: https://chatincluded.live"))
                .addItem(WidgetSettingsItem.asDropdown("speakAccessLevel",
                        "!speak command access",
                        "streamer",
                        "streamer", "mod", "sub", "everyone"));

        // Bot exclusions
        WidgetSettingsSection exclusions = new WidgetSettingsSection("exclusions", "Bot Exclusions")
                .addItem(WidgetSettingsItem.asTextArea("excludedUsernames",
                        "Excluded usernames (comma-separated, case-insensitive)",
                        "fossabot, streamelements, nightbot, botrixoficial, sery_bot",
                        "fossabot, streamelements, nightbot, botrixoficial, sery_bot"));

        // Platforms
        WidgetSettingsSection platforms = new WidgetSettingsSection("platforms", "Platforms")
                .addItem(WidgetSettingsItem.asCheckbox("twitchEnabled",  "Enable on Twitch",  true))
                .addItem(WidgetSettingsItem.asCheckbox("kickEnabled",    "Enable on Kick",    true))
                .addItem(WidgetSettingsItem.asCheckbox("youtubeEnabled", "Enable on YouTube", true))
                .addItem(WidgetSettingsItem.asCheckbox("trovoEnabled",   "Enable on Trovo",   true));

        // Stats
        WidgetSettingsSection stats = buildStatsSection(tracker, settings);

        return new WidgetSettingsLayout()
                .addSection(general)
                .addSection(api)
                .addSection(lang)
                .addSection(perf)
                .addSection(twoWay)
                .addSection(commands)
                .addSection(exclusions)
                .addSection(platforms)
                .addSection(stats);
    }

    private WidgetSettingsSection buildStatsSection(UsageTracker tracker, PluginSettings settings) {
        WidgetSettingsSection stats = new WidgetSettingsSection("stats", "Stats");

        if (tracker == null || settings == null) {
            // No data yet — show placeholder values
            stats.addItem(WidgetSettingsItem.asText("statsTxSent",
                    "Translations sent (session)", "0", "0"));
            stats.addItem(WidgetSettingsItem.asText("statsCharsBilled",
                    "Characters billed (session)", "0", "0"));
            stats.addItem(WidgetSettingsItem.asText("statsMsgFiltered",
                    "Messages filtered (session)", "0", "0"));
            stats.addItem(WidgetSettingsItem.asText("statsCharsSaved",
                    "Characters saved (session)", "0", "0"));
            stats.addItem(WidgetSettingsItem.asText("statsFilterShort",
                    "\u21b3 Too short", "0", "0"));
            stats.addItem(WidgetSettingsItem.asText("statsFilterEmote",
                    "\u21b3 Emote only", "0", "0"));
            stats.addItem(WidgetSettingsItem.asText("statsFilterSameLang",
                    "\u21b3 Same language", "0", "0"));
            stats.addItem(WidgetSettingsItem.asText("statsFilterDedup",
                    "\u21b3 Deduplicated", "0", "0"));
            return stats;
        }

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);

        boolean isPro = "pro".equalsIgnoreCase(settings.deeplPlan);
        long charCount  = tracker.getDeeplCharacterCount();
        long charLimit  = tracker.getDeeplCharacterLimit();
        long billed     = tracker.getCharactersBilled();
        long saved      = tracker.getCharactersSaved();
        int  txSent     = tracker.getTranslationsSent();
        int  filtered   = tracker.getMessagesFiltered();
        int  fShort     = tracker.getFilteredTooShort();
        int  fEmote     = tracker.getFilteredEmoteOnly();
        int  fSameLang  = tracker.getFilteredSameLanguage();
        int  fDedup     = tracker.getFilteredDeduplicated();

        if (isPro) {
            // Pro plan — show cost estimates
            String charsUsedVal = charCount >= 0 ? nf.format(charCount) : "—";
            stats.addItem(WidgetSettingsItem.asText("statsCharsUsed",
                    "Characters translated (this period)", charsUsedVal, charsUsedVal));

            double periodCost = charCount >= 0 ? charCount * 0.000025 : 0.0;
            String estCostVal = charCount >= 0
                    ? String.format("$%.4f", periodCost)
                    : "—";
            stats.addItem(WidgetSettingsItem.asText("statsEstCost",
                    "Estimated cost (this period)", estCostVal, estCostVal));

            String txSentVal = nf.format(txSent);
            stats.addItem(WidgetSettingsItem.asText("statsTxSent",
                    "Translations sent (session)", txSentVal, txSentVal));

            String billedVal = nf.format(billed);
            stats.addItem(WidgetSettingsItem.asText("statsCharsBilled",
                    "Characters billed (session)", billedVal, billedVal));

            double sessionCost = billed * 0.000025;
            String sessionCostVal = String.format("$%.4f", sessionCost);
            stats.addItem(WidgetSettingsItem.asText("statsEstSessionCost",
                    "Estimated session cost", sessionCostVal, sessionCostVal));

            String savedVal = nf.format(saved);
            stats.addItem(WidgetSettingsItem.asText("statsCharsSaved",
                    "Characters saved (session)", savedVal, savedVal));

            double savingsCost = saved * 0.000025;
            String savingsVal = String.format("$%.4f", savingsCost);
            stats.addItem(WidgetSettingsItem.asText("statsEstSavings",
                    "Estimated savings (session)", savingsVal, savingsVal));

        } else {
            // Free plan — show quota display
            String charsUsedVal;
            if (charCount >= 0 && charLimit > 0) {
                charsUsedVal = nf.format(charCount) + " / " + nf.format(charLimit);
            } else if (charCount >= 0) {
                charsUsedVal = nf.format(charCount);
            } else {
                charsUsedVal = "—";
            }
            stats.addItem(WidgetSettingsItem.asText("statsCharsUsed",
                    "Characters used (this period)", charsUsedVal, charsUsedVal));

            String remainingVal;
            String quotaPctVal;
            if (charCount >= 0 && charLimit > 0) {
                long remaining = charLimit - charCount;
                double pct = (double) charCount / charLimit * 100.0;
                double remainPct = 100.0 - pct;
                remainingVal = nf.format(remaining) + String.format(" (%.1f%%)", remainPct);
                quotaPctVal  = String.format("%.1f%%", pct);
            } else {
                remainingVal = "—";
                quotaPctVal  = "—";
            }
            stats.addItem(WidgetSettingsItem.asText("statsCharsRemaining",
                    "Remaining", remainingVal, remainingVal));
            stats.addItem(WidgetSettingsItem.asText("statsQuotaPct",
                    "Quota used", quotaPctVal, quotaPctVal));

            String txSentVal = nf.format(txSent);
            stats.addItem(WidgetSettingsItem.asText("statsTxSent",
                    "Translations sent (session)", txSentVal, txSentVal));

            String billedVal = nf.format(billed);
            stats.addItem(WidgetSettingsItem.asText("statsCharsBilled",
                    "Characters billed (session)", billedVal, billedVal));

            String filteredVal = nf.format(filtered);
            stats.addItem(WidgetSettingsItem.asText("statsMsgFiltered",
                    "Messages filtered (session)", filteredVal, filteredVal));

            String savedVal = nf.format(saved);
            stats.addItem(WidgetSettingsItem.asText("statsCharsSaved",
                    "Characters saved (session)", savedVal, savedVal));

            // Quota saved percentage (session)
            String quotaSavedVal;
            if (charLimit > 0 && saved > 0) {
                double pctSaved = (double) saved / charLimit * 100.0;
                quotaSavedVal = String.format("%.0f%%", pctSaved);
            } else {
                quotaSavedVal = "0%";
            }
            stats.addItem(WidgetSettingsItem.asText("statsQuotaSaved",
                    "Quota saved (session)", quotaSavedVal, quotaSavedVal));
        }

        // Filter breakdown — same for both plans
        String fShortVal   = nf.format(fShort);
        String fEmoteVal   = nf.format(fEmote);
        String fSameLangVal = nf.format(fSameLang);
        String fDedupVal   = nf.format(fDedup);

        stats.addItem(WidgetSettingsItem.asText("statsFilterShort",
                "\u21b3 Too short", fShortVal, fShortVal));
        stats.addItem(WidgetSettingsItem.asText("statsFilterEmote",
                "\u21b3 Emote only", fEmoteVal, fEmoteVal));
        stats.addItem(WidgetSettingsItem.asText("statsFilterSameLang",
                "\u21b3 Same language", fSameLangVal, fSameLangVal));
        stats.addItem(WidgetSettingsItem.asText("statsFilterDedup",
                "\u21b3 Deduplicated", fDedupVal, fDedupVal));

        return stats;
    }

    /**
     * Rebuilds the full settings layout with current tracker data baked in and
     * re-sets it so Caffeinated displays fresh values.
     */
    public void refreshStats() {
        try {
            PluginSettings settings = this.readSettings();
            // Retrieve the tracker from the plugin's chat listener
            ChatIncludedPlugin pluginInstance = null;
            // Walk up via the registered widget namespace to find the plugin
            // The tracker is accessed via plugin.getChatListener().getTracker()
            // We call this from ChatIncludedPlugin so it injects itself.
            // If trackerRef is available, use it; otherwise fall back gracefully.
            if (trackerRef != null && settings != null) {
                this.setSettingsLayout(buildFullLayout(trackerRef, settings));
            }
        } catch (Exception ignored) {}
    }

    /** Reference to the UsageTracker, set by ChatIncludedPlugin after wiring. */
    private UsageTracker trackerRef;

    public void setTrackerRef(UsageTracker tracker) {
        this.trackerRef = tracker;
    }

    // ── Settings reading ──────────────────────────────────────────────────────

    public PluginSettings readSettings() {
        WidgetSettings s = this.settings();

        PluginSettings ps = new PluginSettings();
        ps.enabled                    = s.getBoolean("general.enabled",                       true);
        ps.showAttribution            = s.getBoolean("general.showAttribution",               true);
        ps.deeplApiKey                = s.getString("api.deeplApiKey",                        "");
        ps.deeplPlan                  = s.getString("api.deeplPlan",                          "free");
        ps.targetLanguage             = s.getString("language.targetLanguage",                "EN").toUpperCase().strip();
        ps.cooldownMs                 = s.getNumber("performance.cooldownMs",                 500).intValue();
        ps.burstLimit                 = s.getNumber("performance.burstLimit",                 5).intValue();
        ps.deduplicationWindowSeconds = s.getNumber("performance.deduplicationWindowSeconds", 10).intValue();
        ps.minimumMessageLength       = s.getNumber("performance.minimumMessageLength",       5).intValue();
        ps.twoWayEnabled              = s.getBoolean("twoway.twoWayEnabled",                  true);
        ps.conversationMemoryMinutes  = s.getNumber("twoway.conversationMemoryMinutes",       30).intValue();
        ps.chatIncludedMessage        = s.getString("commands.chatIncludedMessage",
                "ChatIncluded automatically translates multilingual chat in real time! https://chatincluded.live");
        ps.speakAccessLevel           = s.getString("commands.speakAccessLevel",              "streamer");
        ps.excludedUsernames          = s.getString("exclusions.excludedUsernames",           "fossabot, streamelements, nightbot");
        ps.twitchEnabled              = s.getBoolean("platforms.twitchEnabled",               true);
        ps.kickEnabled                = s.getBoolean("platforms.kickEnabled",                 true);
        ps.youtubeEnabled             = s.getBoolean("platforms.youtubeEnabled",              true);
        ps.trovoEnabled               = s.getBoolean("platforms.trovoEnabled",                true);
        return ps;
    }
}
