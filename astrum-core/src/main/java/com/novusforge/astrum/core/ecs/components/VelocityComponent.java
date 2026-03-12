package com.novusforge.astrum.core.ecs.components;

import com.novusforge.astrum.core.ecs.Component;

public class VelocityComponent implements Component {
    public float vx, vy, vz;

    public VelocityComponent(float vx, float vy, float vz) {
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
    }
}
