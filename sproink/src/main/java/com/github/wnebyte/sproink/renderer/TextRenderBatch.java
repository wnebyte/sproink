package com.github.wnebyte.sproink.renderer;

import com.github.wnebyte.sproink.fonts.SFont;
import org.joml.Vector2f;
import org.joml.Vector3f;
import com.github.wnebyte.sproink.core.Window;
import com.github.wnebyte.sproink.fonts.CharInfo;
import com.github.wnebyte.sproink.components.TextRenderer;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class TextRenderBatch {

    //                              VAO
    // =============================================================
    // Pos                Color                     Tex Coords    //
    // float, float       float, float, float       float, float  //
    // =============================================================

    private static final String TAG = "TextRendererBatch";

    private static final int POS_SIZE = 2;

    private static final int COLOR_SIZE = 3;

    private static final int TEX_COORDS_SIZE = 2;

    private static final int POS_OFFSET = 0;

    private static final int COLOR_OFFSET = POS_OFFSET + (POS_SIZE * Float.BYTES);

    private static final int TEX_COORDS_OFFSET = COLOR_OFFSET + (COLOR_SIZE * Float.BYTES);

    private static final int VERTEX_SIZE = POS_SIZE + COLOR_SIZE + TEX_COORDS_SIZE;

    private static final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private static final int BATCH_SIZE = 100;

    private static int[] INDICES = {
            0, 1, 3,
            1, 2, 3
    };

    /*
    private float[] vertices = {
            // x, y,          r, g, b              tex Coords
            0.5f,  0.5f,     1.0f, 0.2f, 0.11f,    1.0f, 0.0f,
            0.5f, -0.5f,     1.0f, 0.2f, 0.11f,    1.0f, 1.0f,
            -0.5f, -0.5f,    1.0f, 0.2f, 0.11f,    0.0f, 1.0f,
            -0.5f,  0.5f,    1.0f, 0.2f, 0.11f,    0.0f, 0.0f
    };
     */

    private float[] vertices;

    private int vaoID;

    private int vboID;

    private int size;

    private Shader shader;

    private SFont font;

    public TextRenderBatch(Renderer renderer) {
        this.vertices = new float[BATCH_SIZE * VERTEX_SIZE];
        this.size = 0;
        this.shader = renderer.getFontShader();
        this.font = renderer.getFont();
    }

    public void start() {
        // Generate and bind a Vertex Array Object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, Float.BYTES * VERTEX_SIZE * BATCH_SIZE, GL_DYNAMIC_DRAW);

        // Create and upload the indices buffer
        int eboID = glGenBuffers();
        int[] indices = genIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Enable the buffer attribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);
    }

    public void flush() {
        // clear the buffer on the GPU, then upload the CPU contents, then draw
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        // Allocate some memory on the GPU
        glBufferData(GL_ARRAY_BUFFER, Float.BYTES * VERTEX_SIZE * BATCH_SIZE, GL_DYNAMIC_DRAW);
        // Upload vertex data to the GPU
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        // Draw the buffer that we just uploaded
        shader.use();
        // glActiveTexture selects which texture unit subsequent texture state calls will affect
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, font.getTextureId());
        shader.uploadTexture(Shader.U_FONT_TEXTURE, 0);
        shader.uploadMatrix4f(Shader.U_PROJECTION, Window.getScene().getCamera().getProjectionMatrix());

        glBindVertexArray(vaoID);
        glDrawElements(GL_TRIANGLES, size * 6, GL_UNSIGNED_INT, 0);

        // Reset batch for use on the next draw call
        size = 0;
        shader.detach();
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void addText(TextRenderer tr) {
        String text = tr.getText();
        float x = tr.gameObject.transform.position.x;
        float y = tr.gameObject.transform.position.y;
        float scale = tr.gameObject.transform.scale.x;
        Vector3f color = tr.getColor();
        addText(text, x, y, scale, color);
    }

    private void addText(String text, float x, float y, float scale, Vector3f color) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            CharInfo info = font.getCharacter(c);
            if (info.getWidth() == 0) {
                System.err.printf("Warning: (TextRenderBatch) Unknown character: '%c'%n", c);
                continue;
            }

            float xPos = x;
            float yPos = y;
            addCharacter(info, xPos, yPos, scale, color);
            x += info.getWidth() * scale;
        }
    }

    private void addCharacter(CharInfo info, float x, float y, float scale, Vector3f color) {
        // if we have no more room - flush and start with a fresh batch
        if (size >= BATCH_SIZE - 4) {
            flush();
        }

        // char info
        int width = info.getWidth();
        int height = info.getHeight();
        Vector2f[] texCoords = info.getTexCoords();

        // position
        float x0 = x;
        float y0 = y;
        float x1 = x + scale * width;
        float y1 = y + scale * height;

        // color
        /*
        float r = (float)((rgb >> 16) & 0xFF) / 255.0f;
        float g = (float)((rgb >> 8)  & 0xFF) / 255.0f;
        float b = (float)((rgb >> 0)  & 0xFF) / 255.0f;
         */

        // tex coords
        float ux0 = texCoords[0].x;
        float uy0 = texCoords[0].y;
        float ux1 = texCoords[1].x;
        float uy1 = texCoords[1].y;

        // load vertex properties
        int index = size * VERTEX_SIZE;
        vertices[index]     = x1;
        vertices[index + 1] = y0;
        vertices[index + 2] = color.x;
        vertices[index + 3] = color.y;
        vertices[index + 4] = color.z;
        vertices[index + 5] = ux1;
        vertices[index + 6] = uy0;

        index += VERTEX_SIZE;
        vertices[index]     = x1;
        vertices[index + 1] = y1;
        vertices[index + 2] = color.x;
        vertices[index + 3] = color.y;
        vertices[index + 4] = color.z;
        vertices[index + 5] = ux1;
        vertices[index + 6] = uy1;

        index += VERTEX_SIZE;
        vertices[index]     = x0;
        vertices[index + 1] = y1;
        vertices[index + 2] = color.x;
        vertices[index + 3] = color.y;
        vertices[index + 4] = color.z;
        vertices[index + 5] = ux0;
        vertices[index + 6] = uy1;

        index += VERTEX_SIZE;
        vertices[index]     = x0;
        vertices[index + 1] = y0;
        vertices[index + 2] = color.x;
        vertices[index + 3] = color.y;
        vertices[index + 4] = color.z;
        vertices[index + 5] = ux0;
        vertices[index + 6] = uy0;

        size += 4;
    }

    private int[] genIndices() {
        // 3 indices per triangle
        int[] elements = new int[BATCH_SIZE * 3];
        for (int i = 0; i < elements.length; i++) {
            elements[i] = INDICES[(i % 6)] + ((i / 6) * 4);
        }
        return elements;
    }
}
