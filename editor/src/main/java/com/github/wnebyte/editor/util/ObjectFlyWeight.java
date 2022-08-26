package com.github.wnebyte.editor.util;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Constructor;
import com.github.wnebyte.sproink.core.Prefab;
import com.github.wnebyte.sproink.core.scene.SceneInitializer;
import static com.github.wnebyte.util.Reflections.newInstance;
import static com.github.wnebyte.util.Reflections.getDefaultConstructor;

public class ObjectFlyWeight {

    private static final Map<Class<? extends Prefab>, Prefab> prefabs = new HashMap<>();

    private static final Map<Class<? extends SceneInitializer>, SceneInitializer> sceneInitializers = new HashMap<>();

    public static <T extends Prefab> Prefab getPrefab(Class<T> cls) {
        if (prefabs.containsKey(cls)) {
            return prefabs.get(cls);
        } else {
            Constructor<?> cons = getDefaultConstructor(cls);
            if (cons != null) {
                Object obj = newInstance(cons);
                if (obj != null) {
                    T prefab = cls.cast(obj);
                    prefabs.put(cls, prefab);
                    return prefab;
                }
            }
            return null;
        }
    }

    public static <T extends SceneInitializer> SceneInitializer getSceneInitializer(Class<T> cls) {
        if (sceneInitializers.containsKey(cls)) {
            return sceneInitializers.get(cls);
        } else {
            Constructor<?> cons = getDefaultConstructor(cls);
            assert (cons != null) :
                    String.format("Error: (ObjectFlyWeight) Constructor for cls: '%s' is null", cls.getCanonicalName());
            Object obj = newInstance(cons);
            assert (obj != null) :
                    String.format("Error: (ObjectFlyWeight) Object for cls: '%s' is null", cls.getCanonicalName());
            T sceneInitializer = cls.cast(obj);
            sceneInitializers.put(cls, sceneInitializer);
            return sceneInitializer;
        }
    }
}
