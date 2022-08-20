package com.github.wnebyte.sproink.core.ecs;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import org.junit.Test;
import com.github.wnebyte.sproink.physics2d.components.CircleCollider;

public class ComponentTest {

    @Test
    public void test00() {
        Collection<Field> c = Component.getAllFields(CircleCollider.class);
        for (Field field : c) {
            System.out.printf("%s %s %s%n",
                    getAccessModifier(field.getModifiers()), field.getType().getSimpleName(), field.getName());
        }
    }

    private static String getAccessModifier(int mod) {
        if (Modifier.isPublic(mod)) {
            return "public";
        } else if (Modifier.isProtected(mod)) {
            return "protected";
        } else if (Modifier.isPrivate(mod)) {
            return "private";
        } else {
            return "";
        }
    }
}
