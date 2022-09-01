package com.github.wnebyte.sproink.core;

import java.util.*;
import imgui.ImGui;
import imgui.type.ImBoolean;
import com.google.gson.Gson;
import com.github.wnebyte.sproink.util.Settings;

public class GameObject {

    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }

    private static int ID_COUNTER = 0;

    public transient Transform transform;

    private int id = -1;

    private String name;

    private final List<Component> components;

    private transient final List<ImBoolean> visibilities;

    private boolean serialize = true;

    private boolean dead = false;

    public GameObject(String name) {
        this.name = name;
        this.components = new ArrayList<>();
        this.visibilities = new ArrayList<>();
        this.id = ID_COUNTER++;
    }

    public void start() {
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            c.start();
        }
    }

    public void update(float dt) {
        for (Component c : components) {
            c.update(dt);
        }
    }

    public void editorUpdate(float dt) {
        for (Component c : components) {
            c.editorUpdate(dt);
        }
    }

    public void destroy() {
        dead = true;
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            c.destroy();
        }
    }

    public void imGui() {
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            ImBoolean visible = visibilities.get(i);
            if (ImGui.collapsingHeader(c.getClass().getSimpleName(), visible)) {
                c.imGui();
            }
            if (c.getClass() == Transform.class) {
                visible.set(true);
            } else if (!visible.get()) {
                removeComponent(c.getClass());
                i--;
            }
        }
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false : String.format("Error: (GameObject) Casting Component: '%s'", c.getClass());
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
                visibilities.remove(i);
                return;
            }
        }
    }

    public void addComponent(Component c) {
        c.generateId();
        components.add(c);
        visibilities.add(new ImBoolean(true));
        c.gameObject = this;
    }

    public List<Component> getAllComponents() {
        return components;
    }

    public GameObject copy() {
        Gson gson = Settings.GSON;
        String json = gson.toJson(this);
        GameObject go = gson.fromJson(json, GameObject.class);
        return go;
    }

    public void generateId() {
        if (id == -1) {
            id = ID_COUNTER++;
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNoSerialize() {
        serialize = false;
    }

    public boolean isSerialize() {
        return serialize;
    }

    public boolean isDead() {
        return dead;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof GameObject)) return false;
        GameObject go = (GameObject) o;
        return Objects.equals(go.id, this.id) &&
                Objects.equals(go.name, this.name) &&
                Arrays.equals(go.components.toArray(), this.components.toArray());
    }

    @Override
    public int hashCode() {
        int result = 76;
        return result +
                13 +
                Objects.hashCode(this.id) +
                Objects.hashCode(this.name) +
                Arrays.hashCode(this.components.toArray());
    }

    @Override
    public String toString() {
        return String.format(
                "GameObject[name: %s, transform: %s, components: %s]",
                name, transform, Arrays.toString(components.toArray())
        );
    }
}
