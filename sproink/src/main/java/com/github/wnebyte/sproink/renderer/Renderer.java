package com.github.wnebyte.sproink.renderer;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import com.github.wnebyte.sproink.core.GameObject;
import com.github.wnebyte.sproink.components.SpriteRenderer;
import com.github.wnebyte.sproink.components.TextRenderer;
import com.github.wnebyte.sproink.fonts.SFont;
import com.github.wnebyte.sproink.util.Assets;

public class Renderer {

    private static final String TAG = "Renderer";

    private static final int MAX_BATCH_SIZE = 1000;

    private Shader shader;

    private Shader fontShader;

    private SFont font;

    private final List<RenderBatch> batches;

    private final TextRenderBatch textBatch;

    private final List<TextRenderer> text;

    public Renderer() {
        this.batches = new ArrayList<>();
        this.text = new ArrayList<>();
        this.fontShader = Assets.getShader(
                "C:/Users/ralle/dev/java/Engine/editor/build/install/editor/assets/shaders/font.glsl");
        this.font = Assets.getFont(
                "C:/Users/ralle/dev/java/Engine/editor/build/install/editor/assets/fonts/super-mario.ttf");
        this.textBatch = new TextRenderBatch(this);
    }

    public void start() {
        textBatch.start();
    }

    public void add(GameObject go) {
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if (spr != null) {
            add(spr);
        }
        TextRenderer tr = go.getComponent(TextRenderer.class);
        if (tr != null) {
            add(tr);
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

    private void add(TextRenderer tr) {
        text.add(tr);
    }

    public void render() {
        shader.use();
        for (int i = 0; i < batches.size(); i++) {
            RenderBatch batch = batches.get(i);
            batch.render();
        }
        for (int i = 0; i < text.size(); i++) {
            TextRenderer tr = text.get(i);
            textBatch.addText(tr);
        }
        textBatch.flush();
    }

    public void destroyGameObject(GameObject go) {
        TextRenderer tr = go.getComponent(TextRenderer.class);
        if (tr != null) {
            text.remove(tr);
            return;
        }
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

    public void setFontShader(Shader shader) {
        this.fontShader = shader;
    }

    public Shader getFontShader() {
        return fontShader;
    }

    public void setFont(SFont font) {
        this.font = font;
    }

    public SFont getFont() {
        return font;
    }
}
