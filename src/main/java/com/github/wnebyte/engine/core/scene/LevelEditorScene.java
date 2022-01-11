package com.github.wnebyte.engine.core.scene;

import org.joml.Vector2f;
import com.github.wnebyte.engine.core.camera.Camera;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.core.ecs.Transform;
import com.github.wnebyte.engine.components.SpriteRenderer;
import com.github.wnebyte.engine.util.ResourceFlyWeight;

public class LevelEditorScene extends Scene {

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f(-250, -100));
        GameObject obj1 = new GameObject("Object 1", new Transform(
                new Vector2f(100, 100), new Vector2f(256, 256)
        ));
        obj1.addComponent(new SpriteRenderer(ResourceFlyWeight.getTexture("/images/testImage.png")));
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Object 2", new Transform(
                new Vector2f(400, 100), new Vector2f(256, 256)
        ));
        obj2.addComponent(new SpriteRenderer(ResourceFlyWeight.getTexture("/images/testImage2.png")));
        this.addGameObjectToScene(obj2);
        loadResources();
    }

    private void loadResources() {
        ResourceFlyWeight.getShader("/shaders/default.glsl");
    }

    @Override
    public void update(float dt) {
        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }
}

