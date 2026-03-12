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
package com.novusforge.astrum.core.ecs;

import com.novusforge.astrum.core.ecs.components.PositionComponent;
import com.novusforge.astrum.core.ecs.components.VelocityComponent;
import com.novusforge.astrum.core.ecs.systems.MovementSystem;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ECSTest {

    @Test
    public void testHighEntityVolume() {
        World world = new World();
        world.registerComponent(PositionComponent.class);
        world.registerComponent(VelocityComponent.class);

        int count = 100_000;
        long start = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            int entity = world.createEntity();
            world.addComponent(entity, new PositionComponent(0, 0, 0));
            world.addComponent(entity, new VelocityComponent(1, 1, 1));
        }

        long end = System.currentTimeMillis();
        System.out.println("Created " + count + " entities in " + (end - start) + "ms");

        MovementSystem moveSystem = new MovementSystem(world);
        
        start = System.currentTimeMillis();
        moveSystem.update(1.0f / 60.0f); // Simulate one frame
        end = System.currentTimeMillis();

        System.out.println("Processed one frame for " + count + " entities in " + (end - start) + "ms");
        
        // Verify one entity
        PositionComponent p = world.getComponent(0, PositionComponent.class);
        assertEquals(1.0f/60.0f, p.x, 0.0001f);
        
        assertTrue((end - start) < 100, "Processing 100k entities took too long: " + (end - start) + "ms");
    }
}
