package com.github.wnebyte.editor.components;

import com.github.wnebyte.sproink.core.Component;
import com.github.wnebyte.sproink.core.KeyListener;
import com.github.wnebyte.sproink.core.Window;
import com.github.wnebyte.sproink.components.Spritesheet;
import com.github.wnebyte.editor.ui.PropertiesWindow;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;

public class GizmoSystem extends Component {

    private Spritesheet gizmos;

    private int usingGizmo = 0;

    public GizmoSystem(Spritesheet gizmos) {
        this.gizmos = gizmos;
    }

    @Override
    public void start() {
        gameObject.addComponent(new TranslateGizmo(gizmos.getSprite(1),
                Window.getImGuiLayer().getWindow(PropertiesWindow.class)));
        gameObject.addComponent(new ScaleGizmo(gizmos.getSprite(2),
                Window.getImGuiLayer().getWindow(PropertiesWindow.class)));
    }

    @Override
    public void editorUpdate(float dt) {
        if (usingGizmo == 0) {
            gameObject.getComponent(TranslateGizmo.class).setUsing();
            gameObject.getComponent(ScaleGizmo.class).setNotUsing();
        } else if (usingGizmo == 1) {
            gameObject.getComponent(TranslateGizmo.class).setNotUsing();
            gameObject.getComponent(ScaleGizmo.class).setUsing();
        }

        // Todo: consider moving to key controls
        if (KeyListener.isKeyPressed(GLFW_KEY_E)) {
            usingGizmo = 0;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_R)) {
            usingGizmo = 1;
        }
    }
}
