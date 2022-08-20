package com.github.wnebyte.sproink.physics2d.components;

import org.joml.Vector2f;
import com.github.wnebyte.sproink.core.window.Window;
import com.github.wnebyte.sproink.physics2d.Physics2D;

public class PillboxCollider extends Collider {

    private transient CircleCollider bottomCircle = new CircleCollider();

    private transient Box2DCollider box = new Box2DCollider();

    private transient boolean resetFixtureNextFrame = false;

    private float width = 0.1f;

    private float height = 0.2f;

    @Override
    public void start() {
        bottomCircle.gameObject = this.gameObject;
        box.gameObject = this.gameObject;
        recalculateColliders();
    }

    @Override
    public void update(float dt) {
        if (resetFixtureNextFrame) {
            resetFixture();
        }
    }

    @Override
    public void editorUpdate(float dt) {
        bottomCircle.editorUpdate(dt);
        box.editorUpdate(dt);

        if (resetFixtureNextFrame) {
            resetFixture();
        }
    }

    public void setWidth(float width) {
        this.width = width;
        recalculateColliders();
        resetFixture();
    }

    public void setHeight(float height) {
        this.height = height;
        recalculateColliders();
        resetFixture();
    }

    public CircleCollider getBottomCircle() {
        return bottomCircle;
    }

    public Box2DCollider getBox() {
        return box;
    }

    public void recalculateColliders() {
        float circleRadius = width / 2.0f;
        float boxHeight = height - circleRadius;
        bottomCircle.setRadius(circleRadius);
        bottomCircle.setOffset(new Vector2f(offset).sub(0, (height - circleRadius * 2.0f) / 2.0f));
        box.setHalfSize(new Vector2f(width - 0.01f, boxHeight));
        box.setOffset(new Vector2f(offset).add(0, (height - boxHeight) / 2.0f));
    }

    public void resetFixture() {
        Physics2D physics = Window.getScene().getPhysics();

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
}
