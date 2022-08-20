package com.github.wnebyte.sproink.components;

import org.joml.Vector2f;
import org.jbox2d.dynamics.contacts.Contact;
import com.github.wnebyte.sproink.core.window.Window;
import com.github.wnebyte.sproink.core.camera.Camera;
import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.core.ecs.Component;
import com.github.wnebyte.sproink.util.ResourceFlyWeight;
import com.github.wnebyte.sproink.physics2d.components.RigidBody2D;

public class TurtleAI extends Component {

    private transient RigidBody2D rb;

    private transient float walkSpeed = 0.6f;

    private transient Vector2f velocity = new Vector2f();

    private transient Vector2f acceleration = new Vector2f();

    private transient Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);

    private transient boolean goingRight = false;

    private transient boolean onGround = false;

    private transient boolean isDead = false;

    private transient boolean isMoving = false;

    private transient StateMachine stateMachine;

    private float movingDebounce = 0.32f;

    @Override
    public void start() {
        this.stateMachine = gameObject.getComponent(StateMachine.class);
        this.rb = gameObject.getComponent(RigidBody2D.class);
        this.acceleration.y = Window.getScene().getPhysics().getGravity().y * 0.7f;
    }

    @Override
    public void update(float dt) {
        movingDebounce -= dt;
        Camera camera = Window.getScene().getCamera();
        if (gameObject.transform.position.x >
                camera.getPosition().x + camera.getProjectionSize().x * camera.getZoom()) {
            return;
        }

        if (!isDead || isMoving) {
            if (goingRight) {
                gameObject.transform.scale.x = -0.25f;
                velocity.x = walkSpeed;
            } else {
                gameObject.transform.scale.x = 0.25f;
                velocity.x = -walkSpeed;
            }
            acceleration.x = 0;
        } else {
            velocity.x = 0;
        }

        checkOnGround();
        if (onGround) {
            acceleration.y = 0;
            velocity.y = 0;
        } else {
            acceleration.y = Window.getScene().getPhysics().getGravity().y * 0.7f;
        }

        velocity.y += acceleration.y * dt;
        velocity.y = Math.max(Math.min(velocity.y, terminalVelocity.y), -terminalVelocity.y);
        rb.setVelocity(velocity);

        // turtle is to the left of the camera
        if (gameObject.transform.position.x < camera.getPosition().x - 0.5f
               // || gameObject.transform.position.y < 0.0f
        ) {
            gameObject.destroy();
        }
    }

    @Override
    public void preSolve(GameObject go, Contact contact, Vector2f contactNormal) {
        GoombaAI goomba = go.getComponent(GoombaAI.class);
        if (isDead && isMoving && goomba != null) {
            goomba.stomp();
            contact.setEnabled(false);
            ResourceFlyWeight.getSound("/sounds/kick.ogg").play();
        }
    }

    @Override
    public void beginCollision(GameObject go, Contact contact, Vector2f contactNormal) {
        PlayerController pc = go.getComponent(PlayerController.class);
        if (pc != null) {
            if (!isDead && !pc.isDead() && !pc.isHurtInvincible() && contactNormal.y > 0.58f) {
                pc.enemyBounce();
                stomp();
                walkSpeed *= 3.0f;
            } else if (movingDebounce < 0 && !pc.isDead() && !pc.isHurtInvincible() && (isMoving || !isDead) &&
                    contactNormal.y < 0.58f) {
                pc.die();
            } else if (!pc.isDead() && !pc.isHurtInvincible()) {
                if (isDead && contactNormal.y > 0.58f) {
                    pc.enemyBounce();
                    isMoving = !isMoving;
                    goingRight = (contactNormal.x < 0);
                } else if (isDead && !isMoving) {
                    isMoving = true;
                    goingRight = (contactNormal.x < 0);
                    movingDebounce = 0.32f;
                }
            }
        } else if (Math.abs(contactNormal.y) < 0.1f && !go.isDead()) { // hit on the side
            goingRight = (contactNormal.x < 0);
            if (isMoving && isDead) {
                ResourceFlyWeight.getSound("/sounds/bump.ogg").play();
            }
        }

        Fireball fireball = go.getComponent(Fireball.class);
        if (fireball != null) {
            stomp();
            fireball.expire();
        }
    }

    public void stomp() {
        isDead = true;
        isMoving = false;
        velocity.zero();
        rb.setVelocity(velocity);
        rb.setAngularVelocity(0.0f);
        rb.setGravityScale(0.0f);
        stateMachine.trigger("squashMe");
        ResourceFlyWeight.getSound("/sounds/bump.ogg").play();
    }

    public void checkOnGround() {
        float innerWidth = 0.25f * 0.7f;
        float yVal = -0.2f;
        onGround = Window.getScene().getPhysics().checkOnGround(gameObject, innerWidth, yVal);
    }
}
