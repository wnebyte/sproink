package com.github.wnebyte.engine.components;

import org.joml.Vector2f;
import com.github.wnebyte.engine.core.ecs.Component;

public abstract class Collider extends Component {

    private Vector2f offset = new Vector2f();

    public Vector2f getOffset() {
        return offset;
    }
}
