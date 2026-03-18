package com.kiralovey.chatincluded;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * DeepL supported target language codes.
 * Used to validate !setlang and !translate requests before hitting the API.
 * Common mistakes: IN (should be ID for Indonesian), CN (should be ZH for Chinese)
 */
public class SupportedLanguages {

    private static final Set<String> CODES = new HashSet<>(Arrays.asList(
            "AR", "BG", "CS", "DA", "DE", "EL", "EN", "EN-GB", "EN-US",
            "ES", "ET", "FI", "FR", "HU", "ID", "IT", "JA", "KO",
            "LT", "LV", "NB", "NL", "PL", "PT", "PT-BR", "PT-PT",
            "RO", "RU", "SK", "SL", "SV", "TR", "UK", "ZH"
    ));

    // Common mistakes and their corrections
    private static final String[][] SUGGESTIONS = {
            {"IN", "ID"},  // Indonesian
            {"CN", "ZH"},  // Chinese
            {"JP", "JA"},  // Japanese
            {"KR", "KO"},  // Korean
            {"BR", "PT-BR"}, // Brazilian Portuguese
            {"UK", "UK"},  // Ukrainian (actually valid)
            {"GR", "EL"},  // Greek
            {"CZ", "CS"},  // Czech
    };

    public static boolean isSupported(String code) {
        if (code == null) return false;
        return CODES.contains(code.toUpperCase());
    }

    /**
     * Returns a suggestion if the code is a common mistake, or null if no suggestion.
     */
    public static String getSuggestion(String code) {
        if (code == null) return null;
        String upper = code.toUpperCase();
        for (String[] pair : SUGGESTIONS) {
            if (pair[0].equals(upper)) return pair[1];
        }
        return null;
    }
}
