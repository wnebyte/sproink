package com.github.wnebyte.engine.components;

import com.github.wnebyte.engine.core.event.MouseListener;
import com.github.wnebyte.engine.editor.PropertiesWindow;

public class ScaleGizmo extends Gizmo {

    public ScaleGizmo(Sprite scaleSprite, PropertiesWindow propertiesWindow) {
        super(scaleSprite, propertiesWindow);
    }

    @Override
    public void editorUpdate(float dt) {
        if (activeGameObject != null) {
            if (xAxisActive && !yAxisActive) {
                activeGameObject.transform.scale.x -= 0;
            } else if (yAxisActive) {
                activeGameObject.transform.scale.y -= 0;
            }
        }

        super.editorUpdate(dt);
    }
}
