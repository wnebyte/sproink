package com.github.wnebyte.engine.components;

import com.github.wnebyte.engine.editor.PropertiesWindow;

public class TranslateGizmo extends Gizmo {

    public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        super(arrowSprite, propertiesWindow);
    }

    @Override
    public void editorUpdate(float dt) {
        if (activeGameObject != null) {
            if (xAxisActive && !yAxisActive) {
                activeGameObject.transform.position.x -= 0;
            } else if (yAxisActive) {
                activeGameObject.transform.position.y -= 0;
            }
        }

        super.editorUpdate(dt);
    }
}
