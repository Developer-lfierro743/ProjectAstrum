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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the refactored SafetyGuardian in the Dedicated Security Module.
 */
public class SafetyGuardianTest {

    @Test
    public void testSafeInput() {
        SafetyGuardian guardian = SafetyGuardian.getInstance();
        assertTrue(guardian.validate("Hello world, let's build something!"), "Should allow safe input");
    }

    @Test
    public void testUnsafeSexualInput() {
        SafetyGuardian guardian = SafetyGuardian.getInstance();
        assertFalse(guardian.validate("Check out my porn site!"), "Should block sexual content");
        assertFalse(guardian.validate("I am looking for an adult to talk to."), "Should block 'adult' keyword");
        assertFalse(guardian.validate("visit my onlyfans"), "Should block 'onlyfans'");
    }

    @Test
    public void testCaseInsensitivity() {
        SafetyGuardian guardian = SafetyGuardian.getInstance();
        assertFalse(guardian.validate("ADULT content"), "Should block uppercase sexual terms");
    }
}
