package com.github.wnebyte.sproink.util;

import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import com.github.wnebyte.sproink.core.audio.Sound;
import com.github.wnebyte.sproink.renderer.Shader;
import com.github.wnebyte.sproink.renderer.Texture;
import com.github.wnebyte.sproink.components.Spritesheet;

public final class AssetFlyWeight {

    private static final Map<String, Shader> shaders = new HashMap<>();

    private static final Map<String, Texture> textures = new HashMap<>();

    private static final Map<String, Spritesheet> spritesheets = new HashMap<>();

    private static final Map<String, Sound> sounds = new HashMap<>();

    public static Shader getShader(String path) {
        File file = new File(path);
        assert file.exists() : String.format("Error: (AssetFlyWeight) Shader: '%s' does not exist",
                file.getAbsolutePath());

        if (shaders.containsKey(file.getAbsolutePath())) {
            return shaders.get(file.getAbsolutePath());
        } else {
            Shader shader = new Shader(file.getPath());
            shader.compile();
            shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }
    
    /**
     * Lazily initializes (if necessary) and returns the <code>Texture</code> located at
     * the specified <code>path</code>.
     */
    public static Texture getTexture(String path) {
        File file = new File(path);
        assert file.exists() : String.format("Error: (AssetFlyWeight) Texture: '%s' does not exist",
                file.getAbsolutePath());

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

    public static Spritesheet getSpritesheet(String path) {
        File file = new File(path);
        assert file.exists() : String.format("Error: (AssetFlyWeight) Spritesheet: '%s' does not exist",
                file.getAbsolutePath());

        if (spritesheets.containsKey(file.getAbsolutePath())) {
            return spritesheets.get(file.getAbsolutePath());
        } else {
            assert false: String.format("Error: (AssetFlyWeight) " +
                    "Spritesheet: '%s' has not yet been added to pool.",
                    file.getAbsolutePath());
            return null;
        }
    }

    public static void addSpritesheet(String path, Spritesheet spritesheet) {
        File file = new File(path);
        assert file.exists() : String.format("Error: (AssetFlyWeight) Spritesheet: '%s' does not exist",
                file.getAbsolutePath());

        if (!spritesheets.containsKey(file.getAbsolutePath())) {
            spritesheets.put(file.getAbsolutePath(), spritesheet);
        }
    }

    public static boolean hasSpritesheet(String path) {
        File file = new File(path);
        return spritesheets.containsKey(file.getAbsolutePath());
    }

    public static Sound getSound(String path) {
        File file = new File(path);
        assert file.exists() : String.format("Error: (AssetFlyWeight) Sound: '%s' does not exist",
                file.getAbsolutePath());

        if (sounds.containsKey(file.getAbsolutePath())) {
            return sounds.get(file.getAbsolutePath());
        } else {
            assert false : String.format("Error (AssetFlyWeight): Sound has not yet been added to pool: '%s'",
                    file.getAbsolutePath());
            return null;
        }
    }

    public static Sound addSound(String path, boolean loops) {
        File file = new File(path);
        assert file.exists() : String.format("Error: (AssetFlyWeight) Sound: '%s' does not exist",
                file.getAbsolutePath());

        if (sounds.containsKey(file.getAbsolutePath())) {
            return sounds.get(file.getAbsolutePath());
        } else {
            Sound sound = new Sound(file.getAbsolutePath(), loops);
            sounds.put(file.getAbsolutePath(), sound);
            return sound;
        }
    }

    public static Collection<Sound> getAllSounds() {
        return sounds.values();
    }
}
