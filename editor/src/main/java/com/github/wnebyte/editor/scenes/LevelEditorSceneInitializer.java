package com.github.wnebyte.editor.scenes;

import com.github.wnebyte.sproink.core.scene.Scene;
import com.github.wnebyte.sproink.core.scene.SceneInitializer;
import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.renderer.Texture;
import com.github.wnebyte.sproink.util.Assets;
import com.github.wnebyte.sproink.components.*;
import com.github.wnebyte.editor.components.EditorCamera;
import com.github.wnebyte.editor.components.GizmoSystem;
import com.github.wnebyte.editor.components.KeyControls;
import com.github.wnebyte.editor.components.MouseControls;

public class LevelEditorSceneInitializer implements SceneInitializer {

    private GameObject levelEditorStuff;

    @Override
    public void init(Scene scene) {
        Spritesheet gizmos = Assets.getSpritesheet("../assets/images/spritesheets/gizmos.png");
        levelEditorStuff = Scene.createGameObject("LevelEditorStuff");
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
        Assets.addSpritesheet("../assets/images/spritesheets/gizmos.png",
                () -> new Spritesheet(Assets.getTexture("../assets/images/spritesheets/gizmos.png"),
                        24, 48, 3, 0));
        // load sounds
        /*
        Assets.addSound("/sounds/main-theme-overworld.ogg", true);
        Assets.addSound("/sounds/flagpole.ogg", false);
        Assets.addSound("/sounds/break_block.ogg", false);
        Assets.addSound("/sounds/bump.ogg", false);
        Assets.addSound("/sounds/coin.ogg", false);
        Assets.addSound("/sounds/gameover.ogg", false);
        Assets.addSound("/sounds/jump-small.ogg", false);
        Assets.addSound("/sounds/mario_die.ogg", false);
        Assets.addSound("/sounds/pipe.ogg", false);
        Assets.addSound("/sounds/powerup.ogg", false);
        Assets.addSound("/sounds/powerup_appears.ogg", false);
        Assets.addSound("/sounds/stage_clear.ogg", false);
        Assets.addSound("/sounds/stomp.ogg", false);
        Assets.addSound("/sounds/kick.ogg", false);
        Assets.addSound("/sounds/invincible.ogg", false);
         */

        for (GameObject go : scene.getGameObjects()) {
            SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
            if (spr != null) {
                spr.setTexture(Assets.getTexture(spr.getTexture().getPath()));
            }
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

