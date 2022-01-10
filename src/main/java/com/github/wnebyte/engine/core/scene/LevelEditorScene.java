package com.github.wnebyte.engine.core.scene;

import com.github.wnebyte.engine.core.camera.Camera;
import com.github.wnebyte.engine.components.SpriteRenderer;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.core.ecs.Transform;
import com.github.wnebyte.engine.renderer.Texture;
import com.github.wnebyte.engine.util.ResourceFlyWeight;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class LevelEditorScene extends Scene {

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f(-250, 0));
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

    /*
        int xOffset = 10;
        int yOffset = 10;

        float totalWidth = (float)(600 - xOffset * 2);
        float totalHeight = (float)(300 - yOffset * 2);
        float sizeX = totalWidth / 100.0f;
        float sizeY = totalHeight / 100.0f;

        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                float xPos = xOffset + (x * sizeX);
                float yPos = yOffset + (y * sizeY);

                GameObject go = new GameObject("Obj" + x + "" + y,
                        new Transform(new Vector2f(xPos, yPos), new Vector2f(sizeX, sizeY)));
                go.addComponent(new SpriteRenderer(new Vector4f(xPos / totalWidth, yPos / totalHeight, 1, 1)));
                this.addGameObjectToScene(go);
            }
        }
     */
}

