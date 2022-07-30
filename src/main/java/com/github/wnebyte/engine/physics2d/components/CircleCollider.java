package com.github.wnebyte.engine.physics2d.components;

import com.github.wnebyte.engine.core.ecs.Component;

public class CircleCollider extends Component {

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    private float radius = 1.0f;
}
