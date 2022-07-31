package com.github.wnebyte.engine.components;

import org.joml.Vector4f;
import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.core.event.KeyListener;
import com.github.wnebyte.engine.core.event.MouseListener;
import com.github.wnebyte.engine.core.window.Window;
import com.github.wnebyte.engine.util.Settings;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component {

    private GameObject draggable = null;

    private float debounceTime = 0.05f;

    private float debounce = debounceTime;

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
        if (draggable != null && debounce <= 0) {
            draggable.transform.position.x = MouseListener.getWorldX();
            draggable.transform.position.y = MouseListener.getWorldY();
            draggable.transform.position.x =
                    ((int)Math.floor(draggable.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH) +
                            Settings.GRID_WIDTH / 2.0f;
            draggable.transform.position.y =
                    ((int)Math.floor(draggable.transform.position.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT) +
                            Settings.GRID_HEIGHT / 2.0f;

            if (MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                drop();
                debounce = debounceTime;
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
                draggable.destroy();
                draggable = null;
            }
        }
    }
}
