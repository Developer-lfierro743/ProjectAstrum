package com.novusforge.astrum.core.ecs.components;

import com.novusforge.astrum.core.ecs.Component;

public class PositionComponent implements Component {
    public float x, y, z;

    public PositionComponent(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
