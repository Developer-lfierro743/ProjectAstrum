package com.novusforge.astrum.core.ecs.systems;

import com.novusforge.astrum.core.ecs.World;
import com.novusforge.astrum.core.ecs.components.PositionComponent;
import com.novusforge.astrum.core.ecs.components.VelocityComponent;
import java.util.BitSet;
import java.util.List;

/**
 * A simple system that updates positions based on velocities.
 * Demonstrates the core logic of iterating over filtered entities.
 */
public class MovementSystem {
    private final World world;
    private final BitSet requiredComponents;

    public MovementSystem(World world) {
        this.world = world;
        this.requiredComponents = world.createMask(PositionComponent.class, VelocityComponent.class);
    }

    public void update(float deltaTime) {
        List<Integer> entities = world.query(requiredComponents);
        for (int entityId : entities) {
            PositionComponent pos = world.getComponent(entityId, PositionComponent.class);
            VelocityComponent vel = world.getComponent(entityId, VelocityComponent.class);

            pos.x += vel.vx * deltaTime;
            pos.y += vel.vy * deltaTime;
            pos.z += vel.vz * deltaTime;
        }
    }
}
