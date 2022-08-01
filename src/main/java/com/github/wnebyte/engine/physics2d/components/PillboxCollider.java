package com.github.wnebyte.engine.physics2d.components;

import org.joml.Vector2f;
import com.github.wnebyte.engine.core.window.Window;
import com.github.wnebyte.engine.physics2d.Physics2D;

public class PillboxCollider extends Collider {

    private transient CircleCollider topCircleCollider = new CircleCollider();

    private transient CircleCollider bottomCircleCollider = new CircleCollider();

    private transient Box2DCollider boxCollider = new Box2DCollider();

    private transient boolean resetFixtureNextFrame = false;

    private float width = 0.1f;

    private float height = 0.2f;

    @Override
    public void start() {
        topCircleCollider.gameObject = this.gameObject;
        bottomCircleCollider.gameObject = this.gameObject;
        boxCollider.gameObject = this.gameObject;
        calcColliders();
    }

    @Override
    public void update(float dt) {
        if (resetFixtureNextFrame) {
            resetFixture();
        }
    }

    @Override
    public void editorUpdate(float dt) {
        topCircleCollider.editorUpdate(dt);
        bottomCircleCollider.editorUpdate(dt);
        boxCollider.editorUpdate(dt);

        if (resetFixtureNextFrame) {
            resetFixture();
        }
    }

    public void calcColliders() {
        float circleRadius = width / 4.0f;
        float boxHeight = height - 2 * circleRadius;
        topCircleCollider.setRadius(circleRadius);
        bottomCircleCollider.setRadius(circleRadius);
        topCircleCollider.setOffset(new Vector2f(offset).add(0, boxHeight / 4.0f));
        bottomCircleCollider.setOffset(new Vector2f(offset).sub(0, boxHeight / 4.0f));
        boxCollider.setHalfSize(new Vector2f(width / 2.0f, boxHeight / 2.0f));
        boxCollider.setOffset(new Vector2f(offset));
    }

    public void resetFixture() {
        Physics2D physics = Window.getPhysics2d();

        if (physics.isLocked()) {
            resetFixtureNextFrame = true;
            return;
        }

        resetFixtureNextFrame = false;
        if (gameObject != null) {
            RigidBody2D rb = gameObject.getComponent(RigidBody2D.class);
            if (rb != null) {
                physics.resetPillboxCollider(rb, this);
            }
        }
    }

    public void setWidth(float width) {
        this.width = width;
        calcColliders();
        resetFixture();
    }

    public void setHeight(float height) {
        this.height = height;
        calcColliders();
        resetFixture();
    }

    public CircleCollider getTopCircleCollider() {
        return topCircleCollider;
    }

    public CircleCollider getBottomCircleCollider() {
        return bottomCircleCollider;
    }

    public Box2DCollider getBoxCollider() {
        return boxCollider;
    }
}
