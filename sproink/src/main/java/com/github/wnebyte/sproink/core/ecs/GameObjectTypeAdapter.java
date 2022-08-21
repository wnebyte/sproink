package com.github.wnebyte.sproink.core.ecs;

import java.lang.reflect.Type;

import com.github.wnebyte.sproink.core.Transform;
import com.google.gson.*;

public class GameObjectTypeAdapter implements JsonDeserializer<GameObject> {

    @Override
    public GameObject deserialize(
            JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        JsonArray components = jsonObject.getAsJsonArray("components");

        GameObject go = new GameObject(name);
        for (JsonElement e : components) {
            Component c = context.deserialize(e, Component.class);
            go.addComponent(c);
        }
        go.transform = go.getComponent(Transform.class);
        return go;
    }
}