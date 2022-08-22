package com.github.wnebyte.editor.components;

import org.joml.Vector2f;
import org.joml.Vector4f;
import com.github.wnebyte.sproink.core.Prefabs;
import com.github.wnebyte.sproink.core.ecs.Component;
import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.core.event.MouseListener;
import com.github.wnebyte.sproink.core.window.Window;
import com.github.wnebyte.sproink.components.NonPickable;
import com.github.wnebyte.sproink.components.Sprite;
import com.github.wnebyte.sproink.components.SpriteRenderer;
import com.github.wnebyte.editor.ui.PropertiesWindow;
import static com.github.wnebyte.sproink.core.event.KeyListener.isKeyPressed;
import static com.github.wnebyte.sproink.core.event.KeyListener.keyBeginPress;
import static com.github.wnebyte.sproink.core.event.MouseListener.isDragging;
import static com.github.wnebyte.sproink.core.event.MouseListener.isMouseButtonDown;
import static org.lwjgl.glfw.GLFW.*;

public class Gizmo extends Component {

    private final float gizmoWidth = 16f / 80f;

    private final float gizmoHeight = 48f / 80f;

    private final Vector4f xAxisColor = new Vector4f(1, 0.3f, 0.3f, 1);

    private final Vector4f xAxisColorHover = new Vector4f(1, 0, 0, 1);

    private final Vector4f yAxisColor = new Vector4f(0.3f, 1, 0.3f, 1);

    private final Vector4f yAxisColorHover = new Vector4f(0, 1, 0, 1);

    private final Vector2f xAxisOffset = new Vector2f(24f / 80f, -6 / 80f);

    private final Vector2f yAxisOffset = new Vector2f(-7f / 80f, 21 / 80f);

    protected boolean xAxisActive = false;

    protected boolean yAxisActive = false;

    private boolean using = false;

    private final GameObject xAxisObject;

    private final GameObject yAxisObject;

    private final SpriteRenderer xAxisSprite;

    private final SpriteRenderer yAxisSprite;

    private final PropertiesWindow propertiesWindow;

    protected GameObject activeGameObject = null;

    public Gizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        this.propertiesWindow = propertiesWindow;
        xAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
        yAxisObject = Prefabs.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
        xAxisSprite = xAxisObject.getComponent(SpriteRenderer.class);
        yAxisSprite = yAxisObject.getComponent(SpriteRenderer.class);
        xAxisObject.addComponent(new NonPickable());
        yAxisObject.addComponent(new NonPickable());
        Window.getScene().addGameObjectToScene(xAxisObject);
        Window.getScene().addGameObjectToScene(yAxisObject);
    }

    @Override
    public void start() {
        xAxisObject.transform.rotation = 90;
        yAxisObject.transform.rotation = 180;
        xAxisObject.transform.zIndex = 100;
        yAxisObject.transform.zIndex = 100;
        xAxisObject.setNoSerialize();
        yAxisObject.setNoSerialize();
    }

    @Override
    public void update(float dt) {
        if (using) {
            setInactive();
        }
        xAxisSprite.setColor(new Vector4f(0, 0, 0, 0));
        yAxisSprite.setColor(new Vector4f(0, 0, 0, 0));
    }

    @Override
    public void editorUpdate(float dt) {
        if (!using) return;

        activeGameObject = propertiesWindow.getActiveGameObject();
        if (activeGameObject != null) {
            setActive();

            // Todo: move this into it's own keyEditorBinding component
            if (isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                    keyBeginPress(GLFW_KEY_D)) {
                GameObject newObject = activeGameObject.copy();
                Window.getScene().addGameObjectToScene(newObject);
                newObject.transform.position.add(new Vector2f(0.1f, 0.1f));
                propertiesWindow.setActiveGameObject(newObject);
                System.out.println("(Debug): Copy");
                return;
            } else if (keyBeginPress(GLFW_KEY_DELETE)) {
                activeGameObject.destroy();
                setInactive();
                propertiesWindow.setActiveGameObject(null);
                return;
            }
        } else {
            setInactive();
            return;
        }

        boolean xAxisHot = checkXHoverState();
        boolean yAxisHot = checkYHoverState();

        if ((xAxisHot || xAxisActive) && isDragging() && isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            xAxisActive = true;
            yAxisActive = false;
        } else if ((yAxisHot || yAxisActive) && isDragging() && isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            yAxisActive = true;
            xAxisActive = false;
        } else {
            xAxisActive = false;
            yAxisActive = false;
        }

        if (activeGameObject != null) {
            xAxisObject.transform.position.set(activeGameObject.transform.position);
            yAxisObject.transform.position.set(activeGameObject.transform.position);
            xAxisObject.transform.position.add(xAxisOffset);
            yAxisObject.transform.position.add(yAxisOffset);
        }
    }

    public boolean isxAxisActive() {
        return xAxisActive;
    }

    public boolean isyAxisActive() {
        return yAxisActive;
    }

    private boolean checkXHoverState() {
        Vector2f mousePos = new Vector2f(MouseListener.getWorldX(), MouseListener.getWorldY());
        if (mousePos.x <= xAxisObject.transform.position.x + (gizmoHeight / 2.0f) &&
                mousePos.x >= xAxisObject.transform.position.x - (gizmoWidth / 2.0f) &&
                mousePos.y >= xAxisObject.transform.position.y - (gizmoHeight / 2.0f) &&
                mousePos.y <= xAxisObject.transform.position.y + (gizmoWidth / 2.0f)) {
            xAxisSprite.setColor(xAxisColorHover);
            return true;
        } else {
            xAxisSprite.setColor(xAxisColor);
            return false;
        }
    }

    private boolean checkYHoverState() {
        Vector2f mousePos = new Vector2f(MouseListener.getWorldX(), MouseListener.getWorldY());
        if (mousePos.x <= yAxisObject.transform.position.x + (gizmoWidth / 2.0f) &&
                mousePos.x >= yAxisObject.transform.position.x - (gizmoWidth / 2.0f) &&
                mousePos.y <= yAxisObject.transform.position.y + (gizmoHeight / 2.0f) &&
                mousePos.y >= yAxisObject.transform.position.y - (gizmoHeight / 2.0f)) {
            yAxisSprite.setColor(yAxisColorHover);
            return true;
        } else {
            yAxisSprite.setColor(yAxisColor);
            return false;
        }
    }

    private void setActive() {
        xAxisSprite.setColor(xAxisColor);
        yAxisSprite.setColor(yAxisColor);
    }

    private void setInactive() {
        activeGameObject = null;
        xAxisSprite.setColor(new Vector4f(0, 0, 0,0));
        yAxisSprite.setColor(new Vector4f(0, 0, 0,0));
    }

    public void setUsing() {
        using = true;
    }

    public void setNotUsing() {
        using = false;
        setInactive();
    }
}
