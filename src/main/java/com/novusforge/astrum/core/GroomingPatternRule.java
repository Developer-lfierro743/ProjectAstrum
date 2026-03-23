package com.novusforge.astrum.core;

import java.util.regex.Pattern;
import com.novusforge.astrum.core.SafetyGuardian.*;

/**
 * GroomingPatternRule: Detects emotional manipulation and relationship escalation.
 * Targets the "grooming ladder" including secrecy, boundary testing, and leverage exploitation.
 */
final class GroomingPatternRule implements SafetyRule<ChatContext> {

    private static final Pattern EMOTIONAL_MANIPULATION = Pattern.compile(
        ".*(\\byou are (so )?mature for your age\\b|\\bno one understands you like i do\\b|\\byou're special to me\\b).*", Pattern.CASE_INSENSITIVE
    );

    private static final Pattern SECRECY_ENFORCEMENT = Pattern.compile(
        ".*(\\bdont tell (anyone|anybody)\\b|\\bjust between us\\b|\\bkeep this private\\b).*", Pattern.CASE_INSENSITIVE
    );

    private static final Pattern BOUNDARY_TESTING = Pattern.compile(
        ".*(\\bdo you have a (boyfriend|girlfriend)\\b|\\bhave you ever kissed\\b|\\bare you single\\b).*", Pattern.CASE_INSENSITIVE
    );

    private static final Pattern TRUST_EXPLOITATION = Pattern.compile(
        ".*(\\bhelp your youtube channel\\b|\\bmake you famous\\b|\\bgive you advice\\b|\\bgrow your following\\b).*", Pattern.CASE_INSENSITIVE
    );

    @Override public String name() { return "Grooming-Pattern-Detector"; }

    @Override public SafetyResult check(ChatContext context) {
        String msg = context.message();
        int signals = 0;

        if (EMOTIONAL_MANIPULATION.matcher(msg).find()) signals++;
        if (SECRECY_ENFORCEMENT.matcher(msg).find()) signals++;
        if (BOUNDARY_TESTING.matcher(msg).find()) signals++;
        if (TRUST_EXPLOITATION.matcher(msg).find()) signals++;

        if (signals >= 2) {
            return SafetyResult.BLOCK; // Multiple indicators = definite block
        } else if (signals == 1) {
            return SafetyResult.WARN; // Single indicator = warning for monitoring
        }

        return SafetyResult.ALLOW;
    }

    @Override public boolean isSupported(SafetyContext context) {
        return context instanceof ChatContext;
    }
}
