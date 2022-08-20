package com.github.wnebyte.sproink.physics2d.components;

import org.joml.Vector2f;
import com.github.wnebyte.sproink.renderer.DebugDraw;

public class Box2DCollider extends Collider {

    private Vector2f halfSize = new Vector2f(1);

    private Vector2f origin = new Vector2f();

    public Vector2f getHalfSize() {
        return halfSize;
    }

    public void setHalfSize(Vector2f halfSize) {
        this.halfSize = halfSize;
    }

    public Vector2f getOrigin() {
        return origin;
    }

    public void setOrigin(Vector2f origin) {
        this.origin = origin;
    }

    @Override
    public void editorUpdate(float dt) {
        Vector2f center = new Vector2f(gameObject.transform.position).add(offset);
        DebugDraw.addBox2D(center, halfSize, gameObject.transform.rotation);
    }
}
