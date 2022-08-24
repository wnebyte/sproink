package com.github.wnebyte.sproink.core.ecs;

import java.util.List;
import java.lang.reflect.Type;
import java.util.stream.Collectors;
import com.google.gson.*;
import com.github.wnebyte.sproink.core.scene.Scene;

public class SceneTypeAdapter implements JsonSerializer<Scene>, JsonDeserializer<Scene> {

    @Override
    public JsonElement serialize(
            Scene src, Type typeOfSrc, JsonSerializationContext context) {
        List<GameObject> gameObjects = src.getGameObjects().stream()
                .filter(GameObject::isSerialize)
                .collect(Collectors.toList());
        JsonElement out = context.serialize(gameObjects);
        return out;
    }

    @Override
    public Scene deserialize(
            JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return null;
    }
}
