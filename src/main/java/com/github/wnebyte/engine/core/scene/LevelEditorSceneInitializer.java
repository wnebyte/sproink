package com.github.wnebyte.engine.core.scene;

import java.io.File;
import java.util.Collection;

import com.github.wnebyte.engine.physics2d.components.Box2DCollider;
import com.github.wnebyte.engine.physics2d.components.RigidBody2D;
import com.github.wnebyte.engine.physics2d.enums.BodyType;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import com.github.wnebyte.engine.core.Prefabs;
import com.github.wnebyte.engine.core.ecs.*;
import com.github.wnebyte.engine.core.audio.Sound;
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
        levelEditorStuff.addComponent(new KeyControls());
        levelEditorStuff.addComponent(new GridLines());
        levelEditorStuff.addComponent(new EditorCamera(scene.getCamera()));
        levelEditorStuff.addComponent(new GizmoSystem(gizmos));
        scene.addGameObjectToScene(levelEditorStuff);
    }

    @Override
    public void loadResources(Scene scene) {
        // load shaders
        ResourceFlyWeight.getShader("/shaders/default.glsl");

        // load spritesheets
        ResourceFlyWeight.addSpritesheet("/images/spritesheets/decorationsAndBlocks.png",
                new Spritesheet(ResourceFlyWeight.getTexture("/images/spritesheets/decorationsAndBlocks.png"),
                        16, 16, 81, 0));
        ResourceFlyWeight.addSpritesheet("/images/spritesheet.png",
                new Spritesheet(ResourceFlyWeight.getTexture("/images/spritesheet.png"),
                        16, 16, 26, 0));
        ResourceFlyWeight.addSpritesheet("/images/turtle.png",
                new Spritesheet(ResourceFlyWeight.getTexture("/images/turtle.png"),
                        16, 24, 4, 0));
        ResourceFlyWeight.addSpritesheet("/images/bigSpritesheet.png",
                new Spritesheet(ResourceFlyWeight.getTexture("/images/bigSpritesheet.png"),
                        16, 32, 42, 0));
        ResourceFlyWeight.addSpritesheet("/images/spritesheets/pipes.png",
                new Spritesheet(ResourceFlyWeight.getTexture("/images/spritesheets/pipes.png"),
                        32, 32, 4, 0));
        ResourceFlyWeight.addSpritesheet("/images/items.png",
                new Spritesheet(ResourceFlyWeight.getTexture("/images/items.png"),
                        16, 16, 43, 0));
        ResourceFlyWeight.addSpritesheet("/images/gizmos.png",
                new Spritesheet(ResourceFlyWeight.getTexture("/images/gizmos.png"),
                        24, 48, 3, 0));
        ResourceFlyWeight.getTexture("/images/blendImage2.png");

        // load sounds
        ResourceFlyWeight.addSound("/sounds/main-theme-overworld.ogg", true);
        ResourceFlyWeight.addSound("/sounds/flagpole.ogg", false);
        ResourceFlyWeight.addSound("/sounds/break_block.ogg", false);
        ResourceFlyWeight.addSound("/sounds/bump.ogg", false);
        ResourceFlyWeight.addSound("/sounds/coin.ogg", false);
        ResourceFlyWeight.addSound("/sounds/gameover.ogg", false);
        ResourceFlyWeight.addSound("/sounds/jump-small.ogg", false);
        ResourceFlyWeight.addSound("/sounds/mario_die.ogg", false);
        ResourceFlyWeight.addSound("/sounds/pipe.ogg", false);
        ResourceFlyWeight.addSound("/sounds/powerup.ogg", false);
        ResourceFlyWeight.addSound("/sounds/powerup_appears.ogg", false);
        ResourceFlyWeight.addSound("/sounds/stage_clear.ogg", false);
        ResourceFlyWeight.addSound("/sounds/stomp.ogg", false);
        ResourceFlyWeight.addSound("/sounds/kick.ogg", false);
        ResourceFlyWeight.addSound("/sounds/invincible.ogg", false);

        for (GameObject go : scene.getGameObjects()) {
            SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
            if (spr != null) {
                Texture texture = spr.getTexture();
                if (texture != null) {
                    spr.setTexture(ResourceFlyWeight.getTexture(texture.getPath()));
                }
            }
        }

        for (GameObject go : scene.getGameObjects()) {
            StateMachine stateMachine = go.getComponent(StateMachine.class);
            if (stateMachine != null) {
                stateMachine.refreshTextures();
            }
        }
    }

    @Override
    public void imGui() {
        ImGui.begin("Level Editor Stuff");
        levelEditorStuff.imGui();
        ImGui.end();

        ImGui.begin("Objects");
        if (ImGui.beginTabBar("WindowTabBar")) {
            if (ImGui.beginTabItem("Blocks")) {
                ImVec2 windowPos = new ImVec2();
                ImGui.getWindowPos(windowPos);
                ImVec2 windowSize = new ImVec2();
                ImGui.getWindowSize(windowSize);
                ImVec2 itemSpacing = new ImVec2();
                ImGui.getStyle().getItemSpacing(itemSpacing);

                float windowX2 = windowPos.x + windowSize.x;
                for (int i = 0; i < sprites.size(); i++) {
                    if (i == 34) continue;
                    if (i >= 38 && i < 61) continue;

                    Sprite sprite = sprites.getSprite(i);
                    float spriteWidth = sprite.getWidth() * 3;
                    float spriteHeight = sprite.getHeight() * 3;
                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.getTexCoords();

                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight,
                            texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) { // [0].x, [0].y, [2].x, [2].y
                        GameObject go = Prefabs.generateSpriteObject(sprite, 0.25f, 0.25f);
                        RigidBody2D rb = new RigidBody2D();
                        rb.setBodyType(BodyType.STATIC);
                        go.addComponent(rb);
                        Box2DCollider bc = new Box2DCollider();
                        bc.setHalfSize(new Vector2f(0.25f, 0.25f));
                        go.addComponent(bc);
                        go.addComponent(new Ground());
                        if (i == 12) {
                            go.addComponent(new BreakableBlock());
                        }
                        levelEditorStuff.getComponent(MouseControls.class).drag(go);
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
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Prefabs")) {
                Spritesheet sprites = ResourceFlyWeight.getSpritesheet("/images/spritesheet.png");
                Sprite sprite = sprites.getSprite(0);
                float spriteWidth = sprite.getWidth() * 2;
                float spriteHeight = sprite.getHeight() * 2;
                int id = sprite.getTexId();
                Vector2f[] texCoords = sprite.getTexCoords();

                if (ImGui.imageButton(id, spriteWidth, spriteHeight,
                        texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject go = Prefabs.generateMario();
                    levelEditorStuff.getComponent(MouseControls.class).drag(go);
                }
                ImGui.sameLine();

                sprites = ResourceFlyWeight.getSpritesheet("/images/items.png");
                sprite = sprites.getSprite(0);
                spriteWidth = sprite.getWidth() * 2;
                spriteHeight = sprite.getHeight() * 2;
                id = sprite.getTexId();
                texCoords = sprite.getTexCoords();

                if (ImGui.imageButton(id, spriteWidth, spriteHeight,
                        texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject go = Prefabs.generateQuestionBlock();
                    levelEditorStuff.getComponent(MouseControls.class).drag(go);
                }
                ImGui.sameLine();
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Sounds")) {
                Collection<Sound> sounds = ResourceFlyWeight.getAllSounds();
                for (Sound sound : sounds) {
                    File tmp = new File(sound.getPath());
                    if (ImGui.button(tmp.getName())) {
                        if (!sound.isPlaying()) {
                            sound.play();
                        } else {
                            sound.stop();
                        }
                    }

                    if (ImGui.getContentRegionAvailX() > 100) {
                        ImGui.sameLine();
                    }
                }
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }
        ImGui.end();
    }
}

