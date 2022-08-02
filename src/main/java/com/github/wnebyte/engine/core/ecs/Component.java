package com.github.wnebyte.engine.core.ecs;

import java.util.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import imgui.ImGui;
import imgui.type.ImInt;
import org.jbox2d.dynamics.contacts.Contact;
import com.github.wnebyte.engine.editor.JImGui;

public abstract class Component {

    private static <T extends Enum<T>> String[] getEnumValues(Class<T> enumType) {
        String[] enumValues = new String[enumType.getEnumConstants().length];
        int i = 0;
        for (T ordinal : enumType.getEnumConstants()) {
            enumValues[i++] = ordinal.name();
        }
        return enumValues;
    }

    private static int indexOf(String s, String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (s.equals(array[i])) {
                return i;
            }
        }
        return -1;
    }

    public static Collection<Field> getAllFields(Class<?> cls) {
        List<Field> c = new ArrayList<>();

        while (cls != null && cls != Component.class) {
            for (Field f : cls.getDeclaredFields()) {
                int mod = f.getModifiers();
                if (!Modifier.isTransient(mod) && !Modifier.isStatic(mod)) {
                    c.add(f);
                }
            }
            cls = cls.getSuperclass();
        }

        return c;
    }

    private static int ID_COUNTER = 0;

    private int id = -1;

    public transient GameObject gameObject = null;

    public void start() { }

    public void editorUpdate(float dt) {}

    public void update(float dt) {}

    public void imGui() {
        try {
            Collection<Field> fields = getAllFields(this.getClass());
            for (Field field: fields) {
                boolean accessible = field.isAccessible();
                if (!accessible) {
                    field.setAccessible(true);
                }

                @SuppressWarnings("rawtypes")
                Class type = field.getType();
                Object value = field.get(this);
                String name = field.getName();

                if (type == int.class) {
                    int val = (int)value;
                    field.set(this, JImGui.dragInt(name, val));
                } else if (type == float.class) {
                    float val = (float)value;
                    field.set(this, JImGui.dragFloat(name, val));
                } else if (type == boolean.class) {
                    boolean val = (boolean)value;
                    if (ImGui.checkbox(name + ": ", val)) {
                        field.set(this, !val);
                    }
                } else if (type == Vector2f.class) {
                    Vector2f val = (Vector2f)value;
                    JImGui.drawVec2Control(name, val);
                } else if (type == Vector3f.class) {
                    Vector3f val = (Vector3f)value;
                    float[] imVec = { val.x, val.y, val.z };
                    if (ImGui.dragFloat3(name + ": ", imVec)) {
                        val.set(imVec[0], imVec[1], imVec[2]);
                    }
                } else if (type == Vector4f.class) {
                    Vector4f val = (Vector4f)value;
                    float[] imVec = { val.x, val.y, val.z, val.w };
                    if (ImGui.dragFloat4(name + ": ", imVec)) {
                        val.set(imVec[0], imVec[1], imVec[2], imVec[3]);
                    }
                } else if (type.isEnum()) {
                    @SuppressWarnings("unchecked")
                    String[] enumValues = getEnumValues(type);
                    String enumType = ((Enum<?>)value).name();
                    ImInt index = new ImInt(indexOf(enumType, enumValues));
                    if (ImGui.combo(field.getName(), index, enumValues, enumValues.length)) {
                        field.set(this, type.getEnumConstants()[index.get()]);
                    }
                }

                if (!accessible) {
                    field.setAccessible(false);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void beginCollision(GameObject go, Contact contact, Vector2f contactNormal) {}

    public void endCollision(GameObject go, Contact contact, Vector2f contactNormal) {}

    public void preSolve(GameObject go, Contact contact, Vector2f contactNormal) {}

    public void postSolve(GameObject go, Contact contact, Vector2f contactNormal) {}

    public void destroy() {}

    public void generateId() {
        if (this.id == -1) {
            this.id = ID_COUNTER++;
        }
    }

    public int getId() {
        return id;
    }

    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Component)) return false;
        Component component = (Component) o;
        return super.equals(component);
    }

    @Override
    public int hashCode() {
        int result = 33;
        return result +
                17 +
                super.hashCode();
    }

    @Override
    public String toString() {
        return "Component";
    }
}
