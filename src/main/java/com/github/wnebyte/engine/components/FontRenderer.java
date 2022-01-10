package com.github.wnebyte.engine.components;

import com.github.wnebyte.engine.core.ecs.Component;

public class FontRenderer extends Component {

    @Override
    public void start() {
        if (gameObject.getComponent(SpriteRenderer.class) != null) {
            System.out.println("Found Font Renderer");
        }
    }

    @Override
    public void update(float dt) {

    }
}
