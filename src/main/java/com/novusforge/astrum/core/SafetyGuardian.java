package com.novusforge.astrum.core;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * SafetyGuardian: The hardcoded "Fort Knox" guardian of Project Astrum.
 * "A reclaim of the sandbox vision. Independent, Resilient, and Secure."
 */
public final class SafetyGuardian {
    private static final Logger LOGGER = Logger.getLogger("SafetyGuardian");

    /**
     * SafetyResult: Carries the decision and the metadata/reason for that decision.
     */
    public record SafetyResult(Decision decision, String reason) {
        public enum Decision { ALLOW, WARN, BLOCK }
        
        public static final SafetyResult ALLOW = new SafetyResult(Decision.ALLOW, "Clean");
        public static final SafetyResult WARN = new SafetyResult(Decision.WARN, "Suspicious");
        public static final SafetyResult BLOCK = new SafetyResult(Decision.BLOCK, "Forbidden");
    }

    // --- Typed Contexts ---

    public sealed interface SafetyContext permits ChatContext, ActionContext, DataContext {
        String identifier();
    }

    public sealed interface DataContext extends SafetyContext permits ModContext, ContentContext {
        byte[] data();
    }

    public record ChatContext(String playerId, String message) implements SafetyContext {
        @Override public String identifier() { return "Player:" + playerId; }
    }

    public record ModContext(String modId, String loader, byte[] data, Map<String, String> metadata) implements DataContext {
        @Override public String identifier() { return "Mod:" + modId + " (" + loader + ")"; }
    }

    public record ActionContext(String action, String actor, String target) implements SafetyContext {
        @Override public String identifier() { return "Action:" + action + " by " + actor + " on " + target; }
    }

    public record ContentContext(String type, String assetName, byte[] data) implements DataContext {
        @Override public String identifier() { return "Asset:" + assetName + " [" + type + "]"; }
    }

    // --- Guardian Engine Logic ---

    private final List<SafetyRule<?>> rules = new ArrayList<>();

    public SafetyGuardian() {
        rules.add(new FileIntegrityRule());   // 1st: FileIntegrityRule (Existing)
        rules.add(new SexualRule());          // 2nd: SexualRule (CSAM Gate)
        rules.add(new MinorContactRule());    // 3rd: MinorContactRule
        rules.add(new GroomingPatternRule()); // 4th: GroomingPatternRule
        rules.add(new NativeModRule());       // 5th: NativeModRule (Existing)
        rules.add(new AntiCheatRule());       // 6th: AntiCheatRule
        rules.add(new IdentityFraudRule());   // 7th: IdentityFraudRule
        rules.add(new GriefingPatternRule()); // 8th: GriefingPatternRule
        rules.add(new HarassmentRule());      // 9th: HarassmentRule
        rules.add(new ChatGuardianRule());    // 10th: ChatGuardianRule (Existing)
        rules.add(new IGDRule());             // 11th: IGDRule (Wellbeing Only)
    }

    public SafetyResult validate(SafetyContext context) {
        SafetyResult worstCase = SafetyResult.ALLOW;

        for (var rule : rules) {
            SafetyResult result = rule.evaluateUnsafe(context);
            if (result.decision().ordinal() > worstCase.decision().ordinal()) {
                worstCase = result;
                logViolation(rule.name(), context, result);
                if (worstCase.decision() == SafetyResult.Decision.BLOCK) break;
            }
        }
        return worstCase;
    }

    public CompletableFuture<SafetyResult> validateAsync(SafetyContext context) {
        return CompletableFuture.supplyAsync(() -> validate(context));
    }

    private void logViolation(String rule, SafetyContext ctx, SafetyResult res) {
        Level level = (res.decision() == SafetyResult.Decision.BLOCK) ? Level.SEVERE : Level.WARNING;
        LOGGER.log(level, "[FORT-KNOX] {0} -> {1} ({2}) triggered by {3}", 
            new Object[]{res.decision(), rule, res.reason(), ctx.identifier()});
    }

    interface SafetyRule<T extends SafetyContext> {
        String name();
        SafetyResult check(T context);
        
        @SuppressWarnings("unchecked")
        default SafetyResult evaluateUnsafe(SafetyContext context) {
            if (isSupported(context)) {
                return check((T) context);
            }
            return SafetyResult.ALLOW;
        }

        default boolean isSupported(SafetyContext context) {
            try {
                T casted = (T) context;
                return true;
            } catch (ClassCastException e) {
                return false;
            }
        }
    }

    /**
     * FileIntegrityRule: Global hash/pattern check for all binary data.
     */
    private static final class FileIntegrityRule implements SafetyRule<DataContext> {
        private static final Set<String> BLACKLISTED_CHECKSUMS = Set.of(
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", 
            "aff01234567890abcdef01234567890abcdef01234567890abcdef012345678"
        );

        private static final Pattern HARMFUL_ASSET_PATTERN = Pattern.compile(
            ".*(j[e3]n+y|n[u0]de|nsfw|adult).*", Pattern.CASE_INSENSITIVE
        );

        @Override public String name() { return "Global-Integrity-Gate"; }
        @Override public SafetyResult check(DataContext context) {
            String actualHash = HashUtils.computeSHA256(context.data());
            if (BLACKLISTED_CHECKSUMS.contains(actualHash)) return SafetyResult.BLOCK;
            if (HARMFUL_ASSET_PATTERN.matcher(context.identifier()).matches()) return SafetyResult.BLOCK;
            return SafetyResult.ALLOW;
        }

        @Override public boolean isSupported(SafetyContext context) {
            return context instanceof DataContext;
        }
    }

    /**
     * NativeModRule: Checks specifically for mod JAR bytecode integrity.
     */
    private static final class NativeModRule implements SafetyRule<ModContext> {
        @Override public String name() { return "Mod-Bytecode-Verifier"; }
        @Override public SafetyResult check(ModContext context) {
            String loader = context.loader().toLowerCase();
            if (loader.contains("forge") || loader.contains("fabric") || loader.contains("quilt")) {
                return SafetyResult.BLOCK;
            }

            BytecodeScanner.ScanResult scan = BytecodeScanner.scan(context.data());
            if (!scan.isSafe()) {
                return new SafetyResult(SafetyResult.Decision.BLOCK, scan.violation());
            }

            return SafetyResult.ALLOW;
        }

        @Override public boolean isSupported(SafetyContext context) {
            return context instanceof ModContext;
        }
    }

    /**
     * ChatGuardianRule: Basic pattern matching for ethics and safety.
     */
    private static final class ChatGuardianRule implements SafetyRule<ChatContext> {
        private static final Pattern GROOMING_PATTERN = Pattern.compile(
            ".*(sus-phrase|bad-pattern|[s5]u[s5]).*", Pattern.CASE_INSENSITIVE
        );

        @Override public String name() { return "Ethics-Engine"; }
        @Override public SafetyResult check(ChatContext context) {
            if (GROOMING_PATTERN.matcher(context.message()).matches()) return SafetyResult.BLOCK;
            return SafetyResult.ALLOW;
        }

        @Override public boolean isSupported(SafetyContext context) {
            return context instanceof ChatContext;
        }
    }
}
