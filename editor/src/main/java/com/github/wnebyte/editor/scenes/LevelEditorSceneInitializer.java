package com.github.wnebyte.editor.scenes;

import com.github.wnebyte.sproink.core.scene.Scene;
import com.github.wnebyte.sproink.core.scene.SceneInitializer;
import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.renderer.Texture;
import com.github.wnebyte.sproink.util.AssetFlyWeight;
import com.github.wnebyte.sproink.components.*;
import com.github.wnebyte.editor.components.EditorCamera;
import com.github.wnebyte.editor.components.GizmoSystem;
import com.github.wnebyte.editor.components.KeyControls;
import com.github.wnebyte.editor.components.MouseControls;

public class LevelEditorSceneInitializer implements SceneInitializer {

    private GameObject levelEditorStuff;

    @Override
    public void init(Scene scene) {
        Spritesheet gizmos = AssetFlyWeight.getSpritesheet("../assets/images/spritesheets/gizmos.png");
        levelEditorStuff = scene.createGameObject("LevelEditorStuff");
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
        // load spritesheets
        AssetFlyWeight.addSpritesheet("../assets/images/spritesheets/gizmos.png",
                new Spritesheet(AssetFlyWeight.getTexture("../assets/images/spritesheets/gizmos.png"),
                        24, 48, 3, 0));
        // load sounds
        /*
        AssetFlyWeight.addSound("/sounds/main-theme-overworld.ogg", true);
        AssetFlyWeight.addSound("/sounds/flagpole.ogg", false);
        AssetFlyWeight.addSound("/sounds/break_block.ogg", false);
        AssetFlyWeight.addSound("/sounds/bump.ogg", false);
        AssetFlyWeight.addSound("/sounds/coin.ogg", false);
        AssetFlyWeight.addSound("/sounds/gameover.ogg", false);
        AssetFlyWeight.addSound("/sounds/jump-small.ogg", false);
        AssetFlyWeight.addSound("/sounds/mario_die.ogg", false);
        AssetFlyWeight.addSound("/sounds/pipe.ogg", false);
        AssetFlyWeight.addSound("/sounds/powerup.ogg", false);
        AssetFlyWeight.addSound("/sounds/powerup_appears.ogg", false);
        AssetFlyWeight.addSound("/sounds/stage_clear.ogg", false);
        AssetFlyWeight.addSound("/sounds/stomp.ogg", false);
        AssetFlyWeight.addSound("/sounds/kick.ogg", false);
        AssetFlyWeight.addSound("/sounds/invincible.ogg", false);
         */

        for (GameObject go : scene.getGameObjects()) {
            SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
            if (spr != null) {
                Texture texture = spr.getTexture();
                if (texture != null) {
                    spr.setTexture(AssetFlyWeight.getTexture(texture.getPath()));
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

