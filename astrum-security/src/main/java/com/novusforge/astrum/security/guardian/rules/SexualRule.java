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
package com.novusforge.astrum.security.guardian.rules;

import com.novusforge.astrum.security.guardian.SafetyRule;
import java.util.regex.Pattern;

/**
 * Hardcoded rule to prevent sexual content and grooming (SafetyGuardian Part 1).
 * Use efficient, non-bypassable regex for content filtering.
 * Refactored for 2026-era patterns in Dedicated Security Module.
 */
public class SexualRule implements SafetyRule {

    // Simple regex pattern to block known harmful terms (can be expanded)
    private static final Pattern SEXUAL_CONTENT_PATTERN = Pattern.compile(
        "\\b(sexting|nsfw|sex|sexual|porn|nude|horny|adult|onlyfans|fansly|of|vids|cam)\\b",
        Pattern.CASE_INSENSITIVE
    );

    @Override
    public boolean isUnsafe(String input) {
        if (input == null || input.isEmpty()) return false;
        
        // Match against the sexual content pattern
        return SEXUAL_CONTENT_PATTERN.matcher(input).find();
    }

    @Override
    public String getRuleID() {
        return "SEXUAL_RULE_001";
    }
}
