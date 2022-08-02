package com.github.wnebyte.engine.core.scene;

import java.util.List;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.joml.Vector2f;
import com.github.wnebyte.engine.util.Settings;
import com.github.wnebyte.engine.core.Transform;
import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.core.camera.Camera;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.renderer.Renderer;
import com.github.wnebyte.engine.physics2d.Physics2D;

public class Scene {
    private Camera camera;

    private boolean isRunning;

    private final Renderer renderer;

    private final List<GameObject> gameObjects;

    private final List<GameObject> pendingGameObject;

    private final Physics2D physics2d;

    private final SceneInitializer sceneInitializer;

    public Scene(SceneInitializer sceneInitializer) {
        this.sceneInitializer = sceneInitializer;
        this.physics2d = new Physics2D();
        this.renderer = new Renderer();
        this.gameObjects = new ArrayList<>();
        this.pendingGameObject = new ArrayList<>();
        this.isRunning = false;
    }

    public void init() {
        camera = new Camera(new Vector2f(0, 0));
        sceneInitializer.loadResources(this);
        sceneInitializer.init(this);
    }

    public void start() {
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.start();
            renderer.add(go);
            physics2d.add(go);
        }
        isRunning = true;
    }

    public void editorUpdate(float dt) {
        camera.adjustProjection();

        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.editorUpdate(dt);

            if (go.isDead()) {
                gameObjects.remove(i);
                renderer.destroyGameObject(go);
                physics2d.destroyGameObject(go);
                i--;
            }
        }

        for (GameObject go : pendingGameObject) {
            gameObjects.add(go);
            go.start();
            renderer.add(go);
            physics2d.add(go);
        }

        pendingGameObject.clear();
    }

    public void update(float dt) {
        camera.adjustProjection();
        physics2d.update(dt);

        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.update(dt);

            if (go.isDead()) {
                gameObjects.remove(i);
                renderer.destroyGameObject(go);
                physics2d.destroyGameObject(go);
                i--;
            }
        }

        for (GameObject go : pendingGameObject) {
            gameObjects.add(go);
            go.start();
            renderer.add(go);
            physics2d.add(go);
        }

        pendingGameObject.clear();
    }

    public void imGui() {
        sceneInitializer.imGui();
    }

    public void render() {
        renderer.render();
    }

    public void destroy() {
        for (GameObject go : gameObjects) {
            go.destroy();
        }
    }

    public void addGameObjectToScene(GameObject go) {
        if (!isRunning) {
            gameObjects.add(go);
        } else {
            pendingGameObject.add(go);
        }
    }

    public GameObject createGameObject(String name) {
        GameObject go = new GameObject(name);
        go.addComponent(new Transform());
        go.transform = go.getComponent(Transform.class);
        return go;
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public Physics2D getPhysics2d() {
        return physics2d;
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

    public void save() {
        try {
            FileWriter writer = new FileWriter("level.txt");
            List<GameObject> out = new ArrayList<>();
            for (GameObject go : gameObjects) {
                if (go.isSerialize()) {
                    out.add(go);
                }
            }
            writer.write(Settings.GSON.toJson(out));
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

            GameObject[] objs = Settings.GSON.fromJson(inFile, GameObject[].class);
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
        }

    }
}
