package com.novusforge.astrum.core;

import java.util.*;

/**
 * CreatorIdentityFilter: Deterministic impersonation protection for Project Astrum.
 * Guards against identity theft of known Minecraft community figures.
 */
public final class CreatorIdentityFilter {

    public enum FilterSeverity { ALLOW, WARN, BLOCK }

    public record FilterResult(boolean allowed, String reason, FilterSeverity severity) {
        public static final FilterResult ALLOW = new FilterResult(true, "Username permitted", FilterSeverity.ALLOW);
    }

    private static final Set<String> BAD_ACTOR_NAMES = Set.of(
        "dream", "dreamwastaken", "dreamxd", "callmecarson", "carson", "lionmaker",
        "jinbop", "bashurverse", "wilbursoot", "wilbur", "skydoesminecraft", 
        "skydoesgaming", "adamdahlberg", "georgenotfound", "georgewbh", "skeppy",
        "iskall85", "iskall", "punz", "gerg", "marlowww", "dangermario",
        "popularmmos", "popularmmospat", "jschlatt", "schlatt", "lforlee", "lforleex"
    );

    private static final Set<String> GOOD_CREATOR_NAMES = Set.of(
        "tommyinnit", "tommy", "technoblade", "techno", "philza", "ph1lza", "tubbo",
        "ranboo", "sapnap", "badboyhalo", "bbh", "quackity", "nihachu", "niki",
        "jackmanifold", "jack", "karljacobs", "karl", "sword4000", "mindofneo",
        "neo", "nyxlunarii", "nyx", "sharkilz", "pharolen", "bobicraft", 
        "minecraftcurios", "ledgy", "knappyt", "knapp"
    );

    private static final Map<String, String> ALIASES = Map.of(
        "clay", "Dream",
        "clayhuff", "Dream",
        "marcuswilton", "LionMaker",
        "brandonasher", "Bashurverse",
        "zakahmed", "Skeppy",
        "adamdahlberg", "SkyDoesMinecraft",
        "georgedavidson", "GeorgeNotFound"
    );

    private CreatorIdentityFilter() {}

    public static FilterResult check(String username) {
        if (username == null || username.length() < 3) return FilterResult.ALLOW;

        // Layer 1: Exact Match (Normalized)
        String layer1 = username.toLowerCase().replaceAll("\\s+", "");
        if (BAD_ACTOR_NAMES.contains(layer1)) return new FilterResult(false, "Bad actor identity blocked", FilterSeverity.BLOCK);
        if (GOOD_CREATOR_NAMES.contains(layer1)) return new FilterResult(false, "Creator impersonation blocked", FilterSeverity.BLOCK);
        if (ALIASES.containsKey(layer1)) return new FilterResult(false, "Identity blocked (Alias of " + ALIASES.get(layer1) + ")", FilterSeverity.BLOCK);

        // Layer 2: Fuzzy Match (Leet Speak)
        String layer2 = layer1.replaceAll("0", "o")
                              .replaceAll("1", "i") // Rule says i or l, we'll check against i normalized
                              .replaceAll("3", "e")
                              .replaceAll("4", "a")
                              .replaceAll("5", "s")
                              .replaceAll("7", "t")
                              .replaceAll("@", "a")
                              .replaceAll("[^a-z0-9]", "");
        
        if (BAD_ACTOR_NAMES.contains(layer2) || GOOD_CREATOR_NAMES.contains(layer2) || ALIASES.containsKey(layer2)) {
            return new FilterResult(false, "Obfuscated identity detected", FilterSeverity.BLOCK);
        }

        // Layer 3: Substring Match
        for (String protectedName : BAD_ACTOR_NAMES) {
            if (layer1.contains(protectedName)) return new FilterResult(true, "Username contains protected creator name", FilterSeverity.WARN);
        }
        for (String protectedName : GOOD_CREATOR_NAMES) {
            if (layer1.contains(protectedName)) return new FilterResult(true, "Username contains protected creator name", FilterSeverity.WARN);
        }

        // Layer 4: Levenshtein Distance
        if (username.length() < 15) {
            for (String protectedName : BAD_ACTOR_NAMES) {
                if (levenshtein(layer1, protectedName) <= 2) return new FilterResult(true, "Username too similar to protected creator name", FilterSeverity.WARN);
            }
            for (String protectedName : GOOD_CREATOR_NAMES) {
                if (levenshtein(layer1, protectedName) <= 2) return new FilterResult(true, "Username too similar to protected creator name", FilterSeverity.WARN);
            }
        }

        return FilterResult.ALLOW;
    }

    public static boolean isBadActor(String username) {
        String normalized = username.toLowerCase().replaceAll("\\s+", "");
        return BAD_ACTOR_NAMES.contains(normalized) || ALIASES.containsKey(normalized);
    }

    private static int levenshtein(String s1, String s2) {
        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }
}
