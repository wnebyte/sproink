package com.github.wnebyte.util;

import java.util.Set;
import java.util.HashSet;

public class Sets {

    public static <T> Set<T> of() {
        return new HashSet<>();
    }

    @SafeVarargs
    public static <T> Set<T> of(T... e) {
        if (e == null) return null;
        Set<T> set = new HashSet<>(e.length);
        for (int i = 0; i < e.length; i++) {
            T value = e[i];
            set.add(value);
        }
        return set;
    }

    public static Set<Integer> of(float[] floats) {
        if (floats == null) return null;
        Set<Integer> set = new HashSet<>(floats.length);
        for (int i = 0; i < floats.length; i++) {
            int value = (int)floats[i];
            set.add(value);
        }
        return set;
    }
}
