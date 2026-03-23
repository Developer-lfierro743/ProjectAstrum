package com.novusforge.astrum.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.novusforge.astrum.core.SafetyGuardian.*;

/**
 * IGDRule: Internet Gaming Disorder / Digital Wellbeing Monitor.
 * Inspired by YouTube's "Take a Break" feature.
 * 
 * Non-punitive: Only issues WARN results with metadata for the UI.
 */
final class IGDRule implements SafetyRule<ActionContext> {

    private static final long HOUR_MS = 3600000;
    
    private final Map<String, Long> sessionStartTimes = new ConcurrentHashMap<>();

    @Override public String name() { return "Digital-Wellbeing-Monitor"; }

    @Override public SafetyResult check(ActionContext context) {
        String actorId = context.actor();
        long now = System.currentTimeMillis();

        // 1. Initialize session if new
        long startTime = sessionStartTimes.computeIfAbsent(actorId, k -> now);
        long elapsedMs = now - startTime;

        // 2. Calculate thresholds (non-punitive)
        if (elapsedMs >= 6 * HOUR_MS) {
            return new SafetyResult(SafetyResult.Decision.WARN, "IGD_REMINDER_6H_STRONG");
        } else if (elapsedMs >= 4 * HOUR_MS) {
            return new SafetyResult(SafetyResult.Decision.WARN, "IGD_REMINDER_4H");
        } else if (elapsedMs >= 2 * HOUR_MS) {
            return new SafetyResult(SafetyResult.Decision.WARN, "IGD_REMINDER_2H");
        }

        return SafetyResult.ALLOW;
    }

    @Override public boolean isSupported(SafetyContext context) {
        return context instanceof ActionContext;
    }
}
