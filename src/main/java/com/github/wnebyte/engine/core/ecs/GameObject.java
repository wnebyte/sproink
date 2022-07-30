package com.github.wnebyte.engine.core.ecs;

import com.github.wnebyte.engine.core.Transform;
import imgui.ImGui;

import java.util.*;

public class GameObject {

    private static int ID_COUNTER = 0;

    public transient Transform transform;

    private int id = -1;

    private final String name;

    private final List<Component> components;

    private boolean serialize = true;

    private boolean isDead = false;

    public GameObject(String name) {
        this.name = name;
        this.components = new ArrayList<>();
        this.id = ID_COUNTER++;
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false : "Error: (GameObject) Casting Component.";
                }
            }
        }

        return null;
    }

    public List<Component> getAllComponents() {
        return components;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            if (componentClass.isAssignableFrom(c.getClass())) {
                components.remove(i);
                return;
            }
        }
    }

    public void addComponent(Component c) {
        c.generateId();
        components.add(c);
        c.gameObject = this;
    }

    public void start() {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).start();
        }
    }

    public void editorUpdate(float dt) {
        for (Component c : components) {
            c.editorUpdate(dt);
        }
    }

    public void update(float dt) {
        for (Component c : components) {
            c.update(dt);
        }
    }

    public void imGui() {
        for (Component c : components) {
            if (ImGui.collapsingHeader(c.getClass().getSimpleName())) {
                c.imGui();
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setNoSerialize() {
        serialize = false;
    }

    public boolean isSerialize() {
        return serialize;
    }

    public boolean isDead() {
        return isDead;
    }

    public void destroy() {
        isDead = true;
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            c.destroy();
        }
    }

    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof GameObject)) return false;
        GameObject go = (GameObject) o;
        return Objects.equals(go.name, this.name) &&
                Arrays.equals(go.components.toArray(), this.components.toArray()) &&
                Objects.equals(go.transform, this.transform) &&
                super.equals(go);
    }

    @Override
    public int hashCode() {
        int result = 76;
        return result +
                13 +
                Objects.hashCode(this.name) +
                Objects.hashCode(this.components) +
                Objects.hashCode(this.transform) +
                super.hashCode();
    }

    @Override
    public String toString() {
        return String.format(
                "GameObject[name: %s, transform: %s, components: %s]",
                name, transform, Arrays.toString(components.toArray())
        );
    }
}
