package com.github.wnebyte.engine.core.ecs;

import org.joml.Vector2f;

import java.util.Objects;

public class Transform {

    public Vector2f position;

    public Vector2f scale;

    public Transform() {
        init(new Vector2f(), new Vector2f());
    }

    public Transform(Vector2f position) {
        init(position, new Vector2f());
    }

    public Transform(Vector2f position, Vector2f scale) {
        init(position, scale);
    }

    private void init(Vector2f position, Vector2f scale) {
        this.position = position;
        this.scale = scale;
    }

    public Transform copy() {
        return new Transform(new Vector2f(this.position), new Vector2f(this.scale));
    }

    public void copy(Transform transf) {
        transf.position.set(this.position);
        transf.scale.set(this.scale);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Transform)) return false;
        Transform transform = (Transform) o;
        return Objects.equals(transform.position, this.position) &&
                Objects.equals(transform.scale, this.scale);
    }

    @Override
    public int hashCode() {
        int result = 79;
        return result +
                13 +
                Objects.hashCode(this.position) +
                Objects.hashCode(this.scale);
    }

    @Override
    public String toString() {
        return String.format(
                "Transform[position: %s, scale: %s]", position, scale
        );
    }
}
