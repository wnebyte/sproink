package com.github.wnebyte.editor.components;

import com.github.wnebyte.sproink.components.Sprite;
import com.github.wnebyte.editor.ui.PropertiesWindow;
import com.github.wnebyte.sproink.core.MouseListener;

public class TranslateGizmo extends Gizmo {

    public TranslateGizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        super(arrowSprite, propertiesWindow);
    }

    @Override
    public void editorUpdate(float dt) {
        if (activeGameObject != null) {
            if (xAxisActive && !yAxisActive) {
                activeGameObject.transform.position.x -= MouseListener.getWorldDx();
            } else if (yAxisActive) {
                activeGameObject.transform.position.y -= MouseListener.getWorldDy();
            }
        }

        super.editorUpdate(dt);
    }
}
