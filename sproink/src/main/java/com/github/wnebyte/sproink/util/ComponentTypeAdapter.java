package com.github.wnebyte.sproink.util;

import java.lang.reflect.Type;

import com.github.wnebyte.sproink.core.Component;
import com.google.gson.*;

public class ComponentTypeAdapter implements JsonSerializer<Component>, JsonDeserializer<Component> {

    @Override
    public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject out = new JsonObject();
        out.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
        out.add("properties", context.serialize(src, src.getClass()));
        return out;
    }

    @Override
    public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        try {
            return context.deserialize(element, Class.forName(type));
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(
                    String.format(
                            "Unknown element type '%s'.", type
                    ), e
            );
        }
    }
}
