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
package com.novusforge.astrum.security.guardian;

import com.novusforge.astrum.api.ModMetadata;
import com.novusforge.astrum.security.audit.SecurityAudit;

/**
 * Scans assets and mod metadata for safety violations.
 */
public class AssetScanner {

    private final SafetyGuardian guardian;

    public AssetScanner(SafetyGuardian guardian) {
        this.guardian = guardian;
    }

    /**
     * Scans mod metadata (ID, Name) for violations.
     * @param meta The mod metadata to scan.
     * @return true if the metadata is safe.
     */
    public boolean scanModMetadata(ModMetadata meta) {
        if (!guardian.validate(meta.id())) {
            SecurityAudit.logViolation("INTEGRITY", "METADATA_SPOOFING", 
                "Mod ID contains unsafe content: " + meta.id());
            return false;
        }
        if (!guardian.validate(meta.name())) {
            SecurityAudit.logViolation("INTEGRITY", "METADATA_SPOOFING", 
                "Mod Name contains unsafe content: " + meta.name());
            return false;
        }
        return true;
    }

    /**
     * Scans arbitrary asset text (e.g. from resource packs).
     */
    public boolean scanAssetText(String text, String assetPath) {
        if (!guardian.validate(text)) {
            SecurityAudit.logViolation("GUARDIAN", "ASSET_METADATA_INJECTION", 
                "Unsafe content detected in asset: " + assetPath);
            return false;
        }
        return true;
    }
}
