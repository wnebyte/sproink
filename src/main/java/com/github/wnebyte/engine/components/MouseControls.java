package com.github.wnebyte.engine.components;

import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.core.event.MouseListener;
import com.github.wnebyte.engine.core.window.Window;
import com.github.wnebyte.engine.util.Constants;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component {

    private GameObject draggableObject = null;

    public void drag(GameObject go) {
        this.draggableObject = go;
        Window.getScene().addGameObjectToScene(go);
    }

    public void drop() {
        this.draggableObject = null;
    }

    @Override
    public void update(float dt) {
        if (draggableObject == null)
            return;

        draggableObject.transform.position.x = MouseListener.getOrthoX();
        draggableObject.transform.position.y = MouseListener.getOrthoY();
        draggableObject.transform.position.x =
                (int)(draggableObject.transform.position.x / Constants.GRID_WIDTH) * Constants.GRID_WIDTH;
        draggableObject.transform.position.y =
                (int)(draggableObject.transform.position.y / Constants.GRID_HEIGHT) * Constants.GRID_HEIGHT;

        if (MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            drop();
        }
    }
}
