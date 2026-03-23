package com.novusforge.astrum.core;

import java.util.*;
import java.util.regex.Pattern;
import com.novusforge.astrum.core.SafetyGuardian.*;

/**
 * SexualRule: The primary CSAM and adult content gate for Project Astrum.
 * Covers Chat, Content, and Mod contexts with zero-tolerance for CSAM.
 */
final class SexualRule implements SafetyRule<SafetyContext> {

    // --- Patterns & Blacklists ---

    // Expanded NSFW patterns including leet variants
    private static final Pattern NSFW_PATTERN = Pattern.compile(
        ".*(j[e3]n+y|n[u0]de|n[s5]fw|adu[l1]t|expl[i1]c[i1]t|xxx|p[o0]rn|h[e3]nta[i1]|r34|sexting).*", 
        Pattern.CASE_INSENSITIVE
    );

    // Explicit sexual language patterns for Chat
    private static final Pattern EXPLICIT_CHAT_PATTERN = Pattern.compile(
        ".*(\\bsexy\\b|\\bhot minor\\b|\\bshow me\\b|\\bsend pics\\b|\\bprivate show\\b|\\bnudes\\b).*",
        Pattern.CASE_INSENSITIVE
    );

    // CSAM hash blacklist (Placeholder - to be populated by Safety Team)
    private static final Set<String> CSAM_HASHES = Set.of(
        "aff01234567890abcdef01234567890abcdef01234567890abcdef012345678",
        "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855" // Sample empty file hash
    );

    @Override public String name() { return "CSAM-Adult-Integrity-Gate"; }

    @Override public SafetyResult check(SafetyContext context) {
        if (context instanceof ChatContext chat) {
            return checkChat(chat);
        } else if (context instanceof DataContext data) {
            return checkData(data);
        }
        return SafetyResult.ALLOW;
    }

    private SafetyResult checkChat(ChatContext chat) {
        String msg = chat.message();
        
        if (EXPLICIT_CHAT_PATTERN.matcher(msg).find()) {
            return SafetyResult.BLOCK; // Explicit sexual solicitation/language
        }
        
        if (NSFW_PATTERN.matcher(msg).find()) {
            return SafetyResult.WARN; // Suspicious terminology
        }

        return SafetyResult.ALLOW;
    }

    private SafetyResult checkData(DataContext data) {
        // 1. Hash Check (CSAM Integrity)
        String actualHash = HashUtils.computeSHA256(data.data());
        if (CSAM_HASHES.contains(actualHash)) {
            return SafetyResult.BLOCK; // Immediate BLOCK for known illegal/prohibited content
        }

        // 2. Identifier Pattern Check (Filenames/IDs)
        if (NSFW_PATTERN.matcher(data.identifier()).find()) {
            return SafetyResult.BLOCK; // Confirmed NSFW assets/mods
        }

        // 3. Metadata Scan (Only for ModContext)
        if (data instanceof ModContext mod) {
            for (String val : mod.metadata().values()) {
                if (NSFW_PATTERN.matcher(val).find()) {
                    return SafetyResult.BLOCK;
                }
            }
        }

        return SafetyResult.ALLOW;
    }

    @Override public boolean isSupported(SafetyContext context) {
        return context instanceof ChatContext || context instanceof DataContext;
    }
}
