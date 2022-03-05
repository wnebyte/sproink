package com.github.wnebyte.engine.renderer;

import org.lwjgl.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

public class TextureBuilder {

    private int id;

    private String path;


    public TextureBuilder generate() {
        id = glGenTextures();
        return this;
    }

    public TextureBuilder bind() {
        glBindTexture(GL_TEXTURE_2D, id);
        return this;
    }

    public TextureBuilder param(int pname, int param) {
        return this;
    }

    public TextureBuilder image(int width, int height) {

        return this;
    }

    public TextureBuilder setPath(String path) {
        this.path = path;
        return this;
    }

    public Texture build() {
        return null;
    }
}
