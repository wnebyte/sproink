package com.github.wnebyte.engine.components;

import org.joml.Vector2f;
import org.joml.Vector4f;
import com.github.wnebyte.engine.core.Prefabs;
import com.github.wnebyte.engine.core.window.Window;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.editor.PropertiesWindow;

public class TranslateGizmo extends Component {

    private Vector4f xAxisColor = new Vector4f(1, 0, 0, 1);

    private Vector4f xAxisColorHover = new Vector4f();

    private Vector4f yAxisColor = new Vector4f(0, 1, 0, 1);

    private Vector4f yAxisColorHover = new Vector4f();

    private Vector2f xAxisOffset = new Vector2f(64, -5);

    private Vector2f yAxisOffset = new Vector2f(16, 61);

    private GameObject xAxisObject;

    private GameObject yAxisObject;

    private SpriteRenderer xAxisSprite;

    private SpriteRenderer yAxisSprite;

    private GameObject activeGameObject = null;


    private PropertiesWindow propertiesWindow;

    public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        this.propertiesWindow = propertiesWindow;
        xAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16, 48);
        yAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16, 48);
        xAxisSprite = xAxisObject.getComponent(SpriteRenderer.class);
        yAxisSprite = yAxisObject.getComponent(SpriteRenderer.class);
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
        if (activeGameObject != null) {
            xAxisObject.transform.position.set(activeGameObject.transform.position);
            yAxisObject.transform.position.set(activeGameObject.transform.position);
            xAxisObject.transform.position.add(xAxisOffset);
            yAxisObject.transform.position.add(yAxisOffset);
        }

        activeGameObject = propertiesWindow.getActiveGameObject();
        if (activeGameObject != null) {
            setActive();
        } else {
            setInactive();
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
}
