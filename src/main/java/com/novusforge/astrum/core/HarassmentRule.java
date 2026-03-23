package com.novusforge.astrum.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import com.novusforge.astrum.core.SafetyGuardian.*;

/**
 * HarassmentRule: Detects racial slurs, direct threats, and targeted harassment.
 * Uses cryptographic hashing for slurs to avoid storing explicit language in source.
 */
final class HarassmentRule implements SafetyRule<ChatContext> {

    // SHA-256 hashes of lowercase normalized slur strings
    private static final Set<String> SLUR_HASHES = Set.of(
        "da39a3ee5e6b4b0d3255bfef95601890afd80709", // Example Hash Placeholder
        "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
    );

    private static final Pattern THREAT_PATTERN = Pattern.compile(
        ".*(\\bi will find you\\b|\\bi know where you live\\b|\\bswat\\b|\\bkill you\\b).*", 
        Pattern.CASE_INSENSITIVE
    );

    private static final int HARASSMENT_THRESHOLD = 10;
    private static final long HARASSMENT_WINDOW_MS = 60000;

    // Track sender -> (target -> timestamp history)
    private final Map<String, Map<String, Deque<Long>>> harassmentHistory = new ConcurrentHashMap<>();

    @Override public String name() { return "Harassment-HateSpeech-Gate"; }

    @Override public SafetyResult check(ChatContext context) {
        String msg = context.message();
        String normalized = normalize(msg);

        // 1. Slur Detection (Cryptographic match)
        for (String word : normalized.split("\\s+")) {
            if (SLUR_HASHES.contains(HashUtils.computeSHA256(word.getBytes(java.nio.charset.StandardCharsets.UTF_8)))) {
                return SafetyResult.BLOCK; // Hate speech is an immediate block
            }
        }

        // 2. Direct Threats
        if (THREAT_PATTERN.matcher(msg).find()) {
            return SafetyResult.BLOCK; // Threats to physical safety
        }

        // 3. Targeted Harassment Tracking
        String target = findTarget(msg);
        if (target != null) {
            if (checkHarassment(context.playerId(), target)) {
                return SafetyResult.WARN; // High frequency targeting
            }
        }

        return SafetyResult.ALLOW;
    }

    private String normalize(String input) {
        if (input == null) return "";
        return input.toLowerCase()
            .replaceAll("[0o]", "o")
            .replaceAll("[1il]", "i")
            .replaceAll("[3e]", "e")
            .replaceAll("[4a@]", "a")
            .replaceAll("[5s]", "s")
            .replaceAll("[7t]", "t")
            .replaceAll("[^a-z\\s]", ""); // Remove punctuation/asterisks
    }

    private String findTarget(String msg) {
        if (msg.startsWith("@")) {
            int space = msg.indexOf(" ");
            return space != -1 ? msg.substring(1, space) : msg.substring(1);
        }
        if (msg.toLowerCase().startsWith("/msg ") || msg.toLowerCase().startsWith("/tell ")) {
            String[] parts = msg.split("\\s+");
            if (parts.length >= 2) return parts[1];
        }
        return null;
    }

    private boolean checkHarassment(String sender, String target) {
        long now = System.currentTimeMillis();
        Map<String, Deque<Long>> targets = harassmentHistory.computeIfAbsent(sender, k -> new ConcurrentHashMap<>());
        Deque<Long> window = targets.computeIfAbsent(target, k -> new ArrayDeque<>());
        
        window.addLast(now);
        while (!window.isEmpty() && now - window.peekFirst() > HARASSMENT_WINDOW_MS) {
            window.removeFirst();
        }
        
        return window.size() >= HARASSMENT_THRESHOLD;
    }

    @Override public boolean isSupported(SafetyContext context) {
        return context instanceof ChatContext;
    }
}
