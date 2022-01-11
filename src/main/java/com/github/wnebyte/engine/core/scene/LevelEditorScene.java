package com.github.wnebyte.engine.core.scene;

import com.github.wnebyte.engine.components.Sprite;
import org.joml.Vector2f;
import com.github.wnebyte.engine.core.camera.Camera;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.core.ecs.Transform;
import com.github.wnebyte.engine.components.Spritesheet;
import com.github.wnebyte.engine.components.SpriteRenderer;
import com.github.wnebyte.engine.util.ResourceFlyWeight;

public class LevelEditorScene extends Scene {

    private GameObject obj1;

    private Spritesheet sprites;

    @Override
    public void init() {
        loadResources();
        this.camera = new Camera(new Vector2f(-250, -100));

        sprites = ResourceFlyWeight.getSpritesheet("/images/spritesheet.png");

        obj1 = new GameObject("Object 1", new Transform(
                new Vector2f(200, 100), new Vector2f(256, 256)), -1
        );
        obj1.addComponent(new SpriteRenderer(new Sprite(
                ResourceFlyWeight.getTexture("/images/blendImage1.png")
        )));

        GameObject obj2 = new GameObject("Object 2", new Transform(
                new Vector2f(400, 100), new Vector2f(256, 256)), -2
        );
        obj2.addComponent(new SpriteRenderer(new Sprite(
                ResourceFlyWeight.getTexture("/images/blendImage2.png")
        )));
        this.addGameObjectToScene(obj2);
        this.addGameObjectToScene(obj1);
    }

    private void loadResources() {
        ResourceFlyWeight.getShader("/shaders/default.glsl");
        ResourceFlyWeight.addSpritesheet("/images/spritesheet.png",
                new Spritesheet(ResourceFlyWeight.getTexture("/images/spritesheet.png"),
                        16, 16, 26, 0));
    }

    @Override
    public void update(float dt) {
        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }
}

