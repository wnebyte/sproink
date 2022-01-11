package com.github.wnebyte.engine.components;

import java.util.Objects;
import org.joml.Vector2f;
import org.joml.Vector4f;
import com.github.wnebyte.engine.core.ecs.Transform;
import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.renderer.Texture;

public class SpriteRenderer extends Component {

    private Vector4f color;

    private Sprite sprite;

    private Transform lastTransform;

    private boolean isDirty;

    public SpriteRenderer(Vector4f color) {
        this.color = color;
        this.sprite = new Sprite(null);
        this.isDirty = true;
    }

    public SpriteRenderer(Sprite sprite) {
        this.sprite = sprite;
        this.color = new Vector4f(1, 1, 1, 1);
        this.isDirty = true;
    }

    @Override
    public void start() {
        this.lastTransform = gameObject.transform.copy();
    }

    @Override
    public void update(float dt) {
        if (!lastTransform.equals(gameObject.transform)) {
            gameObject.transform.copy(lastTransform);
            isDirty = true;
        }
    }

    public Vector4f getColor() {
        return color;
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public Vector2f[] getTexCoords() {
        return sprite.getTexCoords();
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setClean() {
        isDirty = false;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        this.isDirty = true;
    }

    public void setColor(Vector4f color) {
        if (!this.color.equals(color)) {
            this.color.set(color);
            this.isDirty = true;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof SpriteRenderer)) return false;
        SpriteRenderer spr = (SpriteRenderer) o;
        return Objects.equals(spr.color, this.color) &&
                Objects.equals(spr.sprite, this.sprite) &&
                super.equals(spr);
    }

    @Override
    public int hashCode() {
        int result = 17;
        return result +
                5 +
                Objects.hashCode(this.color) +
                Objects.hashCode(this.sprite) +
                super.hashCode();
    }

    @Override
    public String toString() {
        return String.format(
                "SpriteRenderer[color: %s, sprite: %s]", color, sprite
        );
    }
}
