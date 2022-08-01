package com.github.wnebyte.engine.physics2d.components;

import org.joml.Vector2f;
import com.github.wnebyte.engine.renderer.DebugDraw;

public class CircleCollider extends Collider {

    private float radius = 1.0f;

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public void editorUpdate(float dt) {
        Vector2f center = new Vector2f(gameObject.transform.position).add(offset);
        DebugDraw.addCircle(center, radius);
    }
}
