package com.github.wnebyte.engine.core.ecs;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class GameObject {

    private final String name;

    private List<Component> components;

    public Transform transform;

    public GameObject(String name) {
        this.name = name;
        this.components = new ArrayList<>();
        this.transform = new Transform();
    }

    public GameObject(String name, Transform transform) {
        this.name = name;
        this.components = new ArrayList<>();
        this.transform = transform;
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
        components.add(c);
        c.gameObject = this;
    }

    public void start() {
        for (Component c : components) {
            c.start();
        }
    }

    public void update(float dt) {
        for (Component c : components) {
            c.update(dt);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof GameObject)) return false;
        GameObject go = (GameObject) o;
        return Objects.equals(go.name, this.name) &&
                Objects.equals(go.components, this.components) &&
                Objects.equals(go.transform, this.transform);
    }

    @Override
    public int hashCode() {
        int result = 76;
        return result +
                13 +
                Objects.hashCode(this.name) +
                Objects.hashCode(this.components) +
                Objects.hashCode(this.transform);
    }

    @Override
    public String toString() {
        return String.format(
                "GameObject[name: %s, transform: %s, components: %s]",
                name, transform, Arrays.toString(components.toArray())
        );
    }
}
