package com.github.wnebyte.engine.core.scene;

import com.github.wnebyte.engine.renderer.Texture;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import com.github.wnebyte.engine.components.*;
import com.github.wnebyte.engine.core.Prefabs;
import com.github.wnebyte.engine.core.Transform;
import com.github.wnebyte.engine.core.ecs.*;
import com.github.wnebyte.engine.core.camera.Camera;
import com.github.wnebyte.engine.util.ResourceFlyWeight;

public class LevelEditorScene extends Scene {

    private Spritesheet sprites;

    private GameObject levelEditorStuff = new GameObject("LevelEditor", new Transform(), 0);

    @Override
    public void init() {
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new GridLines());
        loadResources();
        this.camera = new Camera(new Vector2f(-250, -100));
        sprites = ResourceFlyWeight.getSpritesheet("/images/spritesheets/decorationsAndBlocks.png");
        if (levelLoaded) {
            if (gameObjects.size() > 0 ) {
                this.activeGameObject = gameObjects.get(0);
            }
            return;
        }
        /*
        obj1 = new GameObject("Object 1", new Transform(new Vector2f(200, 100),
                new Vector2f(256, 256)), -1
        );
        obj1Spr = new SpriteRenderer();
        obj1Spr.setColor(new Vector4f(1, 0, 0, 1));
        obj1.addComponent(obj1Spr);
        obj1.addComponent(new RigidBody());
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100),
                new Vector2f(256, 256)), -2
        );
        SpriteRenderer obj2SpriteRenderer = new SpriteRenderer();
        Sprite obj2Sprite = new Sprite();
        obj2Sprite.setTexture(ResourceFlyWeight.getTexture("/images/blendImage2.png"));
        obj2SpriteRenderer.setSprite(obj2Sprite);
        obj2.addComponent(obj2SpriteRenderer);
        this.addGameObjectToScene(obj2);
        */
    }

    private void loadResources() {
        ResourceFlyWeight.getShader("/shaders/default.glsl");
        ResourceFlyWeight.addSpritesheet("/images/spritesheets/decorationsAndBlocks.png",
                new Spritesheet(ResourceFlyWeight.getTexture("/images/spritesheets/decorationsAndBlocks.png"),
                        16, 16, 81, 0));
        ResourceFlyWeight.getTexture("/images/blendImage2.png");

        for (GameObject go : gameObjects) {
            SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
            if (spr != null) {
                Texture texture = spr.getTexture();
                if (texture != null) {
                    spr.setTexture(ResourceFlyWeight.getTexture(texture.getPath()));
                }
            }
        }
    }

    float x = 0f;
    float y = 0f;

    @Override
    public void update(float dt) {
        levelEditorStuff.update(dt);
        /*
        DebugDraw.addCircle(new Vector2f(x, y), 64, new Vector3f(0, 1f, 0), 1);
        x += 50f * dt;
        y += 50f * dt;
        */
        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }
    }

    @Override
    public void render() {
        this.renderer.render();
    }

    @Override
    public void imGui() {
        ImGui.begin("Spritesheets");

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;
        for (int i = 0; i < sprites.size(); i++) {
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = sprite.getWidth() * 2;
            float spriteHeight = sprite.getHeight() * 2;
            int id = sprite.getTexId();
            Vector2f[] texCoords = sprite.getTexCoords();

            ImGui.pushID(i);
            if (ImGui.imageButton(id, spriteWidth, spriteHeight,
                    texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) { // [0].x, [0].y, [2].x, [2].y
                GameObject object = Prefabs.generateSpriteObject(sprite, 32, 32);
                // Attach this to the mouse cursor
                levelEditorStuff.getComponent(MouseControls.class).drag(object);
            }
            ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);
            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
            if (i + 1 < sprites.size() && nextButtonX2 < windowX2) {
                ImGui.sameLine();
            }
        }

        ImGui.end();
    }
}

