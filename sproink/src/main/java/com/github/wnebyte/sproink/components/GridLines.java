package com.github.wnebyte.sproink.components;

import org.joml.Vector2f;
import org.joml.Vector3f;
import com.github.wnebyte.sproink.core.Camera;
import com.github.wnebyte.sproink.core.Component;
import com.github.wnebyte.sproink.core.Window;
import com.github.wnebyte.sproink.renderer.DebugDraw;
import com.github.wnebyte.sproink.util.Settings;

public class GridLines extends Component {

    @Override
    public void editorUpdate(float dt) {
        Camera camera = Window.getScene().getCamera();
        Vector2f cameraPos = camera.getPosition();
        Vector2f projectionSize = camera.getProjectionSize();

        float firstX = ((int)(cameraPos.x / Settings.GRID_WIDTH)) * Settings.GRID_HEIGHT;
        float firstY = ((int)(cameraPos.y / Settings.GRID_HEIGHT)) * Settings.GRID_HEIGHT;

        int numVtLines = (int)(projectionSize.x * camera.getZoom() / Settings.GRID_WIDTH) + 2;
        int numHzLines = (int)(projectionSize.y * camera.getZoom() / Settings.GRID_HEIGHT) + 2;

        float height = (int)(projectionSize.y * camera.getZoom()) + (5 * Settings.GRID_HEIGHT);
        float width  = (int)(projectionSize.x * camera.getZoom()) + (5 * Settings.GRID_WIDTH);

        int maxLines = Math.max(numVtLines, numHzLines);
        Vector3f color = new Vector3f(0.2f, 0.2f, 0.2f);

        for (int i = 0; i < maxLines; i++) {
            float x = firstX + (Settings.GRID_WIDTH * i);
            float y = firstY + (Settings.GRID_HEIGHT * i);

            if (i < numVtLines) {
                DebugDraw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + height), color);
            }

            if (i < numHzLines) {
                DebugDraw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y), color);
            }
        }
    }
}
