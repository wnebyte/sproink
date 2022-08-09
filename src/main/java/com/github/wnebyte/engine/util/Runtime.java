package com.github.wnebyte.engine.util;

import java.util.Set;
import com.github.wnebyte.engine.core.ecs.Component;

public class Runtime {

    private static final Set<Class<? extends Component>> componentClasses;

    static {
        componentClasses = ReflectionUtil.getAllComponentSubTypes(Runtime.class.getClassLoader());
        componentClasses.removeIf(cls -> !ReflectionUtil.hasDefaultConstructor(cls));
    }

    public static Set<Class<? extends Component>> getComponentClasses() {
        return componentClasses;
    }
}
