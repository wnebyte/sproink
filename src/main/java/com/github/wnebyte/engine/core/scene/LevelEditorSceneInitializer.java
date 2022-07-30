package com.github.wnebyte.engine.core.scene;

import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import com.github.wnebyte.engine.core.Prefabs;
import com.github.wnebyte.engine.core.ecs.*;
import com.github.wnebyte.engine.renderer.Texture;
import com.github.wnebyte.engine.components.*;
import com.github.wnebyte.engine.util.ResourceFlyWeight;

public class LevelEditorSceneInitializer implements SceneInitializer {

    private Spritesheet sprites;

    private GameObject levelEditorStuff;

    @Override
    public void init(Scene scene) {
        sprites = ResourceFlyWeight.getSpritesheet("/images/spritesheets/decorationsAndBlocks.png");
        Spritesheet gizmos = ResourceFlyWeight.getSpritesheet("/images/gizmos.png");
        levelEditorStuff = scene.createGameObject("LevelEditor");
        levelEditorStuff.setNoSerialize();
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new GridLines());
        levelEditorStuff.addComponent(new EditorCamera(scene.getCamera()));
        levelEditorStuff.addComponent(new GizmoSystem(gizmos));
        scene.addGameObjectToScene(levelEditorStuff);
    }

    @Override
    public void imGui() {
        ImGui.begin("Level Editor Stuff");
        levelEditorStuff.imGui();
        ImGui.end();

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

    @Override
    public void loadResources(Scene scene) {
        ResourceFlyWeight.getShader("/shaders/default.glsl");
        ResourceFlyWeight.addSpritesheet("/images/spritesheets/decorationsAndBlocks.png",
                new Spritesheet(ResourceFlyWeight.getTexture("/images/spritesheets/decorationsAndBlocks.png"),
                        16, 16, 81, 0));
        ResourceFlyWeight.addSpritesheet("/images/gizmos.png",
                new Spritesheet(ResourceFlyWeight.getTexture("/images/gizmos.png"),
                        24, 48, 3, 0));
        ResourceFlyWeight.getTexture("/images/blendImage2.png");

        for (GameObject go : scene.getGameObjects()) {
            SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
            if (spr != null) {
                Texture texture = spr.getTexture();
                if (texture != null) {
                    spr.setTexture(ResourceFlyWeight.getTexture(texture.getPath()));
                }
            }
        }
    }
}

