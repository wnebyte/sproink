package com.github.wnebyte.sproink.core;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.github.wnebyte.sproink.fonts.SFont;
import org.joml.Vector2f;
import com.github.wnebyte.sproink.renderer.Shader;
import com.github.wnebyte.sproink.renderer.Renderer;
import com.github.wnebyte.sproink.components.Sprite;
import com.github.wnebyte.sproink.components.SpriteRenderer;
import com.github.wnebyte.sproink.physics2d.Physics2D;
import com.github.wnebyte.sproink.util.Log;
import com.github.wnebyte.sproink.util.Settings;

public class Scene {

    private static final String TAG = "Scene";

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

    private Camera camera;

    private boolean isRunning;

    private final String path;

    private final String name;

    private final Renderer renderer;

    private final List<GameObject> gameObjects;

    private final List<GameObject> pendingGameObjects;

    private final Physics2D physics2d;

    private final SceneInitializer sceneInitializer;

    public Scene(SceneInitializer sceneInitializer) {
        this(null, sceneInitializer);
    }

    public Scene(String path, SceneInitializer sceneInitializer) {
        this.physics2d = new Physics2D();
        this.renderer = new Renderer();
        this.gameObjects = new ArrayList<>();
        this.pendingGameObjects = new ArrayList<>();
        this.isRunning = false;
        this.sceneInitializer = sceneInitializer;
        this.path = path;
        this.name = (path == null) ? "Non-Serial-Scene.json" : new File(path).getName().split("[.]json")[0];
    }

    public void init() {
        camera = new Camera(new Vector2f(0, 0));
        sceneInitializer.loadResources(this);
        sceneInitializer.init(this);
    }

    public void start() {
        renderer.start();
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.start();
            renderer.add(go);
            physics2d.add(go);
        }
        isRunning = true;
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

        for (int i = 0; i < pendingGameObjects.size(); i++) {
            GameObject go = pendingGameObjects.get(i);
            gameObjects.add(go);
            go.start();
            renderer.add(go);
            physics2d.add(go);
        }

        pendingGameObjects.clear();
    }

    public void imGui() {

    }

    public void render() {
        renderer.render();
    }

    public void render(Shader shader) {
        renderer.setShader(shader);
        render();
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

    public void setShader(Shader shader) {
        renderer.setShader(shader);
    }

    public Shader getShader() {
        return renderer.getShader();
    }

    public void setFontShader(Shader shader) {
        renderer.setFontShader(shader);
    }

    public Shader getFontShader() {
        return renderer.getFontShader();
    }

    public void setFont(SFont font) {
        renderer.setFont(font);
    }

    public SFont getFont() {
        return renderer.getFont();
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void save() {
        if (path == null || !new File(path).exists()) {
           // Log.w(TAG, "Scene: '%s' can't be saved", name);
            return;
        }

        try {
            FileWriter writer = new FileWriter(path);
            List<GameObject> out = new ArrayList<>();
            for (GameObject obj : this.gameObjects) {
                if (obj.isSerialize()) {
                    out.add(obj);
                }
            }
            writer.write(Settings.GSON.toJson(out));
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        if (path == null || !new File(path).exists()) {
           // Log.w(TAG, "Scene: '%s' can't be loaded", name);
            return;
        }

        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!inFile.equals("")) {
            int maxGoId   = -1;
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

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Scene)) return false;
        Scene scene = (Scene) o;
        return Objects.equals(scene.path, this.path);
     }

     @Override
     public int hashCode() {
        int result = 15;
        return result *
                5 +
                Objects.hashCode(this.path);
     }
}
