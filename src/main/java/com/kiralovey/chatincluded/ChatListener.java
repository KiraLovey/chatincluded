package com.kiralovey.chatincluded;

import co.casterlabs.caffeinated.pluginsdk.koi.Koi;
import co.casterlabs.koi.api.KoiChatterType;
import co.casterlabs.koi.api.listener.KoiEventHandler;
import co.casterlabs.koi.api.listener.KoiEventListener;
import co.casterlabs.koi.api.listener.KoiLifeCycleHandler;
import co.casterlabs.koi.api.types.events.RichMessageEvent;
import co.casterlabs.koi.api.types.events.rich.ChatFragment;
import co.casterlabs.koi.api.types.user.User;
import co.casterlabs.koi.api.types.user.UserPlatform;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatListener implements KoiLifeCycleHandler, KoiEventListener {

    private static final String PLATFORM_TWITCH  = "TWITCH";
    private static final String PLATFORM_KICK    = "KICK";
    private static final String PLATFORM_YOUTUBE = "YOUTUBE";
    private static final String PLATFORM_TROVO   = "TROVO";

    private static final Pattern MENTION_PATTERN = Pattern.compile("@([\\w]+)");

    // Matches our own translation output [XX->XX]
    private static final Pattern TRANSLATION_OUTPUT_PATTERN =
            Pattern.compile("\\[[A-Z]{2,5}->[A-Z]{2,5}\\]");

    // Matches our own command response messages to prevent re-translation
    private static final Pattern COMMAND_RESPONSE_PATTERN = Pattern.compile(
            "Your language has been set to|"
            + "The streamer's replies will be translated|"
            + "ChatIncluded language codes|"
            + "No viewers have set a language|"
            + "That message is already in|"
            + "No recent messages found|"
            + "Usage: !translate|"
            + "Usage: !speak|"
            + "is not a supported language code|"
            + "Did you mean|"
            + "Invalid language code|"
            + "Translation failed|"
            + "Type !languages"
    );

    private final ChatIncludedPlugin        plugin;
    private final Koi                       koi;
    private final TranslationTracker        tracker      = new TranslationTracker();
    private final ConversationMemory        memory       = new ConversationMemory();
    private final ViewerLanguagePreferences prefs        = new ViewerLanguagePreferences();
    private final RecentMessageCache        messageCache = new RecentMessageCache(10);
    private final ExecutorService           executor     = Executors.newCachedThreadPool();
    private final CommandHandler            commands;
    private final UsageTracker              usageTracker = new UsageTracker();
    private final AtomicBoolean             pollingStartedOnce = new AtomicBoolean(false);

    public ChatListener(ChatIncludedPlugin plugin, Koi koi) {
        this.plugin   = plugin;
        this.koi      = koi;
        this.commands = new CommandHandler(plugin, koi, prefs, messageCache, executor);
    }

    @KoiEventHandler
    public void onRichMessage(RichMessageEvent event) {
        PluginSettings settings = plugin.readSettings();
        if (!settings.enabled) return;

        // Start usage polling the first time we have a valid settings object
        if (pollingStartedOnce.compareAndSet(false, true)) {
            usageTracker.startPolling(settings);
        }

        String platform   = resolvePlatform(event);
        if (!isPlatformEnabled(platform, settings)) return;

        String rawText    = event.getRaw();
        if (rawText == null || rawText.isBlank()) return;

        String senderName = safeGetDisplayName(event);
        String messageId  = safeGetId(event);

        // ── 1. Anti-loop: skip our own translation outputs ────────────────────
        if (TRANSLATION_OUTPUT_PATTERN.matcher(rawText).find()) {
            plugin.getLogger().debug("Skipping translation output: " + rawText);
            return;
        }

        // ── 2. Anti-loop: skip our own command responses ──────────────────────
        if (COMMAND_RESPONSE_PATTERN.matcher(rawText).find()) {
            plugin.getLogger().debug("Skipping command response: " + rawText);
            return;
        }

        // ── 3. Bot exclusion list ─────────────────────────────────────────────
        if (isExcluded(senderName, settings)) {
            plugin.getLogger().debug("Ignoring excluded user: " + senderName);
            usageTracker.recordFiltered(UsageTracker.FilterReason.BOT_EXCLUDED);
            return;
        }

        // ── 4. Extract translatable text using fragments ──────────────────────
        // Skips EMOTE and EMOJI fragments on all platforms (Twitch, Kick, YouTube)
        String text = extractTextFromFragments(event, rawText);

        // ── 5. Cache all non-command messages for !translate ──────────────────
        // By this point, translation outputs and command responses are already
        // filtered out above, so the cache only ever contains real chat messages
        // from both streamers and viewers -- commands themselves are not cached.
        boolean isCommand  = commands.isCommand(rawText);
        if (!isCommand) {
            messageCache.add(messageId, rawText, senderName);
        }

        // ── 6. Streamer message handling ──────────────────────────────────────
        boolean isStreamer = isStreamer(event);
        if (isStreamer) {
            if (isCommand) {
                commands.handle(event, rawText, settings);
                return;
            }
            if (settings.twoWayEnabled) {
                handleStreamerMention(event, text, platform, settings);
            }
            return; // Never auto-translate the streamer's own messages
        }

        // ── 7. Viewer command handling ────────────────────────────────────────
        if (isCommand) {
            commands.handle(event, rawText, settings);
            return;
        }

        // ── 8. Skip if message is empty or too short after emote removal ──────
        if (text.isBlank() || text.length() < settings.minimumMessageLength) {
            plugin.getLogger().debug("Skipping message (empty/too short after emote strip): " + rawText);
            usageTracker.recordFiltered(UsageTracker.FilterReason.TOO_SHORT);
            return;
        }

        // ── 9. Skip if viewer's pinned language already matches target ─────────
        String pinnedLang = prefs.get(senderName);
        if (pinnedLang != null && pinnedLang.equalsIgnoreCase(settings.targetLanguage)) {
            plugin.getLogger().debug("Skipping — " + senderName + " pinned to target language");
            usageTracker.recordFiltered(UsageTracker.FilterReason.SAME_LANGUAGE);
            return;
        }

        // ── 10. Deduplication and rate limiting ───────────────────────────────
        int hash = text.strip().hashCode();
        if (tracker.isDuplicate(hash, settings.deduplicationWindowSeconds)) {
            usageTracker.recordFiltered(UsageTracker.FilterReason.DEDUPLICATED);
            return;
        }
        if (!tracker.isAllowed(settings.cooldownMs, settings.burstLimit)) return;
        tracker.markSeen(hash);

        // ── 11. Translate inbound viewer message ──────────────────────────────
        final String cleanText       = text;
        final String targetLang      = settings.targetLanguage;
        final UserPlatform uPlatform = event.getStreamer().getPlatform();
        DeepLClient deepL            = new DeepLClient(settings);

        executor.submit(() ->
            deepL.translate(cleanText, targetLang)
                .thenAccept(result -> {
                    if (result.detectedSourceLanguage.startsWith(targetLang)) {
                        usageTracker.recordFiltered(UsageTracker.FilterReason.SAME_LANGUAGE);
                        return;
                    }

                    // Record the translation against the tracker
                    usageTracker.recordTranslation(result.billedCharacters, cleanText.length());

                    String effectiveLang = (pinnedLang != null)
                            ? pinnedLang
                            : result.detectedSourceLanguage;

                    prefs.setAuto(senderName, result.detectedSourceLanguage);

                    if (settings.twoWayEnabled) {
                        memory.store(messageId, effectiveLang, senderName);
                    }

                    String reply = buildTranslationReply(
                            effectiveLang, targetLang,
                            result.translatedText,
                            senderName,
                            settings.showAttribution);

                    plugin.getLogger().info("Translated on " + platform + " | " + reply);

                    try {
                        koi.sendChat(uPlatform, reply, KoiChatterType.CLIENT, messageId, false);
                    } catch (Exception e) {
                        plugin.getLogger().severe("Failed to send translation: " + e.getMessage());
                    }

                    // Refresh the stats UI in the widget
                    try {
                        plugin.refreshWidgetStats();
                    } catch (Exception ignored) {}
                })
                .exceptionally(ex -> {
                    plugin.getLogger().severe("DeepL API error: " + ex.getMessage());
                    return null;
                })
        );
    }

    private void handleStreamerMention(RichMessageEvent event, String text,
                                       String platform, PluginSettings settings) {
        Matcher matcher = MENTION_PATTERN.matcher(text);
        while (matcher.find()) {
            String mentionedUsername = matcher.group(1);

            String viewerLang = prefs.get(mentionedUsername);
            if (viewerLang == null) {
                ConversationMemory.Entry entry = memory.lookupByUsername(
                        mentionedUsername, settings.conversationMemoryMinutes);
                if (entry != null) viewerLang = entry.sourceLanguage;
            }

            if (viewerLang == null) continue;
            if (viewerLang.equalsIgnoreCase(settings.targetLanguage)) continue;

            final String targetLang = viewerLang;
            final String viewer     = mentionedUsername;
            final UserPlatform up   = event.getStreamer().getPlatform();
            DeepLClient deepL       = new DeepLClient(settings);

            executor.submit(() ->
                deepL.translate(text, targetLang)
                    .thenAccept(result -> {
                        if (result.detectedSourceLanguage.equalsIgnoreCase(targetLang)
                                || result.detectedSourceLanguage.startsWith(targetLang)) return;

                        String reply = "@" + viewer + " [" + settings.targetLanguage
                                + "->" + targetLang + "] " + result.translatedText;

                        plugin.getLogger().info("Two-way reply on " + platform
                                + " to " + viewer + " | " + reply);

                        try {
                            koi.sendChat(up, reply, KoiChatterType.CLIENT, null, false);
                        } catch (Exception e) {
                            plugin.getLogger().severe("Failed two-way reply: " + e.getMessage());
                        }
                    })
                    .exceptionally(ex -> {
                        plugin.getLogger().severe("DeepL two-way error: " + ex.getMessage());
                        return null;
                    })
            );
        }
    }

    // ── Text extraction ───────────────────────────────────────────────────────

    /**
     * Builds translatable text from Casterlabs message fragments.
     *
     * TEXT     → included (the words to translate)
     * MENTION  → included (preserve @username references)
     * LINK     → included (preserve URLs)
     * EMOTE    → SKIPPED (image, not translatable text)
     * EMOJI    → SKIPPED (unicode pictograph, not text)
     *
     * Falls back to getRaw() with emote tag stripping if fragments unavailable.
     */
    private String extractTextFromFragments(RichMessageEvent event, String fallback) {
        try {
            List<ChatFragment> fragments = event.getFragments();
            if (fragments == null || fragments.isEmpty()) return fallback;

            StringBuilder sb = new StringBuilder();
            for (ChatFragment fragment : fragments) {
                ChatFragment.FragmentType type = fragment.getType();
                if (type == ChatFragment.FragmentType.EMOTE
                        || type == ChatFragment.FragmentType.EMOJI) {
                    continue;
                }
                String raw = fragment.getRaw();
                if (raw != null && !raw.isBlank()) {
                    sb.append(raw);
                }
            }

            String result = sb.toString().strip()
                              .replaceAll("\\s{2,}", " ").strip();
            return result.isEmpty() ? "" : result;

        } catch (Exception e) {
            plugin.getLogger().debug("Fragment extraction failed, using raw: " + e.getMessage());
            return fallback.replaceAll("\\[emote:[^\\]]+\\]", " ")
                           .replaceAll("\\s{2,}", " ").strip();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String buildTranslationReply(String sourceLang, String targetLang,
                                          String translatedText, String senderName,
                                          boolean showAttribution) {
        String tag = "[" + sourceLang + "->" + targetLang + "] ";
        if (showAttribution) {
            return tag + "@" + senderName + ": " + translatedText;
        }
        return tag + translatedText;
    }

    private String resolvePlatform(RichMessageEvent event) {
        try { return event.getStreamer().getPlatform().name().toUpperCase(); }
        catch (Exception e) { return "UNKNOWN"; }
    }

    private boolean isPlatformEnabled(String platform, PluginSettings s) {
        if (PLATFORM_TWITCH.equals(platform))  return s.twitchEnabled;
        if (PLATFORM_KICK.equals(platform))    return s.kickEnabled;
        if (PLATFORM_YOUTUBE.equals(platform)) return s.youtubeEnabled;
        if (PLATFORM_TROVO.equals(platform))   return s.trovoEnabled;
        return false;
    }

    private boolean isStreamer(RichMessageEvent event) {
        try {
            return event.getSender().getUPID().equals(event.getStreamer().getUPID());
        } catch (Exception e) { return false; }
    }

    private boolean isExcluded(String username, PluginSettings settings) {
        if (username == null) return false;
        return settings.getExcludedUsernamesSet().contains(username.toLowerCase());
    }

    private String safeGetId(RichMessageEvent event) {
        try { return event.getId(); } catch (Exception e) { return null; }
    }

    private String safeGetDisplayName(RichMessageEvent event) {
        try { return event.getSender().getDisplayname(); }
        catch (Exception e) { return "viewer"; }
    }

    public UsageTracker getTracker() {
        return usageTracker;
    }

    public void shutdown() {
        executor.shutdownNow();
        usageTracker.shutdown();
    }
}
