package com.github.wnebyte.engine.components;

import org.joml.Vector2f;
import org.joml.Vector4f;
import com.github.wnebyte.engine.core.Prefabs;
import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.core.event.MouseListener;
import com.github.wnebyte.engine.core.window.Window;
import com.github.wnebyte.engine.editor.PropertiesWindow;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Gizmo extends Component {

    private final Vector4f xAxisColor = new Vector4f(1, 0.3f, 0.3f, 1);

    private final Vector4f xAxisColorHover = new Vector4f(1, 0, 0, 1);

    private final Vector4f yAxisColor = new Vector4f(0.3f, 1, 0.3f, 1);

    private final Vector4f yAxisColorHover = new Vector4f(0, 1, 0, 1);

    private final Vector2f xAxisOffset = new Vector2f(64, -5);

    private final Vector2f yAxisOffset = new Vector2f(16, 61);

    private final int gizmoWidth = 16;

    private final int gizmoHeight = 48;

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
        xAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16, 48);
        yAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16, 48);
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
        xAxisObject.setNoSerialize();
        yAxisObject.setNoSerialize();
    }

    @Override
    public void update(float dt) {
        if (!using) return;

        activeGameObject = propertiesWindow.getActiveGameObject();
        if (activeGameObject != null) {
            setActive();
        } else {
            setInactive();
            return;
        }

        boolean xAxisHot = checkXHoverState();
        boolean yAxisHot = checkYHoverState();

        if ((xAxisHot || xAxisActive) && MouseListener.isDragging() && MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            xAxisActive = true;
            yAxisActive = false;
        } else if ((yAxisHot || yAxisActive) && MouseListener.isDragging() && MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
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

    private boolean checkXHoverState() {
        Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
        if (mousePos.x <= xAxisObject.transform.position.x &&
                mousePos.x >= xAxisObject.transform.position.x - gizmoHeight &&
                mousePos.y >= xAxisObject.transform.position.y &&
                mousePos.y <= xAxisObject.transform.position.y + gizmoWidth) {
            xAxisSprite.setColor(xAxisColorHover);
            return true;
        } else {
            xAxisSprite.setColor(xAxisColor);
            return false;
        }
    }

    private boolean checkYHoverState() {
        Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
        if (mousePos.x <= yAxisObject.transform.position.x &&
                mousePos.x >= yAxisObject.transform.position.x - gizmoWidth &&
                mousePos.y <= yAxisObject.transform.position.y &&
                mousePos.y >= yAxisObject.transform.position.y - gizmoHeight) {
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
