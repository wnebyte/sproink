package com.github.wnebyte.editor.components;

import java.util.Set;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import com.github.wnebyte.editor.ui.PropertiesWindow;
import com.github.wnebyte.sproink.util.Log;
import com.github.wnebyte.editor.util.Settings;
import com.github.wnebyte.sproink.core.Window;
import com.github.wnebyte.sproink.core.Scene;
import com.github.wnebyte.sproink.core.GameObject;
import com.github.wnebyte.sproink.core.Component;
import com.github.wnebyte.sproink.core.MouseListener;
import com.github.wnebyte.sproink.renderer.DebugDraw;
import com.github.wnebyte.sproink.components.NonPickable;
import com.github.wnebyte.sproink.components.SpriteRenderer;
import com.github.wnebyte.util.Sets;
import static com.github.wnebyte.sproink.core.MouseListener.isDragging;
import static com.github.wnebyte.sproink.core.MouseListener.isMouseButtonDown;
import static com.github.wnebyte.sproink.core.KeyListener.isKeyPressed;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component {

    private static final String TAG = "MouseControls";

    private static final Vector4f DEFAULT_COLOR
            = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    private static final Vector4f DRAG_COLOR
            = new Vector4f(0.8f, 0.8f, 0.8f, 0.8f);

    private GameObject draggable = null;

    private float debounceTime = 0.2f;

    private float debounce = debounceTime;

    private boolean boxSelectSet = false;

    private Vector2f boxSelectStart = new Vector2f();

    private Vector2f boxSelectEnd = new Vector2f();

    public void drag(GameObject go) {
        if (draggable != null) {
            draggable.destroy();
        }
        draggable = go;
        draggable.getComponent(SpriteRenderer.class).setColor(DRAG_COLOR);
        draggable.addComponent(new NonPickable());
        Window.getScene().addGameObjectToScene(go);
    }

    public void drop() {
        GameObject go = draggable.copy();
        go.getComponent(SpriteRenderer.class).setColor(DEFAULT_COLOR);
        go.removeComponent(NonPickable.class);
        Window.getScene().addGameObjectToScene(go);
    }

    @Override
    public void editorUpdate(float dt) {
        debounce -= dt;
        PropertiesWindow props = Window.getImGuiLayer().getWindow(PropertiesWindow.class);
        Scene scene = Window.getScene();
        
        if (draggable != null) {
            float x = MouseListener.getWorldX();
            float y = MouseListener.getWorldY();
            // clamp the go's position
            draggable.transform.position.x =
                    ((int)Math.floor(x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH) +
                            Settings.GRID_WIDTH / 2.0f;
            draggable.transform.position.y =
                    ((int)Math.floor(y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT) +
                            Settings.GRID_HEIGHT / 2.0f;

            if (isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                float halfWidth  = (Settings.GRID_WIDTH / 2.0f);
                float halfHeight = (Settings.GRID_HEIGHT / 2.0f);
                if (isDragging() &&
                        !blockInSquare(draggable.transform.position.x - halfWidth,
                                draggable.transform.position.y - halfHeight)) {
                    drop();
                } else if (!isDragging() && debounce < 0) {
                    drop();
                    debounce = debounceTime;
                }
            }
            if (isKeyPressed(GLFW_KEY_ESCAPE)) {
                draggable.destroy();
                draggable = null;
            }
        }
        // picking
        else if (!isDragging() && isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            int id = Window.getPickingTexture().readPixel(x, y);
            GameObject go = scene.getGameObject(id);
            if (go != null && go.getComponent(NonPickable.class) == null) {
                props.setActiveGameObject(go);
               // Log.i(TAG, "ID: '%d'", id);
            } else if (go == null && !isDragging()) {
                props.clearSelected();
            }
            debounce = debounceTime;
        }
        // multi picking
        else if (isDragging() && isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
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

            float[] ids = Window.getPickingTexture().readPixels(
                    new Vector2i(screenStartX, screenStartY),
                    new Vector2i(screenEndX, screenEndY));
            Set<Integer> uniqueIds = Sets.of(ids);

            for (Integer id : uniqueIds) {
                GameObject go = Window.getScene().getGameObject(id);
                if (go != null && go.getComponent(NonPickable.class) == null) {
                    props.addActiveGameObject(go);
                }
            }

        }
    }

    private boolean blockInSquare(float x, float y) {
        Vector2f start = new Vector2f(x, y);
        Vector2f end = new Vector2f(start).add(new Vector2f(Settings.GRID_WIDTH, Settings.GRID_HEIGHT));
        Vector2f startScreenf = MouseListener.world2Screen(start);
        Vector2f endScreenf = MouseListener.world2Screen(end);
        Vector2i startScreen = new Vector2i((int)startScreenf.x + 2, (int)startScreenf.y + 2);
        Vector2i endScreen = new Vector2i((int)endScreenf.x - 2, (int)endScreenf.y - 2);
        float[] ids = Window.getPickingTexture().readPixels(startScreen, endScreen);

        for (int i = 0; i < ids.length; i++) {
            float id = ids[i];
            if (id >= 0) {
                GameObject go = Window.getScene().getGameObject((int)id);
                if (go.getComponent(NonPickable.class) == null) {
                    return true;
                }
            }
        }

        return false;
    }
}
