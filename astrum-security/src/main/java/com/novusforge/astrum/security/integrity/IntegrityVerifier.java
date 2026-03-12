/*
 * Copyright (c) 2026 NovusForge Project Astrum. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.novusforge.astrum.security.integrity;

import java.security.MessageDigest;
import java.util.HexFormat;

/**
 * Validates Marketplace and Resource integrity using SHA-256 (Ethics Mandate).
 * No malicious mods allowed in Project Astrum.
 */
public class IntegrityVerifier {

    private static final HexFormat HEX = HexFormat.of();

    /**
     * Verifies if a file's hash matches the signed marketplace signature.
     */
    public static boolean verifyAsset(byte[] data, String expectedHash) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            String actualHash = HEX.formatHex(hash);
            return actualHash.equalsIgnoreCase(expectedHash);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifies the cryptographic signature of a mod (Ethics Mandate).
     * @param modData The raw byte data of the mod.
     * @param signature The hex-encoded RSA/ECDSA signature.
     * @return true if the signature is valid.
     */
    public static boolean verifyModSignature(byte[] modData, String signature) {
        // Future: Implement actual RSA signature verification with NovusForge Public Key
        // For Indev: Require a hardcoded "OFFICIAL_MOD" signature for testing.
        return "OFFICIAL_MOD_SIG".equalsIgnoreCase(signature);
    }
}
