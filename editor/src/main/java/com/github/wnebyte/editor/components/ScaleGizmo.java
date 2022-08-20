package com.github.wnebyte.editor.components;

import com.github.wnebyte.sproink.components.Sprite;
import com.github.wnebyte.editor.ui.PropertiesWindow;

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
