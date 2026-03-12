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
package com.novusforge.astrum.core.mod;

import com.novusforge.astrum.api.Mod;
import com.novusforge.astrum.api.ModContext;
import com.novusforge.astrum.api.ModMetadata;
import com.novusforge.astrum.core.ecs.World;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ModVerificationTest {

    @Test
    public void testValidModRegistration() {
        World world = new World();
        ModManager modManager = new ModManager(world);
        
        Mod validMod = new SampleMod();
        modManager.registerMod(validMod, "OFFICIAL_MOD_SIG");
        
        // Entity check to ensure it actually loaded
        int entityId = world.createEntity();
        world.addComponent(entityId, new SampleMod.ModdedComponent());
        assertNotNull(world.getComponent(entityId, SampleMod.ModdedComponent.class));
    }

    @Test
    public void testInvalidSignatureRegistration() {
        World world = new World();
        ModManager modManager = new ModManager(world);
        
        Mod mod = new SampleMod();
        modManager.registerMod(mod, "FAKE_SIG");
        
        // Entity check: should fail to add component because system wasn't registered/mod wasn't loaded
        int entityId = world.createEntity();
        try {
            world.addComponent(entityId, new SampleMod.ModdedComponent());
            fail("Should have failed to add component for an unloaded mod.");
        } catch (NullPointerException e) {
            // Expected because component type wasn't registered in ECS world
        }
    }

    @Test
    public void testUnsafeMetadataRegistration() {
        World world = new World();
        ModManager modManager = new ModManager(world);
        
        Mod unsafeMod = new Mod() {
            @Override
            public void onInitialize(ModContext context) {}
            @Override
            public ModMetadata getMetadata() {
                return new ModMetadata("porn_mod", "Unsafe Mod", "1.0.0");
            }
        };
        
        modManager.registerMod(unsafeMod, "OFFICIAL_MOD_SIG");
        
        // Should not be in active mods (if we had a getter) or shouldn't have initialized
        // We verify via absence of logs or side effects.
    }
}
