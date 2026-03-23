package com.novusforge.astrum.core;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * BytecodeScanner: Static analysis for "Fort Knox" mod security.
 * Inspects mod JARs for forbidden method calls and package imports.
 */
public final class BytecodeScanner {

    // Forbidden patterns that indicate hacks or malware
    private static final List<String> FORBIDDEN_STRINGS = List.of(
        "java/lang/reflect", 
        "java/lang/instrument",
        "java/net/URLClassLoader",
        "sun/misc/Unsafe",
        "java/lang/Runtime;->exec",
        "java/lang/System;->exit"
    );

    public record ScanResult(boolean isSafe, String violation) {}

    /**
     * Scans a mod file for security violations.
     */
    public static ScanResult scan(byte[] modData) {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(modData))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".class")) {
                    byte[] classBytes = zis.readAllBytes();
                    String content = new String(classBytes, java.nio.charset.StandardCharsets.ISO_8859_1);
                    
                    for (String forbidden : FORBIDDEN_STRINGS) {
                        if (content.contains(forbidden)) {
                            return new ScanResult(false, "Forbidden code pattern detected: " + forbidden);
                        }
                    }
                }
            }
        } catch (IOException e) {
            return new ScanResult(false, "Corrupt or invalid mod file structure.");
        }
        return new ScanResult(true, "Clean");
    }
}
