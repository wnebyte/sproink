package com.github.wnebyte.sproink.scenes;

import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.core.scene.Scene;
import com.github.wnebyte.sproink.core.scene.SceneInitializer;
import com.github.wnebyte.sproink.components.SpriteRenderer;
import com.github.wnebyte.sproink.components.StateMachine;
import com.github.wnebyte.sproink.util.Assets;

public class LevelSceneInitializer implements SceneInitializer {

    @Override
    public void init(Scene scene) {
        // do nothing
    }

    @Override
    public void loadResources(Scene scene) {
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
        // do nothing
    }
}
