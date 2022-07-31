package com.github.wnebyte.engine.util;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

import com.github.wnebyte.engine.core.audio.Sound;
import com.github.wnebyte.engine.renderer.Shader;
import com.github.wnebyte.engine.renderer.Texture;
import com.github.wnebyte.engine.components.Spritesheet;

public class ResourceFlyWeight {

    private static final Map<String, Shader> shaders = new HashMap<>();

    private static final Map<String, Texture> textures = new HashMap<>();

    private static final Map<String, Spritesheet> spritesheets = new HashMap<>();

    private static final Map<String, Sound> sounds = new HashMap<>();

    public static Shader getShader(String resourceName) {
        File file = ResourceUtil.getFile(resourceName);

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
        File file = ResourceUtil.getFile(resourceName);

        if (textures.containsKey(file.getAbsolutePath())) {
            return textures.get(file.getAbsolutePath());
        } else {
            Texture texture = new Texture();
            texture.init(file.getPath());
            textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }

    public static Spritesheet getSpritesheet(String resourceName) {
        File file = ResourceUtil.getFile(resourceName);

        if (spritesheets.containsKey(file.getAbsolutePath())) {
            return spritesheets.get(file.getAbsolutePath());
        } else {
            assert false : "Error: (ResourceFlyWeight) Tried to access spritesheet '" + resourceName + "' " +
                    "but it has not been added to the pool." + "\n" + file.getAbsolutePath() + "\n" +
                    Arrays.toString(spritesheets.keySet().toArray());
            return null;
        }
    }

    public static Sound getSound(String resourceName) {
        File file = ResourceUtil.getFile(resourceName);

        if (sounds.containsKey(file.getAbsolutePath())) {
            return sounds.get(file.getAbsolutePath());
        } else {
            assert false : "Error: (ResourceFlyWeight) Sound file not added: '" + resourceName + "'";
        }

        return null;
    }

    public static Collection<Sound> getAllSounds() {
        return sounds.values();
    }

    public static Sound addSound(String resourceName, boolean loops) {
        File file = ResourceUtil.getFile(resourceName);

        if (sounds.containsKey(file.getAbsolutePath())) {
            return sounds.get(file.getAbsolutePath());
        } else {
            Sound sound = new Sound(file.getAbsolutePath(), loops);
            sounds.put(file.getAbsolutePath(), sound);
            return sound;
        }
    }

    public static void addSpritesheet(String resourceName, Spritesheet spritesheet) {
        File file = ResourceUtil.getFile(resourceName);

        if (!spritesheets.containsKey(file.getAbsolutePath())) {
            spritesheets.put(file.getAbsolutePath(), spritesheet);
        }
    }
}
