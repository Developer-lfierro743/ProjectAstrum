package com.novusforge.astrum.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Utility for cryptographic hashing.
 * Uses Java 21 HexFormat for optimized hex string generation.
 */
public final class HashUtils {
    private HashUtils() {}

    /**
     * Computes a SHA-256 hash of the provided data.
     * @return Lowercase hex string representation of the hash.
     */
    public static String computeSHA256(byte[] data) {
        if (data == null) return "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            return HexFormat.of().withLowerCase().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not found in JVM", e);
        }
    }
}
