package com.github.wnebyte.sproink.physics2d.components;

import org.joml.Vector2f;
import com.github.wnebyte.sproink.core.ecs.Component;

public abstract class Collider extends Component {

    protected Vector2f offset = new Vector2f();

    public Vector2f getOffset() {
        return offset;
    }

    public void setOffset(Vector2f offset) {
        this.offset.set(offset);
    }
}
