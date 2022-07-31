package com.github.wnebyte.engine.components;

import java.util.List;
import java.util.ArrayList;
import org.joml.Vector2f;
import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.core.event.KeyListener;
import com.github.wnebyte.engine.core.window.Window;
import com.github.wnebyte.engine.util.Settings;
import com.github.wnebyte.engine.editor.PropertiesWindow;
import static org.lwjgl.glfw.GLFW.*;

public class KeyControls extends Component {

    @Override
    public void editorUpdate(float dt) {
        PropertiesWindow propertiesWindow = Window.getImGuiLayer().getPropertiesWindow();
        GameObject activeGameObject = propertiesWindow.getActiveGameObject();
        List<GameObject> activeGameObjects = propertiesWindow.getActiveGameObjects();

        // CTRL + D
        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                KeyListener.keyBeginPress(GLFW_KEY_D) && activeGameObject != null) {
            GameObject newObject = activeGameObject.copy();
            Window.getScene().addGameObjectToScene(newObject);
            newObject.transform.position.add(new Vector2f(Settings.GRID_WIDTH, 0.0f));
            propertiesWindow.setActiveGameObject(newObject);
            System.out.println("(Debug): Copy");
        } // CTRL + D
        else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                KeyListener.keyBeginPress(GLFW_KEY_D) && activeGameObjects.size() > 1) {
            List<GameObject> gameObjects = new ArrayList<>(activeGameObjects);
            propertiesWindow.clearSelected();
            for (GameObject go : gameObjects) {
                GameObject copy = go.copy();
                Window.getScene().addGameObjectToScene(copy);
                propertiesWindow.addActiveGameObject(copy);
            }
        } // DELETE
        else if (KeyListener.keyBeginPress(GLFW_KEY_DELETE)) {
            for (GameObject go : activeGameObjects) {
                go.destroy();
            }
            propertiesWindow.clearSelected();
        }
    }
}
