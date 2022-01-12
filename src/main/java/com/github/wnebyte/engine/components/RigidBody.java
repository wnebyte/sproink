package com.github.wnebyte.engine.components;

import org.joml.Vector3f;
import org.joml.Vector4f;
import com.github.wnebyte.engine.core.ecs.Component;

public class RigidBody extends Component {

    private int colliderType = 0;

    private float friction = 0.8f;

    public Vector3f velocity = new Vector3f(0, 0.5f, 0);

    public transient Vector4f tmp = new Vector4f(0, 0, 0, 0);

    @Override
    public String toString() {
        return String.format(
                "RigidBody[colliderType: %d, friction: %f, velocity: %s, tmp: %s]",
                colliderType, friction, velocity, tmp
        );
    }
}
