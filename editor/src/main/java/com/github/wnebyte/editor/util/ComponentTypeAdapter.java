package com.github.wnebyte.editor.util;

import java.lang.reflect.Type;
import com.google.gson.*;
import com.github.wnebyte.editor.project.Context;
import com.github.wnebyte.sproink.core.ecs.Component;

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
        Context c = Context.get();
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        try {
            return context.deserialize(element, Class.forName(type, true, (c != null) ? c.getURLClassLoader() : null));
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(
                    String.format(
                            "Unknown element type '%s'.", type
                    ), e
            );
        }
    }
}
