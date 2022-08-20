package com.github.wnebyte.editor.components;

import java.util.List;
import java.util.ArrayList;
import org.joml.Vector2f;
import com.github.wnebyte.sproink.components.StateMachine;
import com.github.wnebyte.sproink.core.ecs.Component;
import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.core.window.Window;
import com.github.wnebyte.sproink.util.Settings;
import com.github.wnebyte.editor.ui.PropertiesWindow;
import static com.github.wnebyte.sproink.core.event.KeyListener.isKeyPressed;
import static com.github.wnebyte.sproink.core.event.KeyListener.keyBeginPress;
import static org.lwjgl.glfw.GLFW.*;

public class KeyControls extends Component {

    private float debounce = 0.0f;

    private float debounceTime = 0.2f;

    @Override
    public void editorUpdate(float dt) {
        debounce -= dt;

        PropertiesWindow props = Window.getImGuiLayer().getWindow(PropertiesWindow.class);
        GameObject activeGameObject = props.getActiveGameObject();
        List<GameObject> activeGameObjects = props.getActiveGameObjects();
        float multiplier = isKeyPressed(GLFW_KEY_LEFT_SHIFT) ? 0.1f : 1.0f;

        if (isKeyPressed(GLFW_KEY_LEFT_CONTROL) && keyBeginPress(GLFW_KEY_D) &&
                activeGameObject != null) {
            GameObject copy = activeGameObject.copy();
            Window.getScene().addGameObjectToScene(copy);
            copy.transform.position.add(new Vector2f(Settings.GRID_WIDTH, 0.0f));
            props.setActiveGameObject(copy);
            if (copy.getComponent(StateMachine.class) != null) {
                copy.getComponent(StateMachine.class).refreshTextures();
            }
            System.out.println("(Debug): Copy");
        }
        else if (isKeyPressed(GLFW_KEY_LEFT_CONTROL) && keyBeginPress(GLFW_KEY_D) &&
                activeGameObjects.size() > 1) {
            List<GameObject> gameObjects = new ArrayList<>(activeGameObjects);
            props.clearSelected();
            for (GameObject go : gameObjects) {
                GameObject copy = go.copy();
                Window.getScene().addGameObjectToScene(copy);
                props.addActiveGameObject(copy);
                if (copy.getComponent(StateMachine.class) != null) {
                    copy.getComponent(StateMachine.class).refreshTextures();
                }
            }
        }
        else if (keyBeginPress(GLFW_KEY_DELETE)) {
            for (GameObject go : activeGameObjects) {
                go.destroy();
            }
            props.clearSelected();
        } else if (isKeyPressed(GLFW_KEY_PAGE_DOWN) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.zIndex--;
            }
        } else if (isKeyPressed(GLFW_KEY_PAGE_UP) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.zIndex++;
            }
        } else if (isKeyPressed(GLFW_KEY_UP) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.position.y += Settings.GRID_HEIGHT * multiplier;
            }
        } else if (isKeyPressed(GLFW_KEY_DOWN) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.position.y -= Settings.GRID_HEIGHT * multiplier;
            }
        } else if (isKeyPressed(GLFW_KEY_LEFT) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.position.x -= Settings.GRID_WIDTH * multiplier;
            }
        } else if (isKeyPressed(GLFW_KEY_RIGHT) && debounce < 0) {
            debounce = debounceTime;
            for (GameObject go : activeGameObjects) {
                go.transform.position.x += Settings.GRID_WIDTH * multiplier;
            }
        }
    }
}
