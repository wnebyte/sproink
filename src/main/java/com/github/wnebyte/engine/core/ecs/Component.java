package com.github.wnebyte.engine.core.ecs;

import java.util.Objects;

public abstract class Component {

    public GameObject gameObject = null;

    public void start() { }

    public abstract void update(float dt);

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof Component)) return false;
        Component component = (Component) o;
        return Objects.equals(component.gameObject, this.gameObject);
    }

    @Override
    public int hashCode() {
        int result = 33;
        return result +
                17 +
                Objects.hashCode(gameObject);
    }

    @Override
    public String toString() {
        return String.format(
                "Component[gameObject: %s]", gameObject
        );
    }
}
