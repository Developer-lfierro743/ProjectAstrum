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
