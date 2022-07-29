package com.github.wnebyte.engine.components;

import com.github.wnebyte.engine.core.event.KeyListener;
import org.joml.Vector2f;
import com.github.wnebyte.engine.core.event.MouseListener;
import com.github.wnebyte.engine.core.camera.Camera;
import com.github.wnebyte.engine.core.ecs.Component;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_DECIMAL;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public class EditorCamera extends Component {

    private float dragDebounce = 0.032f;

    private float dragSensitivity = 30.0f;

    private float scrollSensitivity = 0.1f;

    private boolean reset = false;

    private float lerpTime = 0.0f;

    private final Camera camera;

    private Vector2f origin;

    public EditorCamera(Camera camera) {
        this.camera = camera;
        this.origin = new Vector2f();
    }

    @Override
    public void update(float dt) {
        if (MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && dragDebounce > 0) {
            this.origin = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            dragDebounce -= dt;
           // return;
        } else if (MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            Vector2f delta = new Vector2f(mousePos).sub(origin);
            camera.getPosition().sub(delta.mul(dt).mul(dragSensitivity));
            origin.lerp(mousePos, dt);
        }

        if (dragDebounce <= 0.0f && !MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            dragDebounce = 0.1f;
        }

        if (MouseListener.getScrollY() != 0.0f) {
            float addValue = (float)Math.pow(Math.abs(MouseListener.getScrollY() * scrollSensitivity),
                    1 / camera.getZoom());
            addValue *= -Math.signum(MouseListener.getScrollY());
            camera.addZoom(addValue);
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_KP_DECIMAL)) {
            reset = true;
        }

        if (reset) {
            camera.getPosition().lerp(new Vector2f(), lerpTime);
            camera.setZoom(camera.getZoom() +
                    ((1.0f - camera.getZoom()) * lerpTime));
            lerpTime += 0.1f * dt;
            if (Math.abs(camera.getPosition().x) <= 5.0f &&
                    Math.abs(camera.getPosition().y) <= 5.0f) {
                lerpTime = 0.0f;
                camera.getPosition().set(0f, 0f);
                camera.setZoom(1.0f);
                reset = false;
            }
        }
    }
}