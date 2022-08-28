package com.github.wnebyte.sproink.core;

import java.util.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import imgui.ImGui;
import imgui.type.ImInt;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jbox2d.dynamics.contacts.Contact;
import com.github.wnebyte.sproink.ui.JImGui;
import com.github.wnebyte.util.Arrays;
import static com.github.wnebyte.util.Reflections.getEnumValues;

public abstract class Component {

    public static Collection<Field> getAllFields(Class<?> cls) {
        List<Field> c = new ArrayList<>();

        while (cls != null && cls != Component.class) {
            for (Field field : cls.getDeclaredFields()) {
                int mod = field.getModifiers();
                if (!Modifier.isTransient(mod) && !Modifier.isStatic(mod) && !Modifier.isFinal(mod)) {
                    c.add(field);
                }
            }
            cls = cls.getSuperclass();
        }

        return c;
    }

    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }

    private static int ID_COUNTER = 0;

    private int id = -1;

    public transient GameObject gameObject;

    public void start() {}

    public void update(float dt) {}

    public void editorUpdate(float dt) {}

    public void destroy() {}

    public void refresh() {}

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
                    ImInt index = new ImInt(Arrays.indexOf(enumValues, enumType));
                    if (ImGui.combo(field.getName(), index, enumValues, enumValues.length)) {
                        field.set(this, type.getEnumConstants()[index.get()]);
                    }
                } else if (type == String.class) {
                    field.set(this, JImGui.inputText(field.getName(), (String)value));
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

    public void generateId() {
        if (id == -1) {
            id = ID_COUNTER++;
        }
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Component)) return false;
        Component c = (Component) o;
        return Objects.equals(c.id, this.id);
    }

    @Override
    public int hashCode() {
        int result = 33;
        return result +
                17 +
                Objects.hashCode(this.id);
    }

    @Override
    public String toString() {
        return String.format("Component[type: %s, id: %d]", this.getClass(), id);
    }
}
