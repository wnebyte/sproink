package com.github.wnebyte.engine.components;

import java.util.Set;
import java.util.HashSet;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import com.github.wnebyte.engine.core.scene.Scene;
import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.core.event.KeyListener;
import com.github.wnebyte.engine.core.event.MouseListener;
import com.github.wnebyte.engine.core.window.Window;
import com.github.wnebyte.engine.renderer.DebugDraw;
import com.github.wnebyte.engine.editor.PropertiesWindow;
import com.github.wnebyte.engine.util.Settings;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component {

    private GameObject draggable = null;

    private float debounceTime = 0.2f   ;

    private float debounce = debounceTime;

    private boolean boxSelectSet = false;

    private Vector2f boxSelectStart = new Vector2f();

    private Vector2f boxSelectEnd = new Vector2f();

    public void drag(GameObject go) {
        if (draggable != null) {
            draggable.destroy();
        }
        draggable = go;
        draggable.getComponent(SpriteRenderer.class).setColor(new Vector4f(0.8f, 0.8f, 0.8f, 0.5f));
        draggable.addComponent(new NonPickable());
        Window.getScene().addGameObjectToScene(go);
    }

    public void drop() {
        GameObject go = draggable.copy();
        if (go.getComponent(StateMachine.class) != null) {
            go.getComponent(StateMachine.class).refreshTextures();
        }
        go.getComponent(SpriteRenderer.class).setColor(new Vector4f(1, 1, 1, 1));
        go.removeComponent(NonPickable.class);
        Window.getScene().addGameObjectToScene(go);
    }

    @Override
    public void editorUpdate(float dt) {
        debounce -= dt;
        PropertiesWindow props = Window.getImGuiLayer().getPropertiesWindow();
        Scene scene = Window.getScene();

        if (draggable != null) {
            draggable.transform.position.x = MouseListener.getWorldX();
            draggable.transform.position.y = MouseListener.getWorldY();
            draggable.transform.position.x =
                    ((int)Math.floor(draggable.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH) +
                            Settings.GRID_WIDTH / 2.0f;
            draggable.transform.position.y =
                    ((int)Math.floor(draggable.transform.position.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT) +
                            Settings.GRID_HEIGHT / 2.0f;

            if (MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                float halfWidth  = (Settings.GRID_WIDTH / 2.0f);
                float halfHeight = (Settings.GRID_HEIGHT / 2.0f);
                if (MouseListener.isDragging() &&
                        !blockInSquare(draggable.transform.position.x - halfWidth,
                                draggable.transform.position.y - halfHeight)) {
                    drop();
                } else if (!MouseListener.isDragging() && debounce < 0) {
                    drop();
                    debounce = debounceTime;
                }
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
                draggable.destroy();
                draggable = null;
            }
        } else if (!MouseListener.isDragging() &&
                MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            int id = props.getPickingTexture().readPixel(x, y);
            GameObject pickedObject = scene.getGameObject(id);
            if (pickedObject != null && pickedObject.getComponent(NonPickable.class) == null) {
                props.setActiveGameObject(pickedObject);
                System.out.printf("(Debug): ID: '%d'%n", id);
            } else if (pickedObject == null && !MouseListener.isDragging()) {
                props.clearSelected();
            }
            debounce = 0.2f;
        } else if (MouseListener.isDragging() && MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            if (!boxSelectSet) {
                props.clearSelected();
                boxSelectStart = MouseListener.getScreen();
                boxSelectSet = true;
            }
            boxSelectEnd = MouseListener.getScreen();
            Vector2f boxSelectStartWorld = MouseListener.screen2World(boxSelectStart);
            Vector2f boxSelectEndWorld = MouseListener.screen2World(boxSelectEnd);
            Vector2f halfSize =
                    (new Vector2f(boxSelectEndWorld).sub(boxSelectStartWorld)).mul(0.5f);
            DebugDraw.addBox2D(
                    (new Vector2f(boxSelectStartWorld)).add(halfSize),
                    new Vector2f(halfSize).mul(2.0f),
                    0.0f);
        } else if (boxSelectSet) {
            boxSelectSet = false;
            int screenStartX = (int)boxSelectStart.x;
            int screenStartY = (int)boxSelectStart.y;
            int screenEndX   = (int)boxSelectEnd.x;
            int screenEndY   = (int)boxSelectEnd.y;
            boxSelectStart.zero();
            boxSelectEnd.zero();

            if (screenEndX < screenStartX) {
                int tmp = screenStartX;
                screenStartX = screenEndX;
                screenEndX = tmp;
            }
            if (screenEndY < screenStartY) {
                int tmp = screenStartY;
                screenStartY = screenEndY;
                screenEndY = tmp;
            }

            float[] ids = props.getPickingTexture().readPixels(
                    new Vector2i(screenStartX, screenStartY),
                    new Vector2i(screenEndX, screenEndY));
            Set<Integer> uniqueIds = new HashSet<>();
            for (float id : ids) {
                uniqueIds.add((int)id);
            }

            for (Integer id : uniqueIds) {
                GameObject pickedObject = Window.getScene().getGameObject(id);
                if (pickedObject != null && pickedObject.getComponent(NonPickable.class) == null) {
                    props.addActiveGameObject(pickedObject);
                }
            }

        }
    }

    private boolean blockInSquare(float x, float y) {
        PropertiesWindow propertiesWindow = Window.getImGuiLayer().getPropertiesWindow();
        Vector2f start = new Vector2f(x, y);
        Vector2f end = new Vector2f(start).add(new Vector2f(Settings.GRID_WIDTH, Settings.GRID_HEIGHT));
        Vector2f startScreenf = MouseListener.world2Screen(start);
        Vector2f endScreenf = MouseListener.world2Screen(end);
        Vector2i startScreen = new Vector2i((int)startScreenf.x + 2, (int)startScreenf.y + 2);
        Vector2i endScreen = new Vector2i((int)endScreenf.x - 2, (int)endScreenf.y - 2);
        float[] ids = propertiesWindow.getPickingTexture().readPixels(startScreen, endScreen);

        for (int i = 0; i < ids.length; i++) {
            float id = ids[i];
            if (id >= 0) {
                GameObject picked = Window.getScene().getGameObject((int)id);
                if (picked.getComponent(NonPickable.class) == null) {
                    return true;
                }
            }
        }

        return false;
    }
}
