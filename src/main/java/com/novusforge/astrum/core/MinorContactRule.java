package com.novusforge.astrum.core;

import java.util.regex.Pattern;
import com.novusforge.astrum.core.SafetyGuardian.*;

/**
 * MinorContactRule: Fort Knox protection against grooming and solicitation.
 * Implements robust detection for predatory patterns based on community-documented incidents.
 */
final class MinorContactRule implements SafetyRule<ChatContext> {

    // BLOCK: Confirmed predatory probing and isolation attempts
    private static final Pattern AGE_PROBE = Pattern.compile(
        ".*(\\bhow old are you\\b|\\bare you a minor\\b|\\basl\\b|\\bage\\?).*", Pattern.CASE_INSENSITIVE
    );

    private static final Pattern ISOLATION = Pattern.compile(
        ".*(\\bdont tell your parents\\b|\\bkeep this between us\\b|\\bour secret\\b|\\bhome alone\\b).*", Pattern.CASE_INSENSITIVE
    );

    // WARN: Suspicious migration and incentive baiting
    private static final Pattern GIFT_BAIT = Pattern.compile(
        ".*(\\bi can give you\\b|\\bill buy you\\b|\\bfree (robux|vbucks|items|capes)).*", Pattern.CASE_INSENSITIVE
    );

    private static final Pattern MIGRATION = Pattern.compile(
        ".*(\\badd me on discord\\b|\\btext me privately\\b|\\bdm me\\b|\\bprivate message\\b).*", Pattern.CASE_INSENSITIVE
    );

    @Override public String name() { return "Minor-Contact-Guardian"; }

    @Override public SafetyResult check(ChatContext context) {
        String msg = context.message();

        // 1. Check for high-risk isolation/probing (BLOCK)
        if (ISOLATION.matcher(msg).find() || AGE_PROBE.matcher(msg).find()) {
            return SafetyResult.BLOCK;
        }

        // 2. Check for platform migration/baiting (WARN)
        if (MIGRATION.matcher(msg).find() || GIFT_BAIT.matcher(msg).find()) {
            return SafetyResult.WARN;
        }

        return SafetyResult.ALLOW;
    }

    @Override public boolean isSupported(SafetyContext context) {
        return context instanceof ChatContext;
    }
}
