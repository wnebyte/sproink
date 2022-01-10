package com.github.wnebyte.engine.components;

import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.renderer.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.Arrays;
import java.util.Objects;

public class SpriteRenderer extends Component {

    private Vector4f color;

    private Texture texture;

    private Vector2f[] texCoords;

    public SpriteRenderer(Vector4f color) {
        this.color = color;
        this.texture = null;
    }

    public SpriteRenderer(Texture texture) {
        this.texture = texture;
        this.color = new Vector4f(1, 1, 1, 1);
    }

    @Override
    public void start() { }

    @Override
    public void update(float dt) { }

    public Vector4f getColor() {
        return color;
    }

    public Texture getTexture() {
        return texture;
    }

    public Vector2f[] getTexCoords() {
        Vector2f[] texCoords = {
                new Vector2f(1, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 0),
                new Vector2f(0, 1)
        };
        return texCoords;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof SpriteRenderer)) return false;
        SpriteRenderer sprite = (SpriteRenderer) o;
        return Objects.equals(sprite.color, this.color) &&
                Objects.equals(sprite.texture, this.texture) &&
                Arrays.equals(sprite.texCoords, this.texCoords) &&
                super.equals(sprite);
    }

    @Override
    public int hashCode() {
        int result = 17;
        return result +
                5 +
                Objects.hashCode(this.color) +
                Objects.hashCode(this.texture) +
                Objects.hashCode(this.texCoords) +
                super.hashCode();
    }

    @Override
    public String toString() {
        return String.format(
                "SpriteRenderer[color: %s, texture: %s, texCoords: %s]",
                color, texture, Arrays.toString(texCoords)
        );
    }
}
