package com.github.wnebyte.util;

import java.util.Arrays;
import java.lang.reflect.Constructor;

public class Reflections {

    public static <T extends Enum<T>> String[] getEnumValues(Class<T> enumType) {
        String[] enumValues = new String[enumType.getEnumConstants().length];
        int i = 0;
        for (T ordinal : enumType.getEnumConstants()) {
            enumValues[i++] = ordinal.name();
        }
        return enumValues;
    }

    public static boolean hasDefaultConstructor(Class<?> cls) {
        if (cls == null) return false;
        return Arrays.stream(cls.getDeclaredConstructors()).anyMatch(cons -> cons.getParameterCount() == 0);
    }

    public static Constructor<?> getDefaultConstructor(Class<?> cls) {
        if (cls == null) return null;
        return Arrays.stream(cls.getDeclaredConstructors()).filter(cons -> cons.getParameterCount() == 0)
                .findFirst().orElse(null);
    }

    public static Object newInstance(Constructor<?> cons, Object... args) {
        if (cons == null) return null;
        Object obj;
        boolean accessible = cons.isAccessible();

        if (!accessible) {
            cons.setAccessible(true);
        }
        try {
            obj = cons.newInstance(args);
        } catch (Exception e) {
            obj = null;
        }
        finally {
            cons.setAccessible(accessible);
        }

        return obj;
    }
}
