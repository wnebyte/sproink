package com.github.wnebyte.editor.scenes;

import com.github.wnebyte.sproink.core.Scene;
import com.github.wnebyte.sproink.core.SceneInitializer;
import com.github.wnebyte.sproink.core.GameObject;
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
    }

    @Override
    public void imGui() {
        // do nothing
    }
}

