package com.kiralovey.chatincluded;

import co.casterlabs.caffeinated.pluginsdk.widgets.Widget;
import co.casterlabs.caffeinated.pluginsdk.widgets.WidgetInstanceMode;
import co.casterlabs.caffeinated.pluginsdk.widgets.WidgetSettings;
import co.casterlabs.caffeinated.pluginsdk.widgets.settings.WidgetSettingsItem;
import co.casterlabs.caffeinated.pluginsdk.widgets.settings.WidgetSettingsLayout;
import co.casterlabs.caffeinated.pluginsdk.widgets.settings.WidgetSettingsSection;

public class ChatIncludedWidget extends Widget {

    @Override
    public void onInit() {
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
                        "ChatIncluded automatically translates multilingual chat in real time across Twitch, Kick, and YouTube! Works for multi-streamers. Commands & language codes: https://chatincluded.live",
                        "ChatIncluded automatically translates multilingual chat in real time across Twitch, Kick, and YouTube! Works for multi-streamers. Commands & language codes: https://chatincluded.live"))
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
                .addItem(WidgetSettingsItem.asCheckbox("youtubeEnabled", "Enable on YouTube", true));

        this.setSettingsLayout(new WidgetSettingsLayout()
                .addSection(general)
                .addSection(api)
                .addSection(lang)
                .addSection(perf)
                .addSection(twoWay)
                .addSection(commands)
                .addSection(exclusions)
                .addSection(platforms));
    }

    @Override
    public void onNameUpdate() {}

    @Override
    public String getWidgetBasePath(WidgetInstanceMode mode) {
        return null;
    }

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
        return ps;
    }
}
