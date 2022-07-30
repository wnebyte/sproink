package com.github.wnebyte.engine.physics2d.components;

import com.github.wnebyte.engine.components.Collider;

public class CircleCollider extends Collider {

    private float radius = 1.0f;

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
