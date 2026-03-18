package com.kiralovey.chatincluded;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe tracker for two distinct concerns:
 *
 * 1. <b>Deduplication</b> – remembers a hash of each translated message for a
 *    configurable window (seconds). If the same message arrives on a second
 *    platform within that window, the second occurrence is skipped.
 *
 * 2. <b>Rate-limiting / burst control</b> – enforces a minimum gap between
 *    translation requests (cooldown) and caps the number of translations
 *    allowed within any single cooldown period (burst limit).
 */
public class TranslationTracker {

    // ── Deduplication ─────────────────────────────────────────────────────────

    /**
     * Simple LRU-ish cache: key = message hash, value = time of first translation.
     * Capped at 200 entries to prevent unbounded growth.
     */
    private final Map<Integer, Instant> seen = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, Instant> eldest) {
            return size() > 200;
        }
    };

    // ── Rate limiting ─────────────────────────────────────────────────────────

    private volatile long  windowStartMs   = 0L;
    private final AtomicInteger burstCount = new AtomicInteger(0);

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if this message has already been translated within
     * the deduplication window and should therefore be skipped.
     *
     * @param messageHash            a hash of the message text
     * @param windowSeconds          deduplication window from settings
     */
    public synchronized boolean isDuplicate(int messageHash, int windowSeconds) {
        Instant prev = seen.get(messageHash);
        if (prev == null) return false;
        return Instant.now().isBefore(prev.plusSeconds(windowSeconds));
    }

    /**
     * Records that a message has been translated now.
     *
     * @param messageHash a hash of the message text
     */
    public synchronized void markSeen(int messageHash) {
        seen.put(messageHash, Instant.now());
    }

    /**
     * Checks whether a new translation is allowed given the rate-limit settings.
     * Resets the burst counter when the cooldown window has elapsed.
     *
     * @param cooldownMs  minimum gap between translation windows
     * @param burstLimit  max translations per cooldown window
     * @return {@code true} if the translation may proceed
     */
    public synchronized boolean isAllowed(int cooldownMs, int burstLimit) {
        long now = System.currentTimeMillis();

        if (now - windowStartMs >= cooldownMs) {
            // New window – reset counter
            windowStartMs = now;
            burstCount.set(0);
        }

        if (burstCount.get() >= burstLimit) {
            return false; // burst limit hit
        }

        burstCount.incrementAndGet();
        return true;
    }
}
