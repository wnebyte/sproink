package com.github.wnebyte.engine.util;

import java.util.Set;
import java.util.Arrays;
import java.lang.reflect.Constructor;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import com.github.wnebyte.engine.core.ecs.Component;

public class ReflectionUtil {

    public static Set<Class<? extends Component>> getAllComponentSubTypes(ClassLoader classLoader) {
        return new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("", classLoader))
                .setScanners(new SubTypesScanner()))
                .getSubTypesOf(Component.class);
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
}
