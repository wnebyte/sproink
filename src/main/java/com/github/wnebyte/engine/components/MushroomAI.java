package com.github.wnebyte.engine.components;

import org.joml.Vector2f;
import org.jbox2d.dynamics.contacts.Contact;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.util.ResourceFlyWeight;
import com.github.wnebyte.engine.physics2d.components.RigidBody2D;

public class MushroomAI extends Component {

    private transient boolean goingRight = true;

    private transient RigidBody2D rb;

    private transient Vector2f speed = new Vector2f(1.0f, 0.0f);

    private transient float maxSpeed = 0.8f;

    private transient boolean hitPlayer = false;

    @Override
    public void start() {
        this.rb = gameObject.getComponent(RigidBody2D.class);
        ResourceFlyWeight.getSound("/sounds/powerup_appears.ogg").play();
    }

    @Override
    public void update(float dt) {
        if (goingRight && Math.abs(rb.getVelocity().x) < maxSpeed) {
            rb.addVelocity(speed);
        } else if (!goingRight && Math.abs(rb.getVelocity().x) < maxSpeed) {
            rb.addVelocity(new Vector2f(-speed.x, speed.y));
        }
    }

    @Override
    public void preSolve(GameObject go, Contact contact, Vector2f contactNormal) {
        PlayerController pc = go.getComponent(PlayerController.class);
        if (pc != null) {
            contact.setEnabled(false);
            if (!hitPlayer) {
                pc.powerup();
                gameObject.destroy();
                hitPlayer = true;
            }
        }

        if (Math.abs(contactNormal.y) < 0.1f) {
            // more horizontal than vertical
            goingRight = (contactNormal.x < 0);
        }
    }
}
