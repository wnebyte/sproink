package com.github.wnebyte.editor.scene;

import imgui.ImGui;
import com.github.wnebyte.sproink.core.scene.Scene;
import com.github.wnebyte.sproink.core.scene.SceneInitializer;
import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.renderer.Texture;
import com.github.wnebyte.sproink.util.ResourceFlyWeight;
import com.github.wnebyte.sproink.components.*;
import com.github.wnebyte.editor.components.EditorCamera;
import com.github.wnebyte.editor.components.GizmoSystem;
import com.github.wnebyte.editor.components.KeyControls;
import com.github.wnebyte.editor.components.MouseControls;

public class LevelEditorSceneInitializer implements SceneInitializer {

    private GameObject levelEditorStuff;

    @Override
    public void init(Scene scene) {
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
        /*
        ImGui.begin("Level Editor Stuff");
        levelEditorStuff.imGui();
        ImGui.end();
         */
    }
}

