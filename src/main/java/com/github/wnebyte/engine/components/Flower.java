package com.github.wnebyte.engine.components;

import org.joml.Vector2f;
import org.jbox2d.dynamics.contacts.Contact;
import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.util.ResourceFlyWeight;
import com.github.wnebyte.engine.physics2d.components.RigidBody2D;

public class Flower extends Component {

    private transient RigidBody2D rb;

    @Override
    public void start() {
        this.rb = gameObject.getComponent(RigidBody2D.class);
        ResourceFlyWeight.getSound("/sounds/powerup_appears.ogg").play();
        rb.setIsSensor();
    }

    @Override
    public void beginCollision(GameObject go, Contact contact, Vector2f contactNormal) {
        PlayerController pc = go.getComponent(PlayerController.class);
        if (pc != null) {
            pc.powerup();
            gameObject.destroy();
        }
    }
}
