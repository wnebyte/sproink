package com.github.wnebyte.engine.components;

import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.core.event.MouseListener;
import com.github.wnebyte.engine.core.window.Window;
import com.github.wnebyte.engine.util.Constants;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component {

    private GameObject holdingObject = null;

    public void pickupObject(GameObject go) {
        this.holdingObject = go;
        Window.getScene().addGameObjectToScene(go);
    }

    public void place() {
        this.holdingObject = null;
    }

    @Override
    public void update(float dt) {
        if (holdingObject != null) {
            holdingObject.transform.position.x = MouseListener.getOrthoX() - 16;
            holdingObject.transform.position.y = MouseListener.getOrthoY() - 16;
            holdingObject.transform.position.x =
                    (int)(holdingObject.transform.position.x / Constants.GRID_WIDTH) * Constants.GRID_WIDTH;
            holdingObject.transform.position.y =
                    (int)(holdingObject.transform.position.y / Constants.GRID_HEIGHT) * Constants.GRID_HEIGHT;

            if (MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                place();
            }
        }
    }
}
