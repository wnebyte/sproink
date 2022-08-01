package com.github.wnebyte.engine.core.ecs;

import java.lang.reflect.Type;
import com.google.gson.*;
import com.github.wnebyte.engine.core.Transform;

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
