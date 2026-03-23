package com.novusforge.astrum.core;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.*;

/**
 * FortKnoxTest: A "dummy script" to verify the SafetyGuardian and ModLoader.
 * Demonstrates blocking of hacks, blacklisted hashes, and malicious bytecode.
 */
public class FortKnoxTest {

    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(60));
        System.out.println("   PROJECT ASTRUM - FORT KNOX SECURITY TEST");
        System.out.println("=".repeat(60));

        SafetyGuardian guardian = new SafetyGuardian();
        ModLoader loader = new ModLoader(guardian);

        // 1. Create a "Clean" Mod
        byte[] cleanMod = createDummyMod("CleanMod.class", "public class CleanMod {}");
        testMod(loader, guardian, "CleanMod", cleanMod, "No metadata");

        // 2. Create a "Malicious" Mod (Contains reflection hack)
        byte[] hackMod = createDummyMod("HackMod.class", "import java/lang/reflect/Field;");
        testMod(loader, guardian, "HackMod", hackMod, "Contains java/lang/reflect");

        // 3. Create a "Blacklisted" Mod (Matches a blocked SHA-256 hash)
        // Note: HashUtils.computeSHA256(new byte[0]) is "e3b0c442..." which we blacklisted earlier
        byte[] blacklistedMod = new byte[0]; 
        testMod(loader, guardian, "EmptyBlacklistedMod", blacklistedMod, "Matches blacklisted empty file hash");

        // 4. Create a "NSFW Name" Mod (Bypass attempt)
        byte[] nsfwNameMod = createDummyMod("test.class", "OK");
        testContent(guardian, "j3nny_texture.png", nsfwNameMod, "Obfuscated name bypass attempt");

        // 5. Test Minor Contact Protection (Grooming detection)
        testChat(guardian, "User123", "hey how old are you?", "Age probing (BLOCK)");
        testChat(guardian, "Stranger", "lets keep this between us our secret", "Isolation attempt (BLOCK)");
        testChat(guardian, "Baiter", "i can give you free robux", "Gift baiting (WARN)");
        testChat(guardian, "Migration", "add me on discord or dm me", "Platform migration (WARN)");

        // 6. Test Grooming Pattern Escalation
        testChat(guardian, "Manipulator", "you are so mature for your age", "Emotional manipulation (WARN)");
        testChat(guardian, "Predator", "you are mature for your age just between us", "Multi-signal grooming (BLOCK)");
        testChat(guardian, "CloutChaser", "i can help your youtube channel", "Leverage exploitation (WARN)");

        // 7. Test Anti-Cheat Macro Detection
        System.out.println("\n[TEST] Simulating human behavior (ALLOW)");
        for(int i=0; i<12; i++) {
            guardian.validate(new SafetyGuardian.ActionContext("click", "LegitPlayer", "block"));
            Thread.sleep(100 + new Random().nextInt(50));
        }
        System.out.println("[RESULT] Human simulation complete.");

        System.out.println("\n[TEST] Simulating Macro (BLOCK)");
        SafetyGuardian.SafetyResult lastRes = SafetyGuardian.SafetyResult.ALLOW;
        for(int i=0; i<12; i++) {
            lastRes = guardian.validate(new SafetyGuardian.ActionContext("click", "MacroUser", "block"));
            Thread.sleep(50); // Constant 50ms interval
        }
        System.out.println("[RESULT] Macro Detection: " + lastRes);

        System.out.println("\n[TEST] Simulating Autoclicker (WARN)");
        for(int i=0; i<5; i++) {
            lastRes = guardian.validate(new SafetyGuardian.ActionContext("click", "FastClicker", "block"));
            Thread.sleep(10); // Impossible 10ms interval
        }
        System.out.println("[RESULT] Autoclicker Detection: " + lastRes);

        // 8. Test Identity Fraud Detection
        System.out.println("\n[TEST] Initial Fraud Signal (WARN)");
        lastRes = guardian.validate(new SafetyGuardian.ActionContext("PROFILE_MISMATCH", "SusActor", "system"));
        System.out.println("[RESULT] Single Signal: " + lastRes.decision());

        System.out.println("\n[TEST] Accumulated Fraud Pattern (BLOCK)");
        guardian.validate(new SafetyGuardian.ActionContext("AVOIDED_ANTICHEAT_SERVER", "Fraudster", "system"));
        guardian.validate(new SafetyGuardian.ActionContext("COMMUNITY_DISPUTE", "Fraudster", "system"));
        lastRes = guardian.validate(new SafetyGuardian.ActionContext("PROFILE_MISMATCH", "Fraudster", "system"));
        System.out.println("[RESULT] Triple Signal: " + lastRes.decision());

        // 9. Test Griefing Pattern Detection
        System.out.println("\n[TEST] Coordinated Attack (BLOCK)");
        lastRes = guardian.validate(new SafetyGuardian.ActionContext("COORDINATED_ATTACK", "Griefer1", "spawn_castle"));
        System.out.println("[RESULT] Attack Signal: " + lastRes.decision());

        System.out.println("\n[TEST] Explosive Chain (WARN -> BLOCK)");
        for(int i=0; i<6; i++) {
            lastRes = guardian.validate(new SafetyGuardian.ActionContext("PLACE_EXPLOSIVE", "TntSpammer", "ground"));
        }
        System.out.println("[RESULT] 6 Explosives: " + lastRes.decision());
        for(int i=0; i<5; i++) {
            lastRes = guardian.validate(new SafetyGuardian.ActionContext("PLACE_EXPLOSIVE", "TntSpammer", "ground"));
        }
        System.out.println("[RESULT] 11 Explosives: " + lastRes.decision());

        System.out.println("\n[TEST] Mass Destruction (BLOCK)");
        for(int i=0; i<55; i++) {
            lastRes = guardian.validate(new SafetyGuardian.ActionContext("BLOCK_DESTROY", "Destroyer", "city"));
        }
        System.out.println("[RESULT] 55 Blocks: " + lastRes.decision());

        // 10. Test Harassment and Threat Detection
        System.out.println("\n[TEST] Direct Threat (BLOCK)");
        lastRes = guardian.validate(new SafetyGuardian.ChatContext("Threatener", "i will find you and kill you"));
        System.out.println("[RESULT] Threat: " + lastRes.decision());

        System.out.println("\n[TEST] Targeted Harassment (WARN)");
        for(int i=0; i<11; i++) {
            lastRes = guardian.validate(new SafetyGuardian.ChatContext("Harasser", "@Victim stop playing the game"));
        }
        System.out.println("[RESULT] 11 Targeted Messages: " + lastRes.decision());

        System.out.println("=".repeat(60));
        System.out.println("TEST COMPLETED");
    }

    private static void testMod(ModLoader loader, SafetyGuardian guardian, String id, byte[] data, String description) {
        System.out.println("\n[TEST] Loading: " + id + " (" + description + ")");
        SafetyGuardian.ModContext ctx = new SafetyGuardian.ModContext(id, "astrum-native", data, Map.of());
        SafetyGuardian.SafetyResult result = guardian.validate(ctx);
        System.out.println("[RESULT] Guardian Decision: " + result.decision());
    }

    private static void testContent(SafetyGuardian guardian, String name, byte[] data, String description) {
        System.out.println("\n[TEST] Asset: " + name + " (" + description + ")");
        SafetyGuardian.ContentContext ctx = new SafetyGuardian.ContentContext("texture", name, data);
        SafetyGuardian.SafetyResult result = guardian.validate(ctx);
        System.out.println("[RESULT] Guardian Decision: " + result.decision());
    }

    private static void testChat(SafetyGuardian guardian, String player, String message, String description) {
        System.out.println("\n[TEST] Chat: \"" + message + "\" (" + description + ")");
        SafetyGuardian.ChatContext ctx = new SafetyGuardian.ChatContext(player, message);
        SafetyGuardian.SafetyResult result = guardian.validate(ctx);
        System.out.println("[RESULT] Guardian Decision: " + result.decision());
    }

    private static byte[] createDummyMod(String entryName, String content) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry entry = new ZipEntry(entryName);
            zos.putNextEntry(entry);
            zos.write(content.getBytes(StandardCharsets.ISO_8859_1));
            zos.closeEntry();
        }
        return baos.toByteArray();
    }
}
