package com.github.wnebyte.engine.physics2d.components;

import org.joml.Vector2f;
import com.github.wnebyte.engine.renderer.DebugDraw;
import com.github.wnebyte.engine.components.Collider;

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

    @Override
    public void editorUpdate(float dt) {
        Vector2f center = new Vector2f(gameObject.transform.position).add(getOffset());
        DebugDraw.addBox2D(center, halfSize, gameObject.transform.rotation);
    }
}
