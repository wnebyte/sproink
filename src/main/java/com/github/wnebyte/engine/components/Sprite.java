package com.github.wnebyte.engine.components;

import org.joml.Vector2f;
import com.github.wnebyte.engine.renderer.Texture;

import java.util.Arrays;

public class Sprite {

    private Texture texture = null;

    private Vector2f[] texCoords = new Vector2f[] {
            new Vector2f(1, 1),
            new Vector2f(1, 0),
            new Vector2f(0, 0),
            new Vector2f(0, 1)
    };

    /*
    public Sprite(Texture texture) {
        this.texture = texture;
        this.texCoords = new Vector2f[] {
                new Vector2f(1, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 0),
                new Vector2f(0, 1)
        };
    }

    public Sprite(Texture texture, Vector2f[] texCoords) {
        this.texture = texture;
        this.texCoords = texCoords;
    }
     */

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

    @Override
    public String toString() {
        return String.format(
                "Sprite[texture :%s, texCords: %s]", texture, Arrays.toString(texCoords)
        );
    }
}
