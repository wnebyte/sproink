package com.github.wnebyte.engine.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.core.ecs.ComponentTypeAdapter;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.core.ecs.GameObjectTypeAdapter;

public class Settings {

    public static float GRID_WIDTH = 0.25f;

    public static float GRID_HEIGHT = 0.25f;

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Component.class, new ComponentTypeAdapter())
            .registerTypeAdapter(GameObject.class, new GameObjectTypeAdapter())
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .create();
}
