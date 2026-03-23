package com.novusforge.astrum.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CreatorIdentityFilter: Deterministic impersonation protection for Project Astrum.
 * Guards against identity theft of known Minecraft/YouTube community figures.
 * 
 * Implements multiple detection layers:
 * 1. Exact match (normalized)
 * 2. Leet speak conversion (0→o, 1→i/l, 3→e, 4→a, 5→s, 7→t)
 * 3. Substring matching
 * 4. Levenshtein distance (max 2 characters difference)
 * 
 * Protected categories:
 * - BAD_ACTOR_NAMES: Known problematic creators (blocked outright)
 * - GOOD_CREATOR_NAMES: Legitimate creators (protected from impersonation)
 * - ALIASES: Real name → Creator name mappings
 * - WHITELIST: Verified YouTube accounts (allowed to use their names)
 * 
 * YouTube Verification:
 * - Real YouTubers can verify their account via OAuth
 * - Verified accounts are added to whitelist
 * - Whitelisted users can use their own channel name
 */
public final class CreatorIdentityFilter {

    public enum FilterSeverity {
        ALLOW,   // Username is permitted
        WARN,    // Username is suspicious but allowed (logged for review)
        BLOCK    // Username is blocked (impersonation attempt)
    }

    public record FilterResult(boolean allowed, String reason, FilterSeverity severity) {
        public static final FilterResult ALLOW = new FilterResult(true, "Username permitted", FilterSeverity.ALLOW);
    }

    /**
     * Verified YouTube account information
     */
    public record VerifiedAccount(
        String youtubeChannelId,
        String youtubeChannelName,
        String minecraftUsername,
        long verificationDate,
        boolean isPartner
    ) {}

    // Bad actors: Known problematic creators (DMCA, controversy, impersonation risk)
    private static final Set<String> BAD_ACTOR_NAMES = Set.of(
        // Major controversial figures
        "dream", "dreamwastaken", "dreamxd",
        "callmecarson", "carson", "lionmaker",
        "jinbop", "bashurverse", "wilbursoot", "wilbur",
        "skydoesminecraft", "skydoesgaming", "adamdahlberg",
        "georgenotfound", "georgewbh", "skeppy",
        "iskall85", "iskall", "punz", "gerg",
        "marlowww", "dangermario", "popularmmos",
        "popularmmospat", "jschlatt", "schlatt",
        "lforlee", "lforleex",
        
        // Additional protected names
        "techblade", "technoblade", "techno",
        "tommyinnit", "tommy", "tubbo", "ranboo",
        "philza", "ph1lza"
    );

    // Good creators: Legitimate creators protected from impersonation
    private static final Set<String> GOOD_CREATOR_NAMES = Set.of(
        // Event creators / partners
        "sword4000", "mindofneo", "neo",
        "nyxlunarii", "nyx", "sharkilz",
        "pharolen", "bobicraft",
        "minecraftcurios", "ledgy",
        "knappyt", "knapp", "knappy",
        
        // Major creators
        "sapnap", "badboyhalo", "bbh",
        "quackity", "nihachu", "niki",
        "jackmanifold", "jack", "karljacobs", "karl",
        "fundy", "hbomb94", "hbomb",
        "vikkstar123", "vikkstar", "bdoubleo100", "bdubs",
        "impulseSV", "impulse", "scar", "scara",
        "goodtimeswithscar", "grian", "mumbo", "mumbojumbo",
        "zombiecleo", "cleo", "rendog", "ren",
        "ethoslab", "etho", "xbcrafted", "xb",
        "docm77", "doc", "jellies", "tango", "tangotek",
        "keralis", "joehills", "beef", "beefcraft"
    );

    // Real name → Creator name aliases
    private static final Map<String, String> ALIASES = Map.ofEntries(
        Map.entry("clay", "Dream"),
        Map.entry("clayhuff", "Dream"),
        Map.entry("marcuswilton", "LionMaker"),
        Map.entry("brandonasher", "Bashurverse"),
        Map.entry("zakahmed", "Skeppy"),
        Map.entry("georgedavidson", "GeorgeNotFound"),
        Map.entry("thomas", "TommyInnit"),
        Map.entry("toby", "Tubbo"),
        Map.entry("ran", "Ranboo"),
        Map.entry("nick", "Nihachu"),
        Map.entry("niki", "Nihachu"),
        Map.entry("jack", "JackManifold"),
        Map.entry("karl", "KarlJacobs"),
        Map.entry("george", "GeorgeNotFound"),
        Map.entry("sap", "Sapnap"),
        Map.entry("bad", "BadBoyHalo"),
        Map.entry("alex", "Quackity"),
        Map.entry("wilbur", "WilburSoot"),
        Map.entry("phil", "Philza"),
        Map.entry("techno", "Technoblade"),
        Map.entry("blade", "Technoblade"),
        Map.entry("dave", "Technoblade"),
        Map.entry("charles", "Cr1TiKaL"),
        Map.entry("moist", "Cr1TiKaL"),
        Map.entry("penguinz0", "Cr1TiKaL")
    );

    // Verified YouTube accounts (whitelist)
    // Thread-safe for concurrent access
    private static final Map<String, VerifiedAccount> WHITELIST = new ConcurrentHashMap<>();
    
    // Cache for normalized names to avoid recalculation
    private static final Map<String, String> NORMALIZATION_CACHE = new ConcurrentHashMap<>();

    // Private constructor - utility class
    private CreatorIdentityFilter() {}

    /**
     * Main filter method - checks username against all protection layers.
     * 
     * @param username The username to check
     * @return FilterResult with decision, reason, and severity
     */
    public static FilterResult check(String username) {
        if (username == null || username.trim().isEmpty()) {
            return new FilterResult(false, "Username cannot be empty", FilterSeverity.BLOCK);
        }
        
        if (username.length() < 3) {
            return new FilterResult(false, "Username must be at least 3 characters", FilterSeverity.BLOCK);
        }
        
        if (username.length() > 20) {
            return new FilterResult(false, "Username must be at most 20 characters", FilterSeverity.BLOCK);
        }
        
        // Layer 0: Check whitelist first (verified YouTubers)
        if (isWhitelisted(username)) {
            return new FilterResult(true, "Verified YouTube account", FilterSeverity.ALLOW);
        }
        
        // Layer 1: Exact Match (Normalized)
        String normalized = normalize(username);
        if (BAD_ACTOR_NAMES.contains(normalized)) {
            return new FilterResult(false, "Bad actor identity blocked: " + username, FilterSeverity.BLOCK);
        }
        if (GOOD_CREATOR_NAMES.contains(normalized)) {
            return new FilterResult(false, "Creator impersonation blocked: " + username, FilterSeverity.BLOCK);
        }
        if (ALIASES.containsKey(normalized)) {
            return new FilterResult(false, "Identity blocked (Alias of " + ALIASES.get(normalized) + ")", FilterSeverity.BLOCK);
        }

        // Layer 2: Fuzzy Match (Leet Speak)
        String leetConverted = convertLeetSpeak(normalized);
        if (BAD_ACTOR_NAMES.contains(leetConverted) || GOOD_CREATOR_NAMES.contains(leetConverted) || ALIASES.containsKey(leetConverted)) {
            return new FilterResult(false, "Obfuscated identity detected (leet speak)", FilterSeverity.BLOCK);
        }

        // Layer 3: Substring Match
        for (String protectedName : BAD_ACTOR_NAMES) {
            if (normalized.contains(protectedName) && protectedName.length() >= 4) {
                return new FilterResult(true, "Username contains protected creator name (bad actor)", FilterSeverity.WARN);
            }
        }
        for (String protectedName : GOOD_CREATOR_NAMES) {
            if (normalized.contains(protectedName) && protectedName.length() >= 4) {
                return new FilterResult(true, "Username contains protected creator name", FilterSeverity.WARN);
            }
        }

        // Layer 4: Levenshtein Distance (for short usernames only)
        if (username.length() <= 15) {
            for (String protectedName : BAD_ACTOR_NAMES) {
                if (levenshtein(normalized, protectedName) <= 2) {
                    return new FilterResult(true, "Username too similar to protected creator name (bad actor)", FilterSeverity.WARN);
                }
            }
            for (String protectedName : GOOD_CREATOR_NAMES) {
                if (levenshtein(normalized, protectedName) <= 2) {
                    return new FilterResult(true, "Username too similar to protected creator name", FilterSeverity.WARN);
                }
            }
        }

        return FilterResult.ALLOW;
    }

    /**
     * Check username with YouTube verification context.
     * If user has verified YouTube account, use that for whitelist check.
     * 
     * @param username Minecraft username
     * @param youtubeChannelId YouTube channel ID (if verified, null otherwise)
     * @param youtubeChannelName YouTube channel name (if verified, null otherwise)
     * @return FilterResult with decision, reason, and severity
     */
    public static FilterResult checkWithYouTube(String username, String youtubeChannelId, String youtubeChannelName) {
        // If user has verified YouTube account, check whitelist
        if (youtubeChannelId != null && !youtubeChannelId.isEmpty()) {
            VerifiedAccount account = WHITELIST.get(youtubeChannelId);
            if (account != null) {
                // Verified account - allow if Minecraft username matches
                if (account.minecraftUsername().equalsIgnoreCase(username)) {
                    return new FilterResult(true, "Verified YouTube account: " + account.youtubeChannelName(), FilterSeverity.ALLOW);
                }
            }
        }
        
        // Fall back to standard check
        return check(username);
    }

    /**
     * Check if username is specifically a bad actor (not just a protected creator).
     */
    public static boolean isBadActor(String username) {
        String normalized = normalize(username);
        String leet = convertLeetSpeak(normalized);
        return BAD_ACTOR_NAMES.contains(normalized) || 
               BAD_ACTOR_NAMES.contains(leet) || 
               ALIASES.containsKey(normalized);
    }

    /**
     * Check if username is attempting to impersonate a good creator.
     */
    public static boolean isCreatorImpersonation(String username) {
        String normalized = normalize(username);
        String leet = convertLeetSpeak(normalized);
        return GOOD_CREATOR_NAMES.contains(normalized) || 
               GOOD_CREATOR_NAMES.contains(leet) ||
               levenshtein(normalized, GOOD_CREATOR_NAMES) <= 2;
    }

    /**
     * Check if username is whitelisted (verified YouTube account).
     */
    public static boolean isWhitelisted(String username) {
        String normalized = normalize(username);
        
        // Check if any verified account has this Minecraft username
        for (VerifiedAccount account : WHITELIST.values()) {
            if (normalize(account.minecraftUsername()).equals(normalized)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Check if YouTube channel is verified/whitelisted.
     */
    public static boolean isYouTubeVerified(String youtubeChannelId) {
        return WHITELIST.containsKey(youtubeChannelId);
    }

    /**
     * Add verified YouTube account to whitelist.
     * Called after successful OAuth verification.
     * 
     * @param channelId YouTube channel ID
     * @param channelName YouTube channel name
     * @param minecraftUsername Associated Minecraft username
     * @param isPartner Whether channel is YouTube Partner (monetized)
     * @return true if added successfully
     */
    public static boolean addVerifiedAccount(String channelId, String channelName, String minecraftUsername, boolean isPartner) {
        if (channelId == null || channelId.isEmpty()) {
            return false;
        }
        
        VerifiedAccount account = new VerifiedAccount(
            channelId,
            channelName,
            minecraftUsername,
            System.currentTimeMillis(),
            isPartner
        );
        
        WHITELIST.put(channelId, account);
        NORMALIZATION_CACHE.clear(); // Clear cache
        
        System.out.println("[CreatorIdentityFilter] Added verified account: " + channelName + " (" + channelId + ")");
        return true;
    }

    /**
     * Remove verified account from whitelist.
     * 
     * @param youtubeChannelId YouTube channel ID to remove
     * @return true if removed successfully
     */
    public static boolean removeVerifiedAccount(String youtubeChannelId) {
        VerifiedAccount removed = WHITELIST.remove(youtubeChannelId);
        if (removed != null) {
            NORMALIZATION_CACHE.clear();
            System.out.println("[CreatorIdentityFilter] Removed verified account: " + removed.youtubeChannelName());
            return true;
        }
        return false;
    }

    /**
     * Get verified account information.
     * 
     * @param youtubeChannelId YouTube channel ID
     * @return VerifiedAccount or null if not found
     */
    public static VerifiedAccount getVerifiedAccount(String youtubeChannelId) {
        return WHITELIST.get(youtubeChannelId);
    }

    /**
     * Get all verified accounts (for admin purposes).
     * 
     * @return Unmodifiable map of verified accounts
     */
    public static Map<String, VerifiedAccount> getAllVerifiedAccounts() {
        return Collections.unmodifiableMap(WHITELIST);
    }

    /**
     * Get count of verified accounts.
     */
    public static int getVerifiedAccountCount() {
        return WHITELIST.size();
    }

    /**
     * Normalize username: lowercase, remove spaces and special characters.
     * Uses cache for performance.
     */
    private static String normalize(String username) {
        return NORMALIZATION_CACHE.computeIfAbsent(username, key -> 
            key.toLowerCase()
               .replaceAll("\\s+", "")
               .replaceAll("[^a-z0-9]", "")
        );
    }

    /**
     * Convert leet speak to normal letters.
     * 0→o, 1→i/l, 3→e, 4→a, 5→s, 7→t, @→a
     */
    private static String convertLeetSpeak(String text) {
        return text.replaceAll("0", "o")
                   .replaceAll("1", "i")
                   .replaceAll("3", "e")
                   .replaceAll("4", "a")
                   .replaceAll("5", "s")
                   .replaceAll("7", "t")
                   .replaceAll("@", "a")
                   .replaceAll("[^a-z0-9]", "");
    }

    /**
     * Calculate Levenshtein distance between two strings.
     * Returns minimum number of single-character edits needed to change one word into the other.
     */
    private static int levenshtein(String s1, String s2) {
        if (s1.equals(s2)) return 0;
        if (s1.isEmpty()) return s2.length();
        if (s2.isEmpty()) return s1.length();
        
        int[] costs = new int[s2.length() + 1];
        
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        }
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) {
                costs[s2.length()] = lastValue;
            }
        }
        
        return costs[s2.length()];
    }

    /**
     * Calculate minimum Levenshtein distance between a string and a set of strings.
     */
    private static int levenshtein(String s, Set<String> strings) {
        int minDistance = Integer.MAX_VALUE;
        for (String str : strings) {
            int distance = levenshtein(s, str);
            if (distance < minDistance) {
                minDistance = distance;
            }
            if (minDistance == 0) break; // Early exit
        }
        return minDistance;
    }

    /**
     * Get list of all protected names (for debugging/admin purposes).
     */
    public static Set<String> getAllProtectedNames() {
        Set<String> all = new HashSet<>();
        all.addAll(BAD_ACTOR_NAMES);
        all.addAll(GOOD_CREATOR_NAMES);
        all.addAll(ALIASES.keySet());
        return Collections.unmodifiableSet(all);
    }

    /**
     * Get alias mapping (for debugging/admin purposes).
     */
    public static Map<String, String> getAliases() {
        return Collections.unmodifiableMap(ALIASES);
    }

    /**
     * Add a new protected name at runtime (admin only).
     */
    public static void addProtectedName(String name, boolean isBadActor) {
        String normalized = normalize(name);
        if (isBadActor) {
            ((Set<String>) BAD_ACTOR_NAMES).add(normalized);
        } else {
            ((Set<String>) GOOD_CREATOR_NAMES).add(normalized);
        }
    }

    /**
     * Add a new alias at runtime (admin only).
     */
    public static void addAlias(String realName, String creatorName) {
        ((Map<String, String>) ALIASES).put(normalize(realName), creatorName);
    }

    /**
     * Clear normalization cache (for testing).
     */
    public static void clearCache() {
        NORMALIZATION_CACHE.clear();
    }

    /**
     * Export whitelist for backup (admin only).
     * 
     * @return JSON-like string representation of whitelist
     */
    public static String exportWhitelist() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        for (Map.Entry<String, VerifiedAccount> entry : WHITELIST.entrySet()) {
            VerifiedAccount acc = entry.getValue();
            sb.append("  \"").append(entry.getKey()).append("\": {\n");
            sb.append("    \"channelName\": \"").append(acc.youtubeChannelName()).append("\",\n");
            sb.append("    \"minecraftUsername\": \"").append(acc.minecraftUsername()).append("\",\n");
            sb.append("    \"verificationDate\": ").append(acc.verificationDate()).append(",\n");
            sb.append("    \"isPartner\": ").append(acc.isPartner()).append("\n");
            sb.append("  },\n");
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Import whitelist from backup (admin only).
     * 
     * @param json JSON-like string representation of whitelist
     * @return Number of accounts imported
     */
    public static int importWhitelist(String json) {
        // Simple parser - in production use proper JSON library
        int count = 0;
        // Implementation would parse JSON and add accounts
        // This is a stub for the full implementation
        System.out.println("[CreatorIdentityFilter] Import whitelist called (stub)");
        return count;
    }
}
