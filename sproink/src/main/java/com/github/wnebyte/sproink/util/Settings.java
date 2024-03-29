package com.github.wnebyte.sproink.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.github.wnebyte.sproink.core.Component;
import com.github.wnebyte.sproink.core.GameObject;

public class Settings {

    public static float GRID_WIDTH = 0.25f;

    public static float GRID_HEIGHT = 0.25f;

    public static Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Component.class, new ComponentTypeAdapter())
            .registerTypeAdapter(GameObject.class, new GameObjectTypeAdapter())
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .create();
}
