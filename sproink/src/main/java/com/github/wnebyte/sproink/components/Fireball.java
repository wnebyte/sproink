package com.github.wnebyte.sproink.components;

import org.joml.Vector2f;
import org.jbox2d.dynamics.contacts.Contact;
import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.core.window.Window;
import com.github.wnebyte.sproink.core.ecs.Component;
import com.github.wnebyte.sproink.physics2d.components.RigidBody2D;

public class Fireball extends Component {

    private static int count = 0;

    public transient boolean goingRight = false;

    private transient RigidBody2D rb;

    private transient float speed = 1.7f;

    private transient Vector2f velocity = new Vector2f();

    private transient Vector2f acceleration = new Vector2f();

    private transient Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);

    private transient boolean onGround = false;

    private transient float lifetime = 4.0f;

    public static boolean canSpawn() {
        return (count < 4);
    }

    @Override
    public void start() {
        this.rb = gameObject.getComponent(RigidBody2D.class);
        acceleration.y = Window.getScene().getPhysics().getGravity().y * 0.7f;
        count++;
    }

    @Override
    public void update(float dt) {
        lifetime -= dt;
        if (lifetime <= 0) {
            expire();
            return;
        }

        if (goingRight) {
            velocity.x = speed;
        } else {
            velocity.x = -speed;
        }

        checkOnGround();
        if (onGround) {
            acceleration.y = 1.5f;
            velocity.y = 2.f;
        } else {
            acceleration.y = Window.getScene().getPhysics().getGravity().y * 0.7f;
        }

        velocity.y += acceleration.y * dt;
        velocity.y = Math.max(Math.min(velocity.y, terminalVelocity.y), -terminalVelocity.y);
        rb.setVelocity(velocity);
    }

    @Override
    public void preSolve(GameObject go, Contact contact, Vector2f contactNormal) {
        if (go.getComponent(PlayerController.class) != null || go.getComponent(Fireball.class) != null) {
            contact.setEnabled(false);
        }
    }

    @Override
    public void beginCollision(GameObject go, Contact contact, Vector2f contactNormal) {
        if (Math.abs(contactNormal.x) > 0.8f) { // hit horizontally
            goingRight = (contactNormal.x < 0);
        }
    }

    @Override
    public void endCollision(GameObject go, Contact contact, Vector2f contactNormal) {
        super.endCollision(go, contact, contactNormal);
    }

    public void expire() {
        count--;
        gameObject.destroy();
    }

    public void checkOnGround() {
        float innerWidth = 0.25f * 0.7f;
        float yVal = -0.09f;
        onGround = Window.getScene().getPhysics().checkOnGround(gameObject, innerWidth, yVal);
    }
}
