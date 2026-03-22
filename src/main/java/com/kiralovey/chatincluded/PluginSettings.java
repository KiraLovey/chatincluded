package com.kiralovey.chatincluded;

import java.util.HashSet;
import java.util.Set;

/**
 * Holds all user-configurable settings for ChatIncluded.
 */
public class PluginSettings {

    // Master toggle
    public boolean enabled = true;

    // DeepL API
    public String deeplApiKey = "";
    public String deeplPlan   = "free";

    // Language
    public String targetLanguage = "EN";

    // Performance
    public int cooldownMs                 = 500;
    public int burstLimit                 = 5;
    public int deduplicationWindowSeconds = 10;
    public int minimumMessageLength       = 5;

    // Two-way conversation
    public boolean twoWayEnabled             = true;
    public int     conversationMemoryMinutes = 30;

    // Attribution
    public boolean showAttribution = true;

    // Commands
    public String chatIncludedMessage = "ChatIncluded automatically translates multilingual chat "
            + "in real time across Twitch, Kick, YouTube, and Trovo! Works for multi-streamers. "
            + "Commands & language codes: https://chatincluded.live";
    public String speakAccessLevel    = "streamer";

    // Bot exclusion list (comma-separated usernames)
    public String excludedUsernames = "fossabot, streamelements, nightbot, botrixoficial, sery_bot";

    // Platform toggles
    public boolean twitchEnabled  = true;
    public boolean kickEnabled    = true;
    public boolean youtubeEnabled = true;
    public boolean trovoEnabled   = true;

    public String getDeeplEndpoint() {
        if ("pro".equalsIgnoreCase(deeplPlan)) {
            return "https://api.deepl.com/v2/translate";
        }
        return "https://api-free.deepl.com/v2/translate";
    }

    public String getDeeplLanguagesEndpoint() {
        if ("pro".equalsIgnoreCase(deeplPlan)) {
            return "https://api.deepl.com/v2/languages?type=target";
        }
        return "https://api-free.deepl.com/v2/languages?type=target";
    }

    public Set<String> getExcludedUsernamesSet() {
        Set<String> set = new HashSet<>();
        if (excludedUsernames == null || excludedUsernames.isBlank()) return set;
        for (String name : excludedUsernames.split(",")) {
            String trimmed = name.trim().toLowerCase();
            if (!trimmed.isEmpty()) set.add(trimmed);
        }
        return set;
    }
}
