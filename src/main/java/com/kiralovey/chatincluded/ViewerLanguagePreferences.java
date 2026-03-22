package com.kiralovey.chatincluded;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Session-only store of viewer language preferences.
 *
 * Preferences can be set two ways:
 *   - Manually via !setlang (locked -- auto-detection will not overwrite)
 *   - Automatically when ChatIncluded translates a viewer's message
 *
 * Resets when Casterlabs is restarted.
 */
public class ViewerLanguagePreferences {

    // username (lowercase) -> language code (uppercase e.g. "ES")
    private final Map<String, String> preferences = new HashMap<>();

    // Usernames that have manually set their language via !setlang
    // Auto-detection will not overwrite these
    private final Set<String> manuallySet = new HashSet<>();

    /**
     * Manually set a viewer's language via !setlang.
     * This locks the preference -- auto-detection will not overwrite it.
     */
    public synchronized void setManual(String username, String languageCode) {
        if (username == null || languageCode == null) return;
        String key = username.toLowerCase();
        preferences.put(key, languageCode.toUpperCase());
        manuallySet.add(key);
    }

    /**
     * Auto-set a viewer's language from translation detection.
     * Will NOT overwrite a manually set preference.
     */
    public synchronized void setAuto(String username, String languageCode) {
        if (username == null || languageCode == null) return;
        String key = username.toLowerCase();
        if (manuallySet.contains(key)) return; // Respect manual lock
        preferences.put(key, languageCode.toUpperCase());
    }

    /**
     * Get a viewer's preferred language, or null if not set.
     */
    public synchronized String get(String username) {
        if (username == null) return null;
        return preferences.get(username.toLowerCase());
    }

    /**
     * Returns true if at least one viewer has a language preference.
     */
    public synchronized boolean hasAnyPreferences() {
        return !preferences.isEmpty();
    }

    /**
     * Returns all unique language codes currently registered.
     * Used by !speak to know which languages to translate into.
     */
    public synchronized Collection<String> getActiveLanguages() {
        return Collections.unmodifiableCollection(preferences.values());
    }

    /**
     * Returns whether this viewer's language was manually locked via !setlang.
     */
    public synchronized boolean isManuallySet(String username) {
        if (username == null) return false;
        return manuallySet.contains(username.toLowerCase());
    }

    /**
     * Removes a viewer's language preference entirely.
     * Used to undo a !setlang if DeepL rejects the code.
     */
    public synchronized void clear(String username) {
        if (username == null) return;
        String key = username.toLowerCase();
        preferences.remove(key);
        manuallySet.remove(key);
    }

    public synchronized int size() {
        return preferences.size();
    }
}
