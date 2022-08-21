package com.github.wnebyte.sproink.util;

import java.io.File;
import java.util.Map;
import java.util.HashMap;
import com.github.wnebyte.sproink.components.Spritesheet;
import com.github.wnebyte.sproink.renderer.Texture;

public class AssetFlyWeight {

    private static final Map<String, Texture> textures = new HashMap<>();

    private static final Map<String, Spritesheet> spritesheets = new HashMap<>();

    /**
     * Lazily initializes (if necessary) and returns the <code>Texture</code> located at
     * the specified <code>path</code>.
     */
    public static Texture getTexture(String path) {
        File file = new File(path);

        if (textures.containsKey(file.getAbsolutePath())) {
            Texture texture = textures.get(file.getAbsolutePath());
            return texture;
        } else {
            Texture texture = new Texture();
            texture.init(file.getAbsolutePath());
            textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }
}
