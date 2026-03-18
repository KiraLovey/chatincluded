package com.kiralovey.chatincluded;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Pattern;

/**
 * Rolling buffer of the most recent chat messages.
 *
 * Used by !translate <code> (no message) to find the most recent message
 * and by reply-based !translate <code> to look up a specific message by ID.
 *
 * Our own translation outputs ([XX->XX]) are never stored here.
 */
public class RecentMessageCache {

    private static final Pattern TRANSLATION_OUTPUT_PATTERN =
            Pattern.compile("\\[[A-Z]{2,5}->[A-Z]{2,5}\\]");

    public static class CachedMessage {
        public final String messageId;
        public final String text;
        public final String senderDisplayName;

        public CachedMessage(String messageId, String text, String senderDisplayName) {
            this.messageId         = messageId;
            this.text              = text;
            this.senderDisplayName = senderDisplayName;
        }
    }

    private final int maxSize;
    private final Deque<CachedMessage> cache = new ArrayDeque<>();

    public RecentMessageCache(int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * Add a message to the cache.
     * Silently ignores our own translation outputs.
     */
    public synchronized void add(String messageId, String text, String senderDisplayName) {
        if (text == null || text.isBlank()) return;
        // Never cache our own translation outputs
        if (TRANSLATION_OUTPUT_PATTERN.matcher(text).find()) return;

        if (cache.size() >= maxSize) {
            cache.pollLast(); // Remove oldest
        }
        cache.addFirst(new CachedMessage(messageId, text, senderDisplayName));
    }

    /**
     * Returns the most recent cached message, or null if cache is empty.
     */
    public synchronized CachedMessage getMostRecent() {
        return cache.isEmpty() ? null : cache.peekFirst();
    }

    /**
     * Looks up a specific message by ID, or null if not found.
     * Used when a viewer replies to a message and types !translate <code>.
     */
    public synchronized CachedMessage getById(String messageId) {
        if (messageId == null) return null;
        for (CachedMessage msg : cache) {
            if (messageId.equals(msg.messageId)) return msg;
        }
        return null;
    }
}
