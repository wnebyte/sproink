package com.github.wnebyte.editor.util;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Constructor;
import com.github.wnebyte.sproink.core.Prefab;
import static com.github.wnebyte.util.Reflections.newInstance;
import static com.github.wnebyte.util.Reflections.getDefaultConstructor;

public class PrefabFlyWeight {

    private static final Map<Class<? extends Prefab>, Prefab> prefabs = new HashMap<>();

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
}
