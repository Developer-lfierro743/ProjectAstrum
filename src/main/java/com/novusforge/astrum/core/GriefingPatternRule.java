package com.novusforge.astrum.core;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.novusforge.astrum.core.SafetyGuardian.*;

/**
 * GriefingPatternRule: Detects systematic destruction and coordinated infiltration.
 * Targets patterns used by organized groups like Team Avolition and The Fifth Column.
 */
final class GriefingPatternRule implements SafetyRule<ActionContext> {

    private static final int GRIEF_THRESHOLD = 50;
    private static final long GRIEF_WINDOW_MS = 30000;
    
    private static final long EXPLOSIVE_WINDOW_MS = 10000;
    private static final int EXPLOSIVE_WARN_THRESHOLD = 5;
    private static final int EXPLOSIVE_BLOCK_THRESHOLD = 10;

    private static final long NEW_PLAYER_THRESHOLD_MS = 24 * 60 * 60 * 1000; // 24 Hours

    // Tracking history per actor
    private final Map<String, Deque<Long>> destructionHistory = new ConcurrentHashMap<>();
    private final Map<String, Deque<Long>> explosiveHistory = new ConcurrentHashMap<>();
    private final Map<String, Long> joinTimes = new ConcurrentHashMap<>();

    @Override public String name() { return "Grief-Integrity-Monitor"; }

    @Override public SafetyResult check(ActionContext context) {
        String actorId = context.actor();
        String action = context.action().toUpperCase();
        long now = System.currentTimeMillis();

        // Ensure join time is tracked
        joinTimes.putIfAbsent(actorId, now);

        // 1. Detect Coordinated Attack Signal
        if ("COORDINATED_ATTACK".equals(action)) {
            return SafetyResult.BLOCK; // Explicit group attack signal
        }

        // 2. Mass Destruction Tracking
        if ("BLOCK_DESTROY".equals(action)) {
            // Check infiltration (new player destroying stuff)
            if (now - joinTimes.get(actorId) < NEW_PLAYER_THRESHOLD_MS) {
                // Suspicious for NOP (New On Project) player
                return SafetyResult.WARN;
            }

            if (checkWindow(destructionHistory, actorId, now, GRIEF_WINDOW_MS, GRIEF_THRESHOLD)) {
                return SafetyResult.BLOCK;
            }
        }

        // 3. Explosive Chain Detection
        if ("PLACE_EXPLOSIVE".equals(action)) {
            Deque<Long> window = explosiveHistory.computeIfAbsent(actorId, k -> new ArrayDeque<>());
            window.addLast(now);
            cleanWindow(window, now, EXPLOSIVE_WINDOW_MS);

            if (window.size() >= EXPLOSIVE_BLOCK_THRESHOLD) {
                return SafetyResult.BLOCK;
            } else if (window.size() >= EXPLOSIVE_WARN_THRESHOLD) {
                return SafetyResult.WARN;
            }
        }

        return SafetyResult.ALLOW;
    }

    private boolean checkWindow(Map<String, Deque<Long>> history, String actor, long now, long windowMs, int threshold) {
        Deque<Long> window = history.computeIfAbsent(actor, k -> new ArrayDeque<>());
        window.addLast(now);
        cleanWindow(window, now, windowMs);
        return window.size() >= threshold;
    }

    private void cleanWindow(Deque<Long> window, long now, long windowMs) {
        while (!window.isEmpty() && now - window.peekFirst() > windowMs) {
            window.removeFirst();
        }
    }

    @Override public boolean isSupported(SafetyContext context) {
        return context instanceof ActionContext;
    }
}
