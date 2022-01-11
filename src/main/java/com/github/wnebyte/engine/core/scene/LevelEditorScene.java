package com.github.wnebyte.engine.core.scene;

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
                new Vector2f(100, 100), new Vector2f(256, 256)
        ));
        obj1.addComponent(new SpriteRenderer(sprites.getSprite(0)));
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Object 2", new Transform(
                new Vector2f(400, 100), new Vector2f(256, 256)
        ));
        obj2.addComponent(new SpriteRenderer(sprites.getSprite(10)));
        this.addGameObjectToScene(obj2);
    }

    private void loadResources() {
        ResourceFlyWeight.getShader("/shaders/default.glsl");
        ResourceFlyWeight.addSpritesheet("/images/spritesheet.png",
                new Spritesheet(ResourceFlyWeight.getTexture("/images/spritesheet.png"),
                        16, 16, 26, 0));
    }

    private int spriteIndex = 0;

    private float spriteFlipTime = 0.2f;

    private float spriteFlipTimeLeft = 0.0f;

    @Override
    public void update(float dt) {
        spriteFlipTimeLeft -= dt;
        if (spriteFlipTimeLeft <= 0) {
            spriteFlipTimeLeft = spriteFlipTime;
            spriteIndex++;
            if (spriteIndex > 4) {
                spriteIndex = 0;
            }
            obj1.getComponent(SpriteRenderer.class).setSprite(sprites.getSprite(spriteIndex));
        }

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }
}

