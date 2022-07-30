package com.github.wnebyte.engine.core.scene;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;

import com.github.wnebyte.engine.core.Transform;
import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.core.ecs.ComponentTypeAdapter;
import com.github.wnebyte.engine.core.ecs.GameObjectTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import com.github.wnebyte.engine.core.camera.Camera;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.renderer.Renderer;

public abstract class Scene {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Component.class, new ComponentTypeAdapter())
            .registerTypeAdapter(GameObject.class, new GameObjectTypeAdapter())
            .setPrettyPrinting()
            .create();

    protected Camera camera;

    protected Renderer renderer = new Renderer();

    protected List<GameObject> gameObjects = new ArrayList<>();

    protected boolean levelLoaded = false;

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

    public abstract void render();

    public void addGameObjectToScene(GameObject go) {
        if (!isRunning) {
            gameObjects.add(go);
        } else {
            gameObjects.add(go);
            go.start();
            renderer.add(go);
        }
    }

    public GameObject getGameObject(int id) {
        return gameObjects.stream()
                .filter(go -> go.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Camera getCamera() {
        return camera;
    }

    public void imGui() {}

    public GameObject createGameObject(String name) {
        GameObject go = new GameObject(name);
        go.addComponent(new Transform());
        go.transform = go.getComponent(Transform.class);
        return go;
    }

    public void saveExit() {
        try {
            FileWriter writer = new FileWriter("level.txt");
            List<GameObject> out = new ArrayList<>();
            for (GameObject go : gameObjects) {
                if (go.isSerialize()) {
                    out.add(go);
                }
            }
            writer.write(GSON.toJson(out));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        String inFile = "";

        try {
            inFile = new String(Files.readAllBytes(Paths.get("level.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!inFile.equals("")) {
            int maxGoId = -1;
            int maxCompId = -1;

            GameObject[] objs = GSON.fromJson(inFile, GameObject[].class);
            for (GameObject go : objs) {
                addGameObjectToScene(go);

                if (go.getId() > maxGoId) {
                    maxGoId = go.getId();
                }
                for (Component c : go.getAllComponents()) {
                    if (c.getId() > maxCompId) {
                        maxCompId = c.getId();
                    }
                }
            }
            maxGoId++;
            maxCompId++;
            GameObject.init(maxGoId);
            Component.init(maxCompId);
            this.levelLoaded = true;
        }

    }
}
