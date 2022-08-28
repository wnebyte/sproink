package com.github.wnebyte.editor.util;

import org.joml.Vector4f;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.github.wnebyte.sproink.core.ecs.Component;
import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.util.io.GameObjectTypeAdapter;

public class Settings {

    public static final float GRID_WIDTH = 0.25f;

    public static final float GRID_HEIGHT = 0.25f;

    public static final Vector4f SPR_DRAG_COLOR =
            new Vector4f(0.8f, 0.8f, 0.8f, 0.8f);

    public static final Vector4f SPR_DROP_COLOR =
            new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Component.class, new ComponentTypeAdapter())
            .registerTypeAdapter(GameObject.class, new GameObjectTypeAdapter())
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .create();
}
