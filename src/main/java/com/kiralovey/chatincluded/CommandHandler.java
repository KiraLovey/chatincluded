package com.kiralovey.chatincluded;

import co.casterlabs.caffeinated.pluginsdk.koi.Koi;
import co.casterlabs.koi.api.KoiChatterType;
import co.casterlabs.koi.api.types.events.RichMessageEvent;
import co.casterlabs.koi.api.types.user.User;
import co.casterlabs.koi.api.types.user.UserPlatform;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class CommandHandler {

    private static final String LANGUAGES_MESSAGE =
            "ChatIncluded language codes — "
            + "EN (English), ES (Spanish), FR (French), DE (German), PT (Portuguese), "
            + "IT (Italian), JA (Japanese), KO (Korean), ZH (Chinese), RU (Russian), "
            + "AR (Arabic), NL (Dutch), PL (Polish), SV (Swedish), TR (Turkish). "
            + "Full list: https://developers.deepl.com/docs/resources/supported-languages";

    private static final String GUIDE_MESSAGE =
            "ChatIncluded \u2014 Break the language barrier, live. "
            + "Commands & language codes: https://chatincluded.live/#commands";

    private final ChatIncludedPlugin        plugin;
    private final Koi                       koi;
    private final ViewerLanguagePreferences prefs;
    private final RecentMessageCache        messageCache;
    private final ExecutorService           executor;

    public CommandHandler(ChatIncludedPlugin plugin, Koi koi,
                          ViewerLanguagePreferences prefs,
                          RecentMessageCache messageCache,
                          ExecutorService executor) {
        this.plugin       = plugin;
        this.koi          = koi;
        this.prefs        = prefs;
        this.messageCache = messageCache;
        this.executor     = executor;
    }

    public boolean isCommand(String text) {
        if (text == null) return false;
        String lower = text.trim().toLowerCase();
        return lower.startsWith("!chatincluded")
                || lower.startsWith("!guide")
                || lower.startsWith("!translate")
                || lower.startsWith("!setlang")
                || lower.startsWith("!languages")
                || lower.startsWith("!speak");
    }

    public void handle(RichMessageEvent event, String text, PluginSettings settings) {
        String trimmed  = text.trim();
        String lower    = trimmed.toLowerCase();
        UserPlatform up = event.getStreamer().getPlatform();
        String sender   = safeGetDisplayName(event);

        if (lower.startsWith("!guide")) {
            sendChat(up, GUIDE_MESSAGE);

        } else if (lower.startsWith("!chatincluded")) {
            sendChat(up, settings.chatIncludedMessage);

        } else if (lower.startsWith("!languages")) {
            sendChat(up, LANGUAGES_MESSAGE);

        } else if (lower.startsWith("!setlang")) {
            handleSetLang(up, trimmed, sender, settings);

        } else if (lower.startsWith("!translate")) {
            handleTranslate(event, trimmed, sender, settings);

        } else if (lower.startsWith("!speak")) {
            handleSpeak(event, trimmed, sender, settings);
        }
    }

    // ── Command implementations ───────────────────────────────────────────────

    private void handleSetLang(UserPlatform up, String text, String sender,
                                PluginSettings settings) {
        String[] parts = text.split("\\s+", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            sendChat(up, "@" + sender + " Usage: !setlang <code> (e.g. !setlang ES) — type !languages for codes.");
            return;
        }

        String code = parts[1].trim().toUpperCase();

        if (!SupportedLanguages.isSupported(code)) {
            String suggestion = SupportedLanguages.getSuggestion(code);
            String hint = (suggestion != null) ? " Did you mean " + suggestion + "?" : "";
            sendChat(up, "@" + sender + " \"" + code + "\" is not a supported language code." + hint
                    + " Type !languages for valid codes.");
            return;
        }

        prefs.setManual(sender, code);
        plugin.getLogger().info("Viewer " + sender + " set language preference to " + code);

        // English confirmation
        sendChat(up, "@" + sender + " Your language has been set to " + code
                + ". The streamer's replies will be translated for you!");

        // Also send confirmation in their language if not English
        if (!code.startsWith("EN")) {
            DeepLClient deepL = new DeepLClient(settings);
            String messageToTranslate = "Your language has been set to " + code
                    + ". The streamer's replies will be translated for you!";

            executor.submit(() ->
                deepL.translate(messageToTranslate, code)
                    .thenAccept(result -> {
                        sendChat(up, "@" + sender + " " + result.translatedText);
                    })
                    .exceptionally(ex -> {
                        plugin.getLogger().debug("Could not translate !setlang confirmation: "
                                + ex.getMessage());
                        return null;
                    })
            );
        }
    }

    private void handleTranslate(RichMessageEvent event, String text,
                                  String sender, PluginSettings settings) {
        String[] parts = text.split("\\s+", 3);
        if (parts.length < 2 || parts[1].isBlank()) {
            sendChat(event.getStreamer().getPlatform(),
                    "@" + sender + " Usage: !translate <code> [message] — e.g. !translate ES or !translate ES Hello!");
            return;
        }

        String targetCode = parts[1].trim().toUpperCase();

        if (!SupportedLanguages.isSupported(targetCode)) {
            String suggestion = SupportedLanguages.getSuggestion(targetCode);
            String hint = (suggestion != null) ? " Did you mean " + suggestion + "?" : "";
            sendChat(event.getStreamer().getPlatform(),
                    "@" + sender + " \"" + targetCode + "\" is not a supported language code." + hint
                    + " Type !languages for valid codes.");
            return;
        }

        String messageToTranslate = null;
        String sourceContext      = null;

        // 1. Reply target
        String replyTargetId = safeGetReplyTarget(event);
        if (replyTargetId != null) {
            RecentMessageCache.CachedMessage replied = messageCache.getById(replyTargetId);
            if (replied != null) {
                messageToTranslate = replied.text;
                sourceContext      = "reply to " + replied.senderDisplayName;
            }
        }

        // 2. Inline text
        if (messageToTranslate == null && parts.length == 3 && !parts[2].isBlank()) {
            messageToTranslate = parts[2].trim();
            sourceContext      = "inline text";
        }

        // 3. Most recent cached message
        if (messageToTranslate == null) {
            RecentMessageCache.CachedMessage recent = messageCache.getMostRecent();
            if (recent != null) {
                messageToTranslate = recent.text;
                sourceContext      = "most recent message from " + recent.senderDisplayName;
            }
        }

        if (messageToTranslate == null) {
            sendChat(event.getStreamer().getPlatform(),
                    "@" + sender + " No recent messages found to translate.");
            return;
        }

        final String finalText    = messageToTranslate;
        final String finalContext = sourceContext;
        final UserPlatform up     = event.getStreamer().getPlatform();
        DeepLClient deepL         = new DeepLClient(settings);

        executor.submit(() ->
            deepL.translate(finalText, targetCode)
                .thenAccept(result -> {
                    if (result.detectedSourceLanguage.equalsIgnoreCase(targetCode)
                            || result.detectedSourceLanguage.startsWith(targetCode)) {
                        sendChat(up, "@" + sender + " That message is already in " + targetCode + "!");
                        return;
                    }

                    String reply = "@" + sender + " [" + result.detectedSourceLanguage
                            + "->" + targetCode + "] " + result.translatedText;

                    plugin.getLogger().info("!translate (" + finalContext + ") | " + reply);
                    sendChat(up, reply);
                })
                .exceptionally(ex -> {
                    plugin.getLogger().severe("!translate error: " + ex.getMessage());
                    sendChat(up, "@" + sender + " Translation failed. Please check the language code and try again.");
                    return null;
                })
        );
    }

    private void handleSpeak(RichMessageEvent event, String text,
                              String sender, PluginSettings settings) {
        if (!hasAccess(event, settings.speakAccessLevel)) return;

        String[] parts = text.split("\\s+", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            sendChat(event.getStreamer().getPlatform(),
                    "Usage: !speak <message> — translates your message into all active viewer languages.");
            return;
        }

        String message = parts[1].trim();

        if (!prefs.hasAnyPreferences()) {
            sendChat(event.getStreamer().getPlatform(),
                    "No viewers have set a language preference yet. "
                    + "Viewers can use !setlang <code> to register, "
                    + "or just chat — their language is detected automatically!");
            return;
        }

        Set<String> uniqueLangs = new LinkedHashSet<>(prefs.getActiveLanguages());
        UserPlatform up         = event.getStreamer().getPlatform();
        DeepLClient deepL       = new DeepLClient(settings);

        for (String langCode : uniqueLangs) {
            if (langCode.equalsIgnoreCase(settings.targetLanguage)) continue;

            final String targetCode = langCode;
            executor.submit(() ->
                deepL.translate(message, targetCode)
                    .thenAccept(result -> {
                        if (result.detectedSourceLanguage.equalsIgnoreCase(targetCode)
                                || result.detectedSourceLanguage.startsWith(targetCode)) return;

                        String reply = "[" + settings.targetLanguage
                                + "->" + targetCode + "] " + result.translatedText;
                        plugin.getLogger().info("!speak -> " + targetCode + " | " + reply);
                        sendChat(up, reply);
                    })
                    .exceptionally(ex -> {
                        plugin.getLogger().severe("!speak error for " + targetCode + ": " + ex.getMessage());
                        return null;
                    })
            );
        }
    }

    // ── Access control ────────────────────────────────────────────────────────

    private boolean hasAccess(RichMessageEvent event, String requiredLevel) {
        if ("everyone".equalsIgnoreCase(requiredLevel)) return true;
        if (isStreamer(event)) return true;
        if ("streamer".equalsIgnoreCase(requiredLevel)) return false;
        boolean isMod = isMod(event);
        if ("mod".equalsIgnoreCase(requiredLevel)) return isMod;
        boolean isSub = isSub(event);
        if ("sub".equalsIgnoreCase(requiredLevel)) return isMod || isSub;
        return false;
    }

    private boolean isStreamer(RichMessageEvent event) {
        try {
            return event.getSender().getUPID().equals(event.getStreamer().getUPID());
        } catch (Exception e) { return false; }
    }

    private boolean isMod(RichMessageEvent event) {
        try {
            for (User.UserRoles role : event.getSender().getRoles()) {
                String r = role.name().toUpperCase();
                if (r.equals("MOD") || r.equals("STAFF") || r.equals("ADMIN")) return true;
            }
            return false;
        } catch (Exception e) { return false; }
    }

    private boolean isSub(RichMessageEvent event) {
        try {
            for (User.UserRoles role : event.getSender().getRoles()) {
                if (role.name().toUpperCase().equals("SUBSCRIBER")) return true;
            }
            return false;
        } catch (Exception e) { return false; }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void sendChat(UserPlatform platform, String message) {
        try {
            koi.sendChat(platform, message, KoiChatterType.CLIENT, null, false);
        } catch (Exception e) {
            plugin.getLogger().severe("CommandHandler failed to send: " + e.getMessage());
        }
    }

    private String safeGetDisplayName(RichMessageEvent event) {
        try { return event.getSender().getDisplayname(); }
        catch (Exception e) { return "viewer"; }
    }

    private String safeGetReplyTarget(RichMessageEvent event) {
        try { return event.getReplyTarget(); }
        catch (Exception e) { return null; }
    }
}
