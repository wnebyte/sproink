package com.github.wnebyte.engine.renderer;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import org.joml.Vector2f;
import org.joml.Vector3f;
import com.github.wnebyte.engine.core.window.Window;
import com.github.wnebyte.engine.core.camera.Camera;
import com.github.wnebyte.engine.util.JMath;
import com.github.wnebyte.engine.util.ResourceFlyWeight;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {

    private static final int MAX_LINES = 3000;

    private static final List<Line2D> lines = new ArrayList<>();

    private static final float[] vertexArray = new float[MAX_LINES * 6 * 2];

    private static final Shader shader = ResourceFlyWeight.getShader("/shaders/debugLine2D.glsl");

    private static int vaoID;

    private static int vboID;

    private static boolean started = false;

    public static void start() {
        // Generate the VAO
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create the VBO and buffer some memory
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Enable the vertex array attributes
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glLineWidth(2.0f);
    }

    public static void beginFrame() {
        if (!started) {
            start();
            started = true;
        }

        // Remove deadlines
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).beginFrame() < 0) {
                lines.remove(i);
                i--;
            }
        }
    }

    public static void draw() {
        if (lines.size() <= 0) {
            return;
        }

        int index = 0;
        for (Line2D line : lines) {
            for (int i = 0; i < 2; i++) {
                Vector2f pos = (i == 0) ? line.getStart() : line.getEnd();
                Vector3f color = line.getColor();

                // Load pos
                vertexArray[index] = pos.x;
                vertexArray[index + 1] = pos.y;
                vertexArray[index + 2] = -10.0f;

                // Load color
                vertexArray[index + 3] = color.x;
                vertexArray[index + 4] = color.y;
                vertexArray[index + 5] = color.z;
                index += 6;
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, lines.size() * 6 * 2));

        // Use our shader
        shader.use();
        shader.uploadMatrix4f(Shader.U_PROJECTION, Window.getScene().getCamera().getProjectionMatrix());
        shader.uploadMatrix4f(Shader.U_VIEW, Window.getScene().getCamera().getViewMatrix());

        // Bind the VAO
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // Draw the batch
        glDrawArrays(GL_LINES, 0, lines.size() * 6 * 2);

        // Disable location
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        // Unbind shader
        shader.detach();
    }

    public static void addLine2D(Vector2f start, Vector2f end) {
        addLine2D(start, end, new Vector3f(0, 1, 0), 1);
    }

    public static void addLine2D(Vector2f start, Vector2f end, Vector3f color) {
        addLine2D(start, end, color, 1);
    }

    public static void addLine2D(Vector2f start, Vector2f end, Vector3f color, int ftl) {
        Camera camera = Window.getScene().getCamera();
        Vector2f cameraLeft = new Vector2f(camera.getPosition())
                .add(new Vector2f(-2.0f, -2.0f));
        Vector2f cameraRight = new Vector2f(camera.getPosition())
                .add(new Vector2f(camera.getProjectionSize())
                .mul(camera.getZoom()))
                .add(new Vector2f(4.0f, 4.0f));
        boolean lineInView =
                ((start.x >= cameraLeft.x && start.x <= cameraRight.x) &&
                        (start.y >= cameraLeft.y && start.y <= cameraRight.y)) ||
                ((end.x >= cameraLeft.x && end.x <= cameraRight.x) &&
                        (end.y >= cameraLeft.y && end.y <= cameraRight.y));
        if (lines.size() >= MAX_LINES || !lineInView) {
            return;
        }
        lines.add(new Line2D(start, end, color, ftl));
    }

    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation) {
        addBox2D(center, dimensions, rotation, new Vector3f(0, 1, 0));
    }

    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation, Vector3f color) {
        addBox2D(center, dimensions, rotation, color, 1);
    }

    public static void addBox2D(Vector2f center, Vector2f dimensions, float rotation, Vector3f color, int ftl) {
        Vector2f min = new Vector2f(center).sub(new Vector2f(dimensions).mul(0.5f));
        Vector2f max = new Vector2f(center).add(new Vector2f(dimensions).mul(0.5f));

        Vector2f[] vertices = {
                new Vector2f(min.x, min.y), new Vector2f(min.x, max.y),
                new Vector2f(max.x, max.y), new Vector2f(max.x, min.y)
        };

        if (rotation != 0.0f) {
            for (Vector2f vert : vertices) {
                JMath.rotate(vert, rotation, center);
            }
        }

        addLine2D(vertices[0], vertices[1], color, ftl);
        addLine2D(vertices[0], vertices[3], color, ftl);
        addLine2D(vertices[1], vertices[2], color, ftl);
        addLine2D(vertices[2], vertices[3], color, ftl);
    }

    public static void addCircle(Vector2f center, float radius) {
        addCircle(center, radius, new Vector3f(0, 1f, 0));
    }

    public static void addCircle(Vector2f center, float radius, Vector3f color) {
        addCircle(center, radius, color, 1);
    }

    public static void addCircle(Vector2f center, float radius, Vector3f color, int ftl) {
        Vector2f[] points = new Vector2f[20];
        int increment = 360 / points.length;
        int currentAngle = 0;

        for (int i = 0; i < points.length; i++) {
            Vector2f tmp = new Vector2f(0, radius);
            JMath.rotate(tmp, currentAngle, new Vector2f());
            points[i] = new Vector2f(tmp).add(center);

            if (i > 0) {
                addLine2D(points[i - 1], points[i], color, ftl);
            }
            currentAngle += increment;
        }

        addLine2D(points[points.length - 1], points[0], color, ftl);
    }
}
