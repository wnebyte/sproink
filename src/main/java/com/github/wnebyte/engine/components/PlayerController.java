package com.github.wnebyte.engine.components;

import org.joml.Vector2f;
import com.github.wnebyte.engine.core.window.Window;
import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.physics2d.components.RigidBody2D;
import static com.github.wnebyte.engine.core.event.KeyListener.isKeyPressed;
import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component {

    public float walkSpeed = 1.9f;

    public float jumpBoost = 1.0f;

    public float jumpImpulse = 3.0f;

    public float slowDownForce = 0.05f;

    public Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);

    public transient boolean onGround = false;

    private transient float groundDebounce = 0.0f;

    private transient float groundDebounceTime = 0.1f;

    private transient RigidBody2D rb;

    private transient StateMachine stateMachine;

    private transient float bigJumpFactor = 1.05f;

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
            if (velocity.x > 0){
                velocity.x = Math.max(0, velocity.x - slowDownForce);
            } else if (velocity.x < 0) {
                velocity.x = Math.min(0, velocity.x + slowDownForce);
            } else {
                stateMachine.trigger("stopRunning");
            }
        }

        acceleration.y = Window.getPhysics2d().getGravity().y * 0.7f;

        velocity.x += acceleration.x * dt;
        velocity.y += acceleration.y * dt;
        velocity.x = Math.max(Math.min(velocity.x, terminalVelocity.x), -terminalVelocity.x);
        velocity.y = Math.max(Math.min(velocity.y, terminalVelocity.y), -terminalVelocity.y);
        rb.setVelocity(velocity);
        rb.setAngularVelocity(0.0f);
    }
}
