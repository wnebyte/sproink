package com.github.wnebyte.engine.core.scene;

import com.github.wnebyte.engine.components.RigidBody;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;
import com.github.wnebyte.engine.core.ecs.*;
import com.github.wnebyte.engine.core.camera.Camera;
import com.github.wnebyte.engine.components.Sprite;
import com.github.wnebyte.engine.components.Spritesheet;
import com.github.wnebyte.engine.components.SpriteRenderer;
import com.github.wnebyte.engine.util.ResourceFlyWeight;

public class LevelEditorScene extends Scene {

    private GameObject obj1;

    private SpriteRenderer obj1Spr;

    private Spritesheet sprites;

    @Override
    public void init() {
        loadResources();
        this.camera = new Camera(new Vector2f(-250, -100));
        if (levelLoaded) {
            this.activeGameObject = gameObjects.get(0);
            return;
        }

        sprites = ResourceFlyWeight.getSpritesheet("/images/spritesheet.png");

        obj1 = new GameObject("Object 1", new Transform(new Vector2f(200, 100),
                new Vector2f(256, 256)), -1
        );
        obj1Spr = new SpriteRenderer();
        obj1Spr.setColor(new Vector4f(1, 0, 0, 1));
        obj1.addComponent(obj1Spr);
        obj1.addComponent(new RigidBody());
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100),
                new Vector2f(256, 256)), -2
        );
        SpriteRenderer obj2SpriteRenderer = new SpriteRenderer();
        Sprite obj2Sprite = new Sprite();
        obj2Sprite.setTexture(ResourceFlyWeight.getTexture("/images/blendImage2.png"));
        obj2SpriteRenderer.setSprite(obj2Sprite);
        obj2.addComponent(obj2SpriteRenderer);
        this.addGameObjectToScene(obj2);
    }

    private void loadResources() {
        ResourceFlyWeight.getShader("/shaders/default.glsl");
        ResourceFlyWeight.addSpritesheet("/images/spritesheet.png",
                new Spritesheet(ResourceFlyWeight.getTexture("/images/spritesheet.png"),
                        16, 16, 26, 0));
        ResourceFlyWeight.getTexture("/images/blendImage2.png");
    }

    @Override
    public void update(float dt) {
        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }

    @Override
    public void imGui() {
        ImGui.begin("Test Window");
        ImGui.text("some random text");
        ImGui.end();
    }
}

