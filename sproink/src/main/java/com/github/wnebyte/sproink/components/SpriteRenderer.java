package com.github.wnebyte.sproink.components;

import java.util.Objects;
import org.joml.Vector2f;
import org.joml.Vector4f;
import com.github.wnebyte.sproink.core.Component;
import com.github.wnebyte.sproink.core.Transform;
import com.github.wnebyte.sproink.renderer.Texture;
import com.github.wnebyte.sproink.ui.JImGui;
import com.github.wnebyte.sproink.util.Assets;

public class SpriteRenderer extends Component {

    private Vector4f color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    private Sprite sprite = new Sprite();

    private transient Transform transform;

    private transient boolean dirty = true;

    @Override
    public void start() {
        transform = gameObject.transform.copy();
    }

    @Override
    public void update(float dt) {
        if (!transform.equals(gameObject.transform)) {
            gameObject.transform.copy(transform);
            dirty = true;
        }
    }

    @Override
    public void editorUpdate(float dt) {
        if (!transform.equals(gameObject.transform)) {
            gameObject.transform.copy(transform);
            dirty = true;
        }
    }

    @Override
    public void imGui() {
        if (JImGui.colorPicker4("Color", color)) {
            dirty = true;
        }
    }

    @Override
    public void refresh() {
        Texture texture = sprite.getTexture();
        if (texture != null) {
            sprite.setTexture(Assets.getTexture(texture.getPath()));
        }
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public void setTexture(Texture texture) {
        sprite.setTexture(texture);
    }

    public Vector2f[] getTexCoords() {
        return sprite.getTexCoords();
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setClean() {
        dirty = false;
    }

    public void setDirty() {
        dirty = true;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        this.dirty = true;
    }

    public Vector4f getColor() {
        return color;
    }

    public void setColor(Vector4f color) {
        if (!this.color.equals(color)) {
            this.color.set(color);
            this.dirty = true;
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
