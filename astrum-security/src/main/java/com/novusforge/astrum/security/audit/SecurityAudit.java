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
package com.novusforge.astrum.security.audit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Hardcoded, immutable security logging (SafetyGuardian Part 2).
 * Records all safety violations for audit trails.
 */
public class SecurityAudit {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Records a security violation to the standard output and future immutable file.
     * @param category The security category (e.g., "GUARDIAN", "EXPLOIT")
     * @param ruleID The ID of the rule that was triggered.
     * @param details Additional context about the violation.
     */
    public static void logViolation(String category, String ruleID, String details) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logEntry = String.format("[%s] [SECURITY-%s] Rule: %s | Info: %s", 
                timestamp, category, ruleID, details);
        
        // Hardcoded output to stderr for immediate visibility in logs.
        System.err.println(logEntry);
        
        // Future: Integration with an encrypted file-based logger.
    }
}
