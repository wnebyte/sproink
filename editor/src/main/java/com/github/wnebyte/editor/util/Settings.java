package com.github.wnebyte.editor.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.github.wnebyte.sproink.core.GameObject;
import com.github.wnebyte.sproink.core.Component;
import com.github.wnebyte.sproink.util.GameObjectTypeAdapter;

public class Settings {

    public static final float GRID_WIDTH = 0.25f;

    public static final float GRID_HEIGHT = 0.25f;

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Component.class, new ComponentTypeAdapter())
            .registerTypeAdapter(GameObject.class, new GameObjectTypeAdapter())
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .create();
}
