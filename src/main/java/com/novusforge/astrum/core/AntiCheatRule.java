package com.novusforge.astrum.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.novusforge.astrum.core.SafetyGuardian.*;

/**
 * AntiCheatRule: Detects macros and automated input patterns.
 * Focuses on inhuman timing consistency and reaction speed violations.
 */
final class AntiCheatRule implements SafetyRule<ActionContext> {

    private static final int CONSISTENCY_SAMPLE_SIZE = 10;
    private static final long HUMAN_REACTION_FLOOR_MS = 80;
    private static final long VARIANCE_THRESHOLD_MS = 2;

    // Track last N action intervals per player (Actor ID -> Queue of Intervals)
    private final Map<String, Deque<Long>> actionHistory = new ConcurrentHashMap<>();
    private final Map<String, Long> lastActionTime = new ConcurrentHashMap<>();

    @Override public String name() { return "Anti-Cheat-Monitor"; }

    @Override public SafetyResult check(ActionContext context) {
        String actorId = context.actor();
        long now = System.currentTimeMillis();
        
        Long lastTime = lastActionTime.get(actorId);
        lastActionTime.put(actorId, now);

        if (lastTime == null) return SafetyResult.ALLOW;

        long interval = now - lastTime;
        
        // 1. Detect impossible reaction speed
        if (interval < HUMAN_REACTION_FLOOR_MS) {
            return SafetyResult.WARN; // Extreme speed detected
        }

        // 2. Track intervals for consistency analysis
        Deque<Long> intervals = actionHistory.computeIfAbsent(actorId, k -> new ArrayDeque<>());
        intervals.addLast(interval);
        if (intervals.size() > CONSISTENCY_SAMPLE_SIZE) {
            intervals.removeFirst();
        }

        if (intervals.size() == CONSISTENCY_SAMPLE_SIZE) {
            return analyzeConsistency(intervals);
        }

        return SafetyResult.ALLOW;
    }

    private SafetyResult analyzeConsistency(Deque<Long> intervals) {
        List<Long> list = new ArrayList<>(intervals);
        int consistentPairs = 0;

        for (int i = 0; i < list.size() - 1; i++) {
            long diff = Math.abs(list.get(i) - list.get(i + 1));
            if (diff <= VARIANCE_THRESHOLD_MS) {
                consistentPairs++;
            }
        }

        double ratio = (double) consistentPairs / (CONSISTENCY_SAMPLE_SIZE - 1);

        if (ratio >= 0.90) {
            return SafetyResult.BLOCK; // Inhuman consistency (Macros)
        } else if (ratio >= 0.70) {
            return SafetyResult.WARN; // Highly suspicious pattern
        }

        return SafetyResult.ALLOW;
    }

    @Override public boolean isSupported(SafetyContext context) {
        return context instanceof ActionContext;
    }
}
