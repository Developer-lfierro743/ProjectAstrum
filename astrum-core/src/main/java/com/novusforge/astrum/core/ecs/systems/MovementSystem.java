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
