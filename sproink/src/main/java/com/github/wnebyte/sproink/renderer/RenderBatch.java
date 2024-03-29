package com.github.wnebyte.sproink.renderer;

import java.util.List;
import java.util.ArrayList;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.joml.Matrix4f;
import com.github.wnebyte.sproink.core.Window;
import com.github.wnebyte.sproink.core.GameObject;
import com.github.wnebyte.sproink.components.SpriteRenderer;
import com.github.wnebyte.sproink.components.Anchor;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch implements Comparable<RenderBatch> {

    //                                     Vertex Array Object (VAO)
    // ================================================================================================
    // Pos                Color                            Tex Coords         Tex Id      Entity Id  //
    // float, float       float, float, float, float       float, float       float       float      //
    // ================================================================================================

    private static final int POS_SIZE = 2;

    private static final int COLOR_SIZE = 4;

    private static final int TEX_COORDS_SIZE = 2;

    private static final int TEX_ID_SIZE = 1;

    private static final int ENTITY_ID_SIZE = 1;

    private static final int STATIC_FLAG_SIZE = 1;

    private static final int POS_OFFSET = 0;

    private static final int COLOR_OFFSET = POS_OFFSET + (POS_SIZE * Float.BYTES);

    private static final int TEX_COORDS_OFFSET = COLOR_OFFSET + (COLOR_SIZE * Float.BYTES);

    private static final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + (TEX_COORDS_SIZE * Float.BYTES);

    private static final int ENTITY_ID_OFFSET = TEX_ID_OFFSET + (TEX_ID_SIZE * Float.BYTES);

    private static final int STATIC_FLAG_OFFSET = ENTITY_ID_OFFSET + (ENTITY_ID_SIZE * Float.BYTES);

    private static final int VERTEX_SIZE = POS_SIZE + COLOR_SIZE + TEX_COORDS_SIZE + TEX_ID_SIZE + ENTITY_ID_SIZE + STATIC_FLAG_SIZE;

    private static final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private static final int[] TEX_SLOTS = { 0, 1, 2, 3, 4, 5, 6, 7 };

    private SpriteRenderer[] sprites;

    private int numSprites;

    private boolean hasRoom;

    private float[] vertices;

    private int vaoID;

    private int vboID;

    private int maxBatchSize;

    private List<Texture> textures;

    private Renderer renderer;

    private int zIndex;

    public RenderBatch(Renderer renderer, int maxBatchSize, int zIndex) {
        this.renderer = renderer;
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;
        // 4 vertices per quad
        this.vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];
        this.numSprites = 0;
        this.hasRoom = true;
        this.textures = new ArrayList<>();
        this.zIndex = zIndex;
    }

    public void start() {
        // Generate and bind a Vertex Array Object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

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

        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);

        glVertexAttribPointer(4, ENTITY_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, ENTITY_ID_OFFSET);
        glEnableVertexAttribArray(4);

        glVertexAttribPointer(5, STATIC_FLAG_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, STATIC_FLAG_OFFSET);
        glEnableVertexAttribArray(5);
    }

    public void addSprite(SpriteRenderer spr) {
        // Get index and add the renderObject
        int index = numSprites;
        sprites[index] = spr;
        numSprites++;

        if (spr.getTexture() != null) {
            if (!textures.contains(spr.getTexture())) {
                textures.add(spr.getTexture());
            }
        }

        // Add properties to local vertices array
        loadVertexProperties(index);

        if (numSprites >= maxBatchSize) {
            hasRoom = false;
        }
    }

    public void render() {
        boolean rebufferData = false;
        for (int i = 0; i < numSprites; i++) {

            SpriteRenderer spr = sprites[i];
            if (spr.isDirty()) {
                if (!hasTexture(spr.getTexture())) {
                    renderer.destroyGameObject(spr.gameObject);
                    renderer.add(spr.gameObject);
                } else {
                    loadVertexProperties(i);
                    spr.setClean();
                    rebufferData = true;
                }
            }

            // Todo: better solution for this
            if (spr.gameObject.transform.zIndex != zIndex) {
                destroyIfExists(spr.gameObject);
                renderer.add(spr.gameObject);
                i--;
            }
        }
        if (rebufferData) {
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }

        // Use shader
        Shader shader = renderer.getShader();
        shader.use();
        shader.uploadMatrix4f(Shader.U_PROJECTION, Window.getScene().getCamera().getProjectionMatrix());
        shader.uploadMatrix4f(Shader.U_VIEW, Window.getScene().getCamera().getViewMatrix());
        for (int i = 0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        shader.uploadIntArray(Shader.U_TEXTURES, TEX_SLOTS);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, numSprites * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        for (Texture texture : textures) {
            texture.unbind();
        }
        shader.detach();
    }

    public boolean destroyIfExists(GameObject go) {
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        for (int i = 0; i < numSprites; i++) {
            if (sprites[i].equals(spr)) {
                for (int j = i; j < numSprites - 1; j++) {
                    sprites[j] = sprites[j + 1];
                    sprites[j].setDirty();
                }
                numSprites--;
                return true;
            }
        }
        return false;
    }

    private void loadVertexProperties(int index) {
        SpriteRenderer spr = sprites[index];

        // Find offset within array (4 vertices per sprite)
        int offset = index * 4 * VERTEX_SIZE;
        Vector4f color = spr.getColor();
        Vector2f[] texCoords = spr.getTexCoords();
        int texId = (spr.getTexture() == null) ? 0 : textures.indexOf(spr.getTexture()) + 1;

        boolean isRotated = (spr.gameObject.transform.rotation != 0.0f);
        Matrix4f transformMatrix = null;
        if (isRotated) {
            transformMatrix = spr.gameObject.transform.getTransformMatrix();
        }

        // Add vertex with the appropriate properties
        float xAdd = 0.5f;
        float yAdd = 0.5f;
        for (int i = 0; i < 4; i++) {
            if (i == 1) {
                yAdd = -0.5f;
            } else if (i == 2) {
                xAdd = -0.5f;
            } else if (i == 3) {
                yAdd = 0.5f;
            }

            // i0: xAdd =  0.5, yAdd =  0.5
            // i1: xAdd =  0.5, yAdd = -0.5
            // i2: xAdd = -0.5, yAdd = -0.5
            // i3: xAdd = -0.5, yAdd =  0.5
            Vector4f pos = new Vector4f(
                    spr.gameObject.transform.position.x + (xAdd * spr.gameObject.transform.scale.x),
                    spr.gameObject.transform.position.y + (yAdd * spr.gameObject.transform.scale.y),
                    0, 1);
            if (isRotated) {
                pos = new Vector4f(xAdd, yAdd, 0, 1).mul(transformMatrix);
            }
            // Load position
            vertices[offset]     = pos.x;
            vertices[offset + 1] = pos.y;

            // Load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            // Load texture coordinates
            vertices[offset + 6] = texCoords[i].x;
            vertices[offset + 7] = texCoords[i].y;

            // Load texture id
            vertices[offset + 8] = texId;

            // Load entity id
            vertices[offset + 9] = spr.gameObject.getId() + 1;

            // Load static flag
            vertices[offset + 10] = (spr.gameObject.getComponent(Anchor.class) != null) ? 1.0f : 0.0f;

            offset += VERTEX_SIZE;
        }
    }

    private int[] genIndices() {
        // 6 indices per quad (3 per triangle)
        int[] elements = new int[6 * maxBatchSize];
        for (int i = 0; i < maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }
        return elements;
    }

    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        // 3, 2, 0, 0, 2, 1     7, 6, 4, 4, 6, 5
        // Triangle 1
        elements[offsetArrayIndex]     = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;

        // Triangle 2
        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    public boolean hasRoom() {
        return hasRoom;
    }

    public boolean hasTextureRoom() {
        return (textures.size() < 8);
    }

    public boolean hasTexture(Texture texture) {
        return textures.contains(texture);
    }

    public int zIndex() {
        return zIndex;
    }

    @Override
    public int compareTo(RenderBatch o) {
        return Integer.compare(this.zIndex, o.zIndex);
    }
}
