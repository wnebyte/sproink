package com.github.wnebyte.engine.components;

import org.joml.Vector2f;
import org.joml.Vector3f;
import com.github.wnebyte.engine.core.camera.Camera;
import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.core.window.Window;
import com.github.wnebyte.engine.renderer.DebugDraw;
import com.github.wnebyte.engine.util.Settings;

public class GridLines extends Component {

    @Override
    public void update(float dt) {
        Camera camera = Window.getScene().getCamera();
        Vector2f cameraPos = camera.getPosition();
        Vector2f projectionSize = camera.getProjectionSize();

        int firstX = ((int)(cameraPos.x / Settings.GRID_WIDTH) - 1) * Settings.GRID_HEIGHT;
        int firstY = ((int)(cameraPos.y / Settings.GRID_HEIGHT) - 1) * Settings.GRID_HEIGHT;

        int numVtLines = (int)(projectionSize.x * camera.getZoom() / Settings.GRID_WIDTH) + 2;
        int numHzLines = (int)(projectionSize.y * camera.getZoom() / Settings.GRID_HEIGHT) + 2;

        int height = (int)(projectionSize.y * camera.getZoom()) + Settings.GRID_HEIGHT * 2;
        int width  = (int)(projectionSize.x * camera.getZoom()) + Settings.GRID_WIDTH * 2;

        int maxLines = Math.max(numVtLines, numHzLines);
        Vector3f color = new Vector3f(0.2f, 0.2f, 0.2f);

        for (int i = 0; i < maxLines; i++) {
            int x = firstX + (Settings.GRID_WIDTH * i);
            int y = firstY + (Settings.GRID_HEIGHT * i);

            if (i < numVtLines) {
                DebugDraw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + height), color);
            }

            if (i < numHzLines) {
                DebugDraw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y), color);
            }
        }
    }
}
