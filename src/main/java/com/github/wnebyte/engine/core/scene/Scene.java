package com.github.wnebyte.engine.core.scene;

import java.util.List;
import java.util.ArrayList;
import com.github.wnebyte.engine.core.camera.Camera;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.renderer.Renderer;
import imgui.ImGui;

public abstract class Scene {

    protected Camera camera;

    protected Renderer renderer = new Renderer();

    protected List<GameObject> gameObjects = new ArrayList<>();

    protected GameObject activeGameObject = null;

    private boolean isRunning = false;

    public void init() {

    }

    public void start() {
        for (GameObject go : gameObjects) {
            go.start();
            renderer.add(go);
        }
        isRunning = true;
    }

    public abstract void update(float dt);

    public void addGameObjectToScene(GameObject go) {
        if (!isRunning) {
            gameObjects.add(go);
        } else {
            gameObjects.add(go);
            go.start();
            renderer.add(go);
        }
    }

    public Camera getCamera() {
        return camera;
    }

    public void sceneImGui() {
        if (activeGameObject != null) {
            ImGui.begin("Inspector");
            activeGameObject.imGui();
            ImGui.end();
        }

        imGui();
    }

    public void imGui() {}
}
