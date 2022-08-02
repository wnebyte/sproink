package com.github.wnebyte.engine.components;

import org.joml.Vector2f;
import org.jbox2d.dynamics.contacts.Contact;
import com.github.wnebyte.engine.core.window.Window;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.util.ResourceFlyWeight;
import com.github.wnebyte.engine.physics2d.components.RigidBody2D;
import com.github.wnebyte.engine.physics2d.components.PillboxCollider;
import static com.github.wnebyte.engine.core.event.KeyListener.isKeyPressed;
import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component {

    private enum PlayerState {
        SMALL,
        BIG,
        FIRE,
        INVINCIBLE
    }

    public float walkSpeed = 1.9f;

    public float jumpBoost = 1.0f;

    public float jumpImpulse = 3.0f;

    public float slowDownForce = 0.05f;

    public Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);

    private PlayerState playerState = PlayerState.SMALL;

    public transient boolean onGround = false;

    private transient float groundDebounce = 0.0f;

    private transient float groundDebounceTime = 0.1f;

    private transient RigidBody2D rb;

    private transient StateMachine stateMachine;

    private transient float bigJumpBoostFactor = 1.05f;

    private transient float playerWidth = 0.25f;

    private transient int jumpTime = 0;

    private transient Vector2f acceleration = new Vector2f();

    private transient Vector2f velocity = new Vector2f();

    private transient boolean isDead = false;

    private transient int enemyBounce = 0;

    @Override
    public void start() {
        this.rb = gameObject.getComponent(RigidBody2D.class);
        this.stateMachine = gameObject.getComponent(StateMachine.class);
        this.rb.setGravityScale(0.0f);
    }

    @Override
    public void update(float dt) {
        if (isKeyPressed(GLFW_KEY_RIGHT) || isKeyPressed(GLFW_KEY_D)) {
            gameObject.transform.scale.x = playerWidth;
            acceleration.x = walkSpeed;

            if (velocity.x < 0) {
                stateMachine.trigger("switchDirection");
                velocity.x += slowDownForce;
            } else {
                stateMachine.trigger("startRunning");
            }
        } else if (isKeyPressed(GLFW_KEY_LEFT) || isKeyPressed(GLFW_KEY_A)) {
            gameObject.transform.scale.x = -playerWidth;
            acceleration.x = -walkSpeed;

            if (velocity.x > 0) {
                stateMachine.trigger("switchDirection");
                velocity.x -= slowDownForce;
            } else {
                stateMachine.trigger("startRunning");
            }
        } else {
            acceleration.x = 0.0f;
            if (velocity.x > 0) {
                velocity.x = Math.max(0, velocity.x - slowDownForce);
            } else if (velocity.x < 0) {
                velocity.x = Math.min(0, velocity.x + slowDownForce);
            } else {
                stateMachine.trigger("stopRunning");
            }
        }

        checkOnGround();

        if (isKeyPressed(GLFW_KEY_SPACE) &&
                (jumpTime > 0 || onGround || groundDebounce > 0)) {
            if ((onGround || groundDebounce > 0) && jumpTime == 0) {
                ResourceFlyWeight.getSound("/sounds/jump-small.ogg").play();
                jumpTime = 28;
                velocity.y = jumpImpulse;
            } else if (jumpTime > 0) {
                jumpTime--;
                velocity.y = ((jumpTime / 2.2f) * jumpBoost);
            } else {
                velocity.y = 0;
            }
            groundDebounce = 0;
        } else if (!onGround) {
            if (jumpTime > 0) {
                velocity.y *= 0.35f;
                jumpTime = 0;
            }
            groundDebounce -= dt;
            acceleration.y = Window.getPhysics2d().getGravity().y * 0.7f;
        } else {
            // on ground
            velocity.y = 0;
            acceleration.y = 0;
            groundDebounce = groundDebounceTime;
        }

        velocity.x += acceleration.x * dt;
        velocity.y += acceleration.y * dt;
        velocity.x = Math.max(Math.min(velocity.x, terminalVelocity.x), -terminalVelocity.x);
        velocity.y = Math.max(Math.min(velocity.y, terminalVelocity.y), -terminalVelocity.y);
        rb.setVelocity(velocity);
        rb.setAngularVelocity(0.0f);

        if (!onGround) {
            stateMachine.trigger("jump");
        } else {
            stateMachine.trigger("stopJumping");
        }
    }

    public void checkOnGround() {
        float innerPlayerWidth = playerWidth * 0.6f;
        float yVal = isSmall() ? -0.14f : -0.24f;
        onGround = Window.getPhysics2d().checkOnGround(gameObject, innerPlayerWidth, yVal);
    }

    @Override
    public void beginCollision(GameObject collidingGo, Contact contact, Vector2f contactNormal) {
        if (isDead) return;

        if (collidingGo.getComponent(Ground.class) != null) {
            if (Math.abs(contactNormal.x) > 0.8f) {
                // horizontal hit
                velocity.x = 0;
            } else if (contactNormal.y > 0.8f) {
                // hit on the bottom of a block
                velocity.y = 0;
                acceleration.y = 0;
                jumpTime = 0;
            }
        }
    }

    public void powerup() {
        if (isSmall()) {
            playerState = PlayerState.BIG;
            ResourceFlyWeight.getSound("/sounds/powerup.ogg").play();
            gameObject.transform.scale.y = 0.42f;
            PillboxCollider pbc = gameObject.getComponent(PillboxCollider.class);
            if (pbc != null) {
                jumpBoost *= bigJumpBoostFactor;
                walkSpeed *= bigJumpBoostFactor;
                pbc.setHeight(0.63f);
            }
        } else if (isBig()) {
            playerState = PlayerState.FIRE;
            ResourceFlyWeight.getSound("/sounds/powerup.ogg").play();
        }

        stateMachine.trigger("powerup");
    }

    public boolean isBig() {
        return (playerState == PlayerState.BIG);
    }

    public boolean isSmall() {
        return (playerState == PlayerState.SMALL);
    }

    public boolean isFire() {
        return (playerState == PlayerState.FIRE);
    }

    public boolean isInvincible() {
        return (playerState == PlayerState.INVINCIBLE);
    }

    public boolean isDead() {
        return isDead;
    }
}
