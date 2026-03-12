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

import com.novusforge.astrum.security.audit.SecurityAudit;
import com.novusforge.astrum.security.guardian.rules.SexualRule;
import java.util.ArrayList;
import java.util.List;

/**
 * The SafetyGuardian system (The Formula Part 4).
 * Orchestrates all hardcoded safety rules to protect players.
 * Moved to Dedicated Security Module.
 */
public class SafetyGuardian {

    private static SafetyGuardian instance;
    private final List<SafetyRule> rules;

    private SafetyGuardian() {
        this.rules = new ArrayList<>();
        // Register all hardcoded rules here
        registerRule(new SexualRule());
    }

    public static SafetyGuardian getInstance() {
        if (instance == null) {
            instance = new SafetyGuardian();
        }
        return instance;
    }

    /**
     * Validates if the input string is safe according to ALL rules.
     * @param input The text input (chat, signs, names).
     * @return true if the input is safe to display.
     */
    public boolean validate(String input) {
        if (input == null) return true;
        
        for (SafetyRule rule : rules) {
            if (rule.isUnsafe(input)) {
                SecurityAudit.logViolation("GUARDIAN", rule.getRuleID(), "Text validation failure: " + input);
                return false;
            }
        }
        return true;
    }

    public void registerRule(SafetyRule rule) {
        rules.add(rule);
    }
}
