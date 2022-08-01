package com.github.wnebyte.engine.components;

import java.util.List;
import java.util.ArrayList;
import org.joml.Vector2f;
import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.core.window.Window;
import com.github.wnebyte.engine.util.Settings;
import com.github.wnebyte.engine.editor.PropertiesWindow;
import static com.github.wnebyte.engine.core.event.KeyListener.isKeyPressed;
import static com.github.wnebyte.engine.core.event.KeyListener.keyBeginPress;
import static org.lwjgl.glfw.GLFW.*;

public class KeyControls extends Component {

    @Override
    public void editorUpdate(float dt) {
        PropertiesWindow props = Window.getImGuiLayer().getPropertiesWindow();
        GameObject activeGameObject = props.getActiveGameObject();
        List<GameObject> activeGameObjects = props.getActiveGameObjects();

        if (isKeyPressed(GLFW_KEY_LEFT_CONTROL) && keyBeginPress(GLFW_KEY_D) &&
                activeGameObject != null) {
            GameObject newObject = activeGameObject.copy();
            Window.getScene().addGameObjectToScene(newObject);
            newObject.transform.position.add(new Vector2f(Settings.GRID_WIDTH, 0.0f));
            props.setActiveGameObject(newObject);
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
            }
        }
        else if (keyBeginPress(GLFW_KEY_DELETE)) {
            for (GameObject go : activeGameObjects) {
                go.destroy();
            }
            props.clearSelected();
        }
    }
}
