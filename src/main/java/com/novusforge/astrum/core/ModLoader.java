package com.novusforge.astrum.core;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * ModLoader: The secure gateway for loading "Native Mods".
 * Enforces SafetyGuardian validation before any code is loaded into the JVM.
 */
public final class ModLoader {
    private final SafetyGuardian guardian;
    private final Map<String, byte[]> loadedMods = new HashMap<>();

    public ModLoader(SafetyGuardian guardian) {
        this.guardian = guardian;
    }

    /**
     * Attempts to load a mod from a file path.
     */
    public void loadMod(Path path) throws IOException {
        byte[] data = Files.readAllBytes(path);
        String modId = path.getFileName().toString();

        SafetyGuardian.ModContext context = new SafetyGuardian.ModContext(
            modId, "astrum-native", data, Map.of("path", path.toString())
        );

        if (guardian.validate(context) == SafetyGuardian.SafetyResult.BLOCK) {
            System.err.println("[ModLoader] SECURITY BLOCK: Mod " + modId + " failed safety validation.");
            return;
        }

        // Only add to JVM-ready collection if ALLOWED
        loadedMods.put(modId, data);
        System.out.println("[ModLoader] Mod " + modId + " verified and loaded.");
    }
}
