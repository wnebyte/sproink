package com.github.wnebyte.sproink.renderer;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.components.SpriteRenderer;

public class Renderer {

    private static final int MAX_BATCH_SIZE = 1000;

    private Shader shader;

    private final List<RenderBatch> batches;

    public Renderer() {
        this.batches = new ArrayList<>();
    }

    public void add(GameObject go) {
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if (spr != null) {
            add(spr);
        }
    }

    private void add(SpriteRenderer spr) {
        boolean added = false;
        for (RenderBatch batch : batches) {
            if (batch.hasRoom() && batch.zIndex() == spr.gameObject.transform.zIndex) {
                Texture texture = spr.getTexture();
                if (texture == null || (batch.hasTexture(texture) || (batch.hasTextureRoom()))) {
                    batch.addSprite(spr);
                    added = true;
                    break;
                }
            }
        }

        if (!added) {
            RenderBatch newBatch = new RenderBatch(this, MAX_BATCH_SIZE, spr.gameObject.transform.zIndex);
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(spr);
            Collections.sort(batches);
        }
    }

    public void render() {
        shader.use();
        for (int i = 0; i < batches.size(); i++) {
            batches.get(i).render();
        }
    }

    public void destroyGameObject(GameObject go) {
        if (go.getComponent(SpriteRenderer.class) == null) {
            return;
        }
        for (RenderBatch batch : batches) {
            if (batch.destroyIfExists(go)) {
                return;
            }
        }
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    public Shader getShader() {
        return shader;
    }
}
