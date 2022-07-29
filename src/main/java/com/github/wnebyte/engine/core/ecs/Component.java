package com.github.wnebyte.engine.core.ecs;

import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Component {

    private static int ID_COUNTER = 0;

    private int id = -1;

    public transient GameObject gameObject = null;

    public void start() { }

    public void update(float dt) {}

    public void imGui() {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field: fields) {
                boolean isTransient = Modifier.isTransient(field.getModifiers());
                if (isTransient) {
                    continue;
                }
                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                if (isPrivate) {
                    field.setAccessible(true);
                }

                Class<?> type = field.getType();
                Object value = field.get(this);
                String name = field.getName();

                if (type == int.class) {
                    int val = (int)value;
                    int[] imInt = { val };
                    if (ImGui.dragInt(name + ": ", imInt)) {
                        field.set(this, imInt[0]);
                    }
                } else if (type == float.class) {
                    float val = (float)value;
                    float[] imFloat = { val };
                    if (ImGui.dragFloat(name + ": ", imFloat)) {
                        field.set(this, imFloat[0]);
                    }
                } else if (type == boolean.class) {
                    boolean val = (boolean)value;
                    if (ImGui.checkbox(name + ": ", val)) {
                        field.set(this, !val);
                    }
                } else if (type == Vector2f.class) {
                    Vector2f val = (Vector2f)value;
                    float[] imVec = { val.x, val.y };
                    if (ImGui.dragFloat2(name + ": ", imVec)) {
                        val.set(imVec[0], imVec[1]);
                    }
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
                }

                if (isPrivate) {
                    field.setAccessible(false);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

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
        return "Component[]";
    }
}
