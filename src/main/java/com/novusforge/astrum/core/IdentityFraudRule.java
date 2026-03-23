package com.novusforge.astrum.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.novusforge.astrum.core.SafetyGuardian.*;

/**
 * IdentityFraudRule: Detects identity manipulation and faked personas.
 * Targets social engineering and trust exploitation via identity deception.
 */
final class IdentityFraudRule implements SafetyRule<ActionContext> {

    private final Map<String, PlayerProfile> profileRegistry = new ConcurrentHashMap<>();

    private static final Set<String> FRAUD_SIGNALS = Set.of(
        "AVOIDED_ANTICHEAT_SERVER",
        "PROFILE_MISMATCH",
        "COMMUNITY_DISPUTE"
    );

    @Override public String name() { return "Identity-Fraud-Monitor"; }

    @Override public SafetyResult check(ActionContext context) {
        String actorId = context.actor();
        String action = context.action().toUpperCase();

        if (!FRAUD_SIGNALS.contains(action)) {
            return SafetyResult.ALLOW;
        }

        PlayerProfile profile = profileRegistry.computeIfAbsent(actorId, k -> new PlayerProfile(actorId));
        profile.incrementFlags();
        profile.addSignature(action + "_" + System.currentTimeMillis());

        int flags = profile.getFlagCount();

        if (flags >= 3) {
            return SafetyResult.BLOCK; // Pattern of deception established
        } else if (flags >= 1) {
            return SafetyResult.WARN; // Initial signals detected
        }

        return SafetyResult.ALLOW;
    }

    @Override public boolean isSupported(SafetyContext context) {
        return context instanceof ActionContext;
    }

    private static class PlayerProfile {
        private final String registeredUsername;
        private final List<String> behaviorSignatures = new ArrayList<>();
        private int flagCount = 0;

        public PlayerProfile(String username) {
            this.registeredUsername = username;
        }

        public void incrementFlags() { this.flagCount++; }
        public int getFlagCount() { return flagCount; }
        public void addSignature(String sig) { behaviorSignatures.add(sig); }
    }
}
