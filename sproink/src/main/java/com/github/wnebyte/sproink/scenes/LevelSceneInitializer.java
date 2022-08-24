package com.github.wnebyte.sproink.scenes;

import com.github.wnebyte.sproink.core.scene.Scene;
import com.github.wnebyte.sproink.core.scene.SceneInitializer;
import com.github.wnebyte.sproink.renderer.Texture;
import com.github.wnebyte.sproink.util.AssetFlyWeight;
import com.github.wnebyte.sproink.util.ResourceFlyWeight;
import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.components.GameCamera;
import com.github.wnebyte.sproink.components.SpriteRenderer;
import com.github.wnebyte.sproink.components.Spritesheet;
import com.github.wnebyte.sproink.components.StateMachine;

public class LevelSceneInitializer implements SceneInitializer {

    @Override
    public void init(Scene scene) {
        GameObject gameCameraGo = scene.createGameObject("GameCamera");
        gameCameraGo.addComponent(new GameCamera(scene.getCamera()));
        gameCameraGo.start();
        scene.addGameObjectToScene(gameCameraGo);
    }

    @Override
    public void loadResources(Scene scene) {
        // load shaders
        AssetFlyWeight.getShader("../assets/shaders/default.glsl");

        /*
        AssetFlyWeight.addSpritesheet("../assets/images/spritesheets/decorationsAndBlocks.png",
                new Spritesheet(AssetFlyWeight.getTexture("../assets/images/spritesheets/decorationsAndBlocks.png"),
                        16, 16, 81, 0));
        AssetFlyWeight.addSpritesheet("../assets/images/spritesheet.png",
                new Spritesheet(AssetFlyWeight.getTexture("../assets/images/spritesheet.png"),
                        16, 16, 26, 0));
        AssetFlyWeight.addSpritesheet("../assets/images/turtle.png",
                new Spritesheet(AssetFlyWeight.getTexture("../assets/images/turtle.png"),
                        16, 24, 4, 0));
        AssetFlyWeight.addSpritesheet("../assets/images/bigSpritesheet.png",
                new Spritesheet(AssetFlyWeight.getTexture("../assets/images/bigSpritesheet.png"),
                        16, 32, 42, 0));
        AssetFlyWeight.addSpritesheet("../assets/images/spritesheets/pipes.png",
                new Spritesheet(AssetFlyWeight.getTexture("../assets/images/spritesheets/pipes.png"),
                        32, 32, 4, 0));
        AssetFlyWeight.addSpritesheet("../assets/images/items.png",
                new Spritesheet(AssetFlyWeight.getTexture("../assets/images/items.png"),
                        16, 16, 43, 0));
        AssetFlyWeight.addSpritesheet("../assets/images/gizmos.png",
                new Spritesheet(AssetFlyWeight.getTexture("../assets/images/gizmos.png"),
                        24, 48, 3, 0));
        AssetFlyWeight.getTexture("../assets/images/blendImage2.png");

        ResourceFlyWeight.addSound("../assets/sounds/main-theme-overworld.ogg", true);
        ResourceFlyWeight.addSound("../assets/sounds/flagpole.ogg", false);
        ResourceFlyWeight.addSound("../assets/sounds/break_block.ogg", false);
        ResourceFlyWeight.addSound("../assets/sounds/bump.ogg", false);
        ResourceFlyWeight.addSound("../assets/sounds/coin.ogg", false);
        ResourceFlyWeight.addSound("../assets/sounds/gameover.ogg", false);
        ResourceFlyWeight.addSound("../assets/sounds/jump-small.ogg", false);
        ResourceFlyWeight.addSound("../assets/sounds/mario_die.ogg", false);
        ResourceFlyWeight.addSound("../assets/sounds/pipe.ogg", false);
        ResourceFlyWeight.addSound("../assets/sounds/powerup.ogg", false);
        ResourceFlyWeight.addSound("../assets/sounds/powerup_appears.ogg", false);
        ResourceFlyWeight.addSound("../assets/sounds/stage_clear.ogg", false);
        ResourceFlyWeight.addSound("../assets/sounds/stomp.ogg", false);
        ResourceFlyWeight.addSound("../assets/sounds/kick.ogg", false);
        ResourceFlyWeight.addSound("../assets/sounds/invincible.ogg", false);
         */

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
        // do nothing
    }
}
