package com.github.wnebyte.sproink.core.scene;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.google.gson.Gson;
import org.joml.Vector2f;
import com.github.wnebyte.sproink.core.Transform;
import com.github.wnebyte.sproink.core.camera.Camera;
import com.github.wnebyte.sproink.core.ecs.Component;
import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.physics2d.Physics2D;
import com.github.wnebyte.sproink.renderer.Renderer;
import com.github.wnebyte.sproink.components.Sprite;
import com.github.wnebyte.sproink.components.SpriteRenderer;

public class Scene {

    public static GameObject createGameObject(String name) {
        GameObject go = new GameObject(name);
        go.addComponent(new Transform());
        go.transform = go.getComponent(Transform.class);
        return go;
    }

    public static GameObject createGameObject(Sprite sprite, String name, float width, float height) {
        GameObject go = new GameObject(name);
        go.addComponent(new Transform());
        go.transform = go.getComponent(Transform.class);
        go.transform.scale.x = width;
        go.transform.scale.y = height;
        SpriteRenderer spr = new SpriteRenderer();
        spr.setSprite(sprite);
        go.addComponent(spr);
        return go;
    }

    private String path;

    private Camera camera;

    private boolean isRunning;

    private final Renderer renderer;

    private final List<GameObject> gameObjects;

    private final List<GameObject> pendingGameObjects;

    private final Physics2D physics2d;

    private final SceneInitializer sceneInitializer;

    public Scene(SceneInitializer sceneInitializer) {
        this("../assets/scenes/scene.json", sceneInitializer);
    }

    public Scene(String path, SceneInitializer sceneInitializer) {
        this.sceneInitializer = sceneInitializer;
        this.physics2d = new Physics2D();
        this.renderer = new Renderer();
        this.gameObjects = new ArrayList<>();
        this.pendingGameObjects = new ArrayList<>();
        this.isRunning = false;
        this.path = path;
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

        for (GameObject go : pendingGameObjects) {
            gameObjects.add(go);
            go.start();
            renderer.add(go);
            physics2d.add(go);
        }

        pendingGameObjects.clear();
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

        for (GameObject go : pendingGameObjects) {
            gameObjects.add(go);
            go.start();
            renderer.add(go);
            physics2d.add(go);
        }

        pendingGameObjects.clear();
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
            pendingGameObjects.add(go);
        }
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public Physics2D getPhysics() {
        return physics2d;
    }

    public GameObject getGameObject(int id) {
        return gameObjects.stream()
                .filter(go -> go.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public GameObject getGameObject(String name) {
        return gameObjects.stream()
                .filter(go -> go.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public <T extends Component> GameObject getGameObject(Class<T> componentClass) {
        return gameObjects.stream()
                .filter(go -> go.getComponent(componentClass) != null)
                .findFirst()
                .orElse(null);
    }

    public Camera getCamera() {
        return camera;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        File file = new File(path);
        return file.getName();
    }

    public void save(Gson gson) {
        try {
            FileWriter writer = new FileWriter(path);
            List<GameObject> out = new ArrayList<>();
            for (GameObject go : gameObjects) {
                if (go.isSerialize()) {
                    out.add(go);
                }
            }
            writer.write(gson.toJson(out));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(Gson gson) {
        String in = "";

        try {
            in = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            System.err.println("Error: (Scene) " + e.getMessage());
        }

        if (!in.equals("")) {
            int maxGoId = -1;
            int maxCompId = -1;

            GameObject[] objs = gson.fromJson(in, GameObject[].class);
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
