package com.github.wnebyte.engine.core.ecs;

public abstract class Component {

    public transient GameObject gameObject = null;

    public void start() { }

    public void update(float dt) {}

    public void imGui() { }

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
        return String.format(
                "Component[gameObject: %s]", gameObject
        );
    }
}
