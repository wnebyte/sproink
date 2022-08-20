package com.github.wnebyte.sproink.components;

import java.util.Objects;

import com.github.wnebyte.sproink.core.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;
import com.github.wnebyte.sproink.core.ecs.Component;
import com.github.wnebyte.sproink.renderer.Texture;
import com.github.wnebyte.sproink.core.ui.JImGui;

public class SpriteRenderer extends Component {

    private Vector4f color = new Vector4f(1, 1, 1, 1);

    private Sprite sprite = new Sprite();

    private transient Transform lastTransform;

    private transient boolean isDirty = true;

    @Override
    public void start() {
        this.lastTransform = gameObject.transform.copy();
    }

    @Override
    public void editorUpdate(float dt) {
        if (!lastTransform.equals(gameObject.transform)) {
            gameObject.transform.copy(lastTransform);
            isDirty = true;
        }
    }

    @Override
    public void update(float dt) {
        if (!lastTransform.equals(gameObject.transform)) {
            gameObject.transform.copy(lastTransform);
            isDirty = true;
        }
    }

    @Override
    public void imGui() {
        if (JImGui.colorPicker4("Color", color)) {
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

    public void setDirty() {
        isDirty = true;
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

    public void setTexture(Texture texture) {
        sprite.setTexture(texture);
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
