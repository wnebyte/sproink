package com.github.wnebyte.engine.physics2d.components;

import com.github.wnebyte.engine.core.window.Window;
import org.joml.Vector2f;
import com.github.wnebyte.engine.renderer.DebugDraw;

public class CircleCollider extends Collider {

    private float radius = 1.0f;

    private transient boolean resetFixtureNextFrame = false;

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public void update(float dt) {
        if (resetFixtureNextFrame) {
            resetFixture();
        }
    }

    @Override
    public void editorUpdate(float dt) {
        Vector2f center = new Vector2f(gameObject.transform.position).add(offset);
        DebugDraw.addCircle(center, radius);

        if (resetFixtureNextFrame) {
            resetFixture();
        }
    }

    public void resetFixture() {
        if (Window.getPhysics().isLocked()) {
            resetFixtureNextFrame = true;
            return;
        }
        resetFixtureNextFrame = false;

        if (gameObject != null) {
            RigidBody2D rb = gameObject.getComponent(RigidBody2D.class);
            if (rb != null) {
                Window.getPhysics().resetCircleCollider(rb, this);
            }
        }
    }
}
