package com.kiralovey.chatincluded;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Remembers the source language and sender username of translated messages
 * indexed by both message ID and username.
 *
 * Message ID lookup: used when streamer @mentions a viewer
 * Username lookup: used as fallback when message ID isn't available
 *
 * Entries expire after a configurable number of minutes.
 * Both maps are capped at 500 entries to prevent unbounded growth.
 */
public class ConversationMemory {

    public static class Entry {
        public final String sourceLanguage;
        public final String senderDisplayName;
        public final Instant createdAt;

        public Entry(String sourceLanguage, String senderDisplayName) {
            this.sourceLanguage    = sourceLanguage;
            this.senderDisplayName = senderDisplayName;
            this.createdAt         = Instant.now();
        }
    }

    // Indexed by message ID
    private final Map<String, Entry> byMessageId = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Entry> eldest) {
            return size() > 500;
        }
    };

    // Indexed by username (lowercase) -- most recent entry per user
    private final Map<String, Entry> byUsername = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Entry> eldest) {
            return size() > 500;
        }
    };

    /**
     * Store an entry by both message ID and username.
     */
    public synchronized void store(String messageId, String sourceLanguage, String senderDisplayName) {
        if (sourceLanguage == null || senderDisplayName == null) return;

        Entry entry = new Entry(sourceLanguage, senderDisplayName);

        if (messageId != null && !messageId.isBlank()) {
            byMessageId.put(messageId, entry);
        }
        // Always store by username so @mention lookup works
        byUsername.put(senderDisplayName.toLowerCase(), entry);
    }

    /**
     * Look up by message ID -- used for direct reply threading.
     */
    public synchronized Entry lookupByMessageId(String messageId, int memoryMinutes) {
        if (messageId == null) return null;
        return getIfValid(byMessageId.get(messageId), memoryMinutes);
    }

    /**
     * Look up by username -- used for @mention detection.
     */
    public synchronized Entry lookupByUsername(String username, int memoryMinutes) {
        if (username == null) return null;
        return getIfValid(byUsername.get(username.toLowerCase()), memoryMinutes);
    }

    private Entry getIfValid(Entry entry, int memoryMinutes) {
        if (entry == null) return null;
        if (Instant.now().isAfter(entry.createdAt.plusSeconds(memoryMinutes * 60L))) {
            return null; // expired
        }
        return entry;
    }
}
