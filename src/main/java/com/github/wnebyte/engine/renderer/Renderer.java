package com.github.wnebyte.engine.renderer;

import java.util.List;
import java.util.ArrayList;
import com.github.wnebyte.engine.components.SpriteRenderer;
import com.github.wnebyte.engine.core.ecs.GameObject;

public class Renderer {

    private final int MAX_BATCH_SIZE = 1000;

    private List<RenderBatch> batches;

    public Renderer() {
        this.batches = new ArrayList<>();
    }

    public void add(GameObject go) {
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if (spr != null) {
            add(spr);
        }
    }

    private void add(SpriteRenderer sprite) {
        boolean added = false;
        for (RenderBatch batch : batches) {
            if (batch.hasRoom()) {
                Texture texture = sprite.getTexture();
                if (texture == null || (batch.hasTexture(texture) || (batch.hasTextureRoom()))) {
                    batch.addSprite(sprite);
                    added = true;
                    break;
                }
            }
        }

        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE);
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
        }
    }

    public void render() {
        for (RenderBatch batch : batches) {
            batch.render();
        }
    }
}
