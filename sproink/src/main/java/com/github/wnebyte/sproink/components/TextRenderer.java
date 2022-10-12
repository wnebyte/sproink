package com.github.wnebyte.sproink.components;

import java.util.Objects;
import org.joml.Vector3f;
import com.github.wnebyte.sproink.core.Component;
import com.github.wnebyte.sproink.core.Transform;
import com.github.wnebyte.sproink.fonts.SFont;

public class TextRenderer extends Component {

    private Vector3f color = new Vector3f(1.0f, 1.0f, 1.0f);

    private String text;

    private SFont font;

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


    public void setColor(Vector3f color) {
        if (!this.color.equals(color)) {
            this.color.set(color);
            dirty = true;
        }
    }

    public Vector3f getColor() {
        return color;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setFont(SFont font) {
        this.font = font;
    }

    public SFont getFont() {
        return font;
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

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof TextRenderer)) return false;
        TextRenderer tr = (TextRenderer) o;
        return Objects.equals(tr.color, this.color) &&
                Objects.equals(tr.text, this.text) &&
                Objects.equals(tr.dirty, this.dirty) &&
                super.equals(tr);
    }

    @Override
    public int hashCode() {
        int result = 25;
        return 2 *
                result +
                Objects.hashCode(this.color) +
                Objects.hashCode(this.text) +
                Objects.hashCode(this.dirty);
    }
}
