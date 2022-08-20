package com.github.wnebyte.sproink.components;

import org.joml.Vector2f;
import org.jbox2d.dynamics.contacts.Contact;
import com.github.wnebyte.sproink.core.ecs.Component;
import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.util.ResourceFlyWeight;
import com.github.wnebyte.sproink.physics2d.components.RigidBody2D;

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
