package com.github.wnebyte.engine.core;

import java.util.Objects;
import org.joml.Vector2f;
import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.editor.JImGui;

public class Transform extends Component {

    public final Vector2f position;

    public final Vector2f scale;

    public float rotation;

    public int zIndex;

    public Transform() {
        this(new Vector2f(), new Vector2f());
    }

    public Transform(Vector2f position) {
        this(position, new Vector2f());
    }

    public Transform(Vector2f position, Vector2f scale) {
        this.position = position;
        this.scale = scale;
        this.rotation = 0.0f;
        this.zIndex = 0;
    }

    @Override
    public void imGui() {
        JImGui.drawVec2Control("Position", position);
        JImGui.drawVec2Control("Scale", scale, 32.0f);
        rotation = JImGui.dragFloat("Rotation", rotation);
        zIndex = JImGui.dragInt("Z-Index", zIndex);
    }

    public Transform copy() {
        return new Transform(new Vector2f(this.position), new Vector2f(this.scale));
    }

    public void copy(Transform transform) {
        transform.position.set(this.position);
        transform.scale.set(this.scale);
        transform.rotation = this.rotation;
        transform.zIndex = this.zIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Transform)) return false;
        Transform transform = (Transform) o;
        return Objects.equals(transform.position, this.position) &&
                Objects.equals(transform.scale, this.scale) &&
                Objects.equals(transform.rotation, this.rotation) &&
                Objects.equals(transform.zIndex, this.zIndex) &&
                super.equals(transform);
    }

    @Override
    public int hashCode() {
        int result = 79;
        return result +
                13 +
                Objects.hashCode(this.position) +
                Objects.hashCode(this.scale) +
                Objects.hashCode(this.rotation) +
                Objects.hashCode(this.zIndex) +
                super.hashCode();
    }

    @Override
    public String toString() {
        return String.format(
                "Transform[position: %s, scale: %s, rotation: %.2f, zIndex: %d]", position, scale, rotation, zIndex
        );
    }
}
