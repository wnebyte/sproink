package com.github.wnebyte.sproink.core;

import java.util.Objects;
import org.joml.Vector2f;
import com.github.wnebyte.sproink.ui.JImGui;

public class Transform extends Component {

    /*
    ###########################
    #      STATIC METHODS     #
    ###########################
    */

    public static Transform copyOf(Transform transform) {
        return (transform == null) ? null : transform.copy();
    }

    public static Transform newInstance() {
        return new Transform();
    }

    public static Transform newInstance(Vector2f position) {
        return new Transform(position);
    }

    public static Transform newInstance(Vector2f position, Vector2f scale) {
        return new Transform(position, scale);
    }

    public static Transform newInstance(Vector2f position, Vector2f scale, float rotation, int zIndex) {
        return new Transform(position, scale, rotation, zIndex);
    }

    /*
    ###########################
    #       STATIC FIELDS     #
    ###########################
    */

    private static final float DEFAULT_ROTATION = 0.0f;

    private static final int DEFAULT_Z_INDEX = 0;

    /*
    ###########################
    #         FIELDS          #
    ###########################
    */

    public final Vector2f position;

    public final Vector2f scale;

    public float rotation;

    public int zIndex;

    /*
    ###########################
    #       CONSTRUCTORS      #
    ###########################
    */

    public Transform() {
        this(new Vector2f(), new Vector2f());
    }

    public Transform(Vector2f position) {
        this(position, new Vector2f());
    }

    public Transform(Vector2f position, Vector2f scale) {
        this(position, scale, DEFAULT_ROTATION, DEFAULT_Z_INDEX);
    }

    public Transform(Vector2f position, Vector2f scale, float rotation, int zIndex) {
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
        this.zIndex = zIndex;
    }

    /*
    ###########################
    #         METHODS         #
    ###########################
    */

    @Override
    public void imGui() {
        String name = JImGui.inputText("Name", gameObject.getName());
        gameObject.setName(name);
        JImGui.drawVec2Control("Position", position);
        JImGui.drawVec2Control("Scale", scale, 32.0f);
        rotation = JImGui.dragFloat("Rotation", rotation);
        zIndex = JImGui.dragInt("Z-Index", zIndex);
    }

    public Transform copy() {
        return new Transform(new Vector2f(position), new Vector2f(scale), rotation, zIndex);
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
