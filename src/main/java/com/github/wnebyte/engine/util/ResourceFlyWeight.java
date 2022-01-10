package com.github.wnebyte.engine.util;

import java.io.File;
import java.util.Map;
import java.util.HashMap;
import com.github.wnebyte.engine.renderer.Shader;
import com.github.wnebyte.engine.renderer.Texture;

public class ResourceFlyWeight {

    private static final Map<String, Shader> shaders = new HashMap<>();

    private static final Map<String, Texture> textures = new HashMap<>();

    public static Shader getShader(String resourceName) {
        File file = new File(getPath(resourceName));

        if (shaders.containsKey(file.getAbsolutePath())) {
            return shaders.get(file.getAbsolutePath());
        } else {
            Shader shader = new Shader(file.getPath());
            shader.compile();
            shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }

    public static Texture getTexture(String resourceName) {
        File file = new File(getPath(resourceName));

        if (textures.containsKey(file.getAbsolutePath())) {
            return textures.get(file.getAbsolutePath());
        } else {
            Texture texture = new Texture(file.getPath());
            textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }

    private static String getPath(String resourceName) {
        return ResourceFlyWeight.class.getResource(resourceName).getPath();
    }
}
