package com.github.wnebyte.engine.components;

import org.joml.Vector2f;
import org.joml.Vector3f;
import com.github.wnebyte.engine.core.camera.Camera;
import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.core.window.Window;
import com.github.wnebyte.engine.renderer.DebugDraw;
import com.github.wnebyte.engine.util.Constants;

public class GridLines extends Component {

    @Override
    public void update(float dt) {
        Camera camera = Window.getScene().getCamera();
        Vector2f cameraPos = camera.getPosition();
        Vector2f projectionSize = camera.getProjectionSize();

        int firstX = ((int)(cameraPos.x / Constants.GRID_WIDTH) - 1) * Constants.GRID_HEIGHT;
        int firstY = ((int)(cameraPos.y / Constants.GRID_HEIGHT) - 1) * Constants.GRID_HEIGHT;

        int numVtLines = (int)(projectionSize.x * camera.getZoom() / Constants.GRID_WIDTH) + 2;
        int numHzLines = (int)(projectionSize.y * camera.getZoom() / Constants.GRID_HEIGHT) + 2;

        int height = (int)(projectionSize.y * camera.getZoom()) + Constants.GRID_HEIGHT * 2;
        int width  = (int)(projectionSize.x * camera.getZoom()) + Constants.GRID_WIDTH * 2;

        int maxLines = Math.max(numVtLines, numHzLines);
        Vector3f color = new Vector3f(0.2f, 0.2f, 0.2f);

        for (int i = 0; i < maxLines; i++) {
            int x = firstX + (Constants.GRID_WIDTH * i);
            int y = firstY + (Constants.GRID_HEIGHT * i);

            if (i < numVtLines) {
                DebugDraw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + height), color);
            }

            if (i < numHzLines) {
                DebugDraw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y), color);
            }
        }
    }
}
