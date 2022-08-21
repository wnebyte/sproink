package com.github.wnebyte.sproink.components;

import java.util.Arrays;
import org.joml.Vector2f;
import com.github.wnebyte.sproink.renderer.Texture;

public class Sprite {

    private Texture texture = null;

    private float width, height;

    private Vector2f[] texCoords = new Vector2f[] {
            new Vector2f(1, 1),
            new Vector2f(1, 0),
            new Vector2f(0, 0),
            new Vector2f(0, 1)
    };

    public Sprite() {}

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Vector2f[] getTexCoords() {
        return texCoords;
    }

    public void setTexCoords(Vector2f[] texCoords) {
        this.texCoords = texCoords;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getTexId() {
        return (texture == null) ? -1 : texture.getId();
    }

    @Override
    public String toString() {
        return String.format(
                "Sprite[id: %d, texture :%s, texCords: %s, width: %f, height: %f]",
                getTexId(), texture, Arrays.toString(texCoords), width, height
        );
    }
}
