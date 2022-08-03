package com.github.wnebyte.engine.components;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.jbox2d.dynamics.contacts.Contact;
import com.github.wnebyte.engine.core.window.Window;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.core.Prefabs;
import com.github.wnebyte.engine.core.audio.Sound;
import com.github.wnebyte.engine.core.scene.LevelSceneInitializer;
import com.github.wnebyte.engine.core.scene.LevelEditorSceneInitializer;
import com.github.wnebyte.engine.util.ResourceFlyWeight;
import com.github.wnebyte.engine.physics2d.enums.BodyType;
import com.github.wnebyte.engine.physics2d.components.RigidBody2D;
import com.github.wnebyte.engine.physics2d.components.PillboxCollider;
import static com.github.wnebyte.engine.core.event.KeyListener.isKeyPressed;
import static com.github.wnebyte.engine.core.event.KeyListener.keyBeginPress;
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

    private transient SpriteRenderer spr;

    private transient RigidBody2D rb;

    private transient StateMachine stateMachine;

    private transient float bigJumpBoostFactor = 1.05f;

    private transient float playerWidth = 0.25f;

    private transient int jumpTime = 0;

    private transient Vector2f acceleration = new Vector2f();

    private transient Vector2f velocity = new Vector2f();

    private transient boolean isDead = false;

    private transient float hurtInvincibilityTimeLeft = 0;

    private transient float hurtInvincibilityTime = 1.4f;

    private transient float deadMaxHeight = 0;

    private transient float deadMinHeight = 0;

    private transient boolean deadGoingUp = true;

    private transient float blinkTime = 0.0f;

    private transient int enemyBounce = 0;

    private transient boolean playWinAnimation = false;

    private transient float timeToCastle = 4.5f;

    private transient float walkTime = 2.2f;

    @Override
    public void start() {
        this.spr = gameObject.getComponent(SpriteRenderer.class);
        this.rb = gameObject.getComponent(RigidBody2D.class);
        this.stateMachine = gameObject.getComponent(StateMachine.class);
        this.rb.setGravityScale(0.0f);
    }

    @Override
    public void update(float dt) {
        if (playWinAnimation) {
            checkOnGround();
            if (!onGround) {
                gameObject.transform.scale.x = -0.25f;
                gameObject.transform.position.y -= dt;
                stateMachine.trigger("stopRunning");
                stateMachine.trigger("stopJumping");
            } else {
                if (walkTime > 0) {
                    gameObject.transform.scale.x = 0.25f;
                    gameObject.transform.position.x += dt;
                    stateMachine.trigger("startRunning");
                }
                Sound sound = ResourceFlyWeight.getSound("/sounds/stage_clear.ogg");
                if (!sound.isPlaying()) {
                    sound.play();
                }

                timeToCastle -= dt;
                walkTime -= dt;

                if (timeToCastle <= 0) {
                    Window.setScene(new LevelEditorSceneInitializer());
                }
            }
            return;
        }

        if (isDead) {
            if (gameObject.transform.position.y < deadMaxHeight && deadGoingUp) {
                gameObject.transform.position.y += dt * walkSpeed / 2.0f;
            } else if (gameObject.transform.position.y >= deadMaxHeight && deadGoingUp) {
                deadGoingUp = false;
            }  else if (!deadGoingUp && gameObject.transform.position.y > deadMinHeight) {
                rb.setBodyType(BodyType.KINEMATIC);
                acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
                velocity.y += acceleration.y * dt;
                velocity.y = Math.max(Math.min(velocity.y, terminalVelocity.y), -terminalVelocity.y);
                rb.setVelocity(velocity);
                rb.setAngularVelocity(0.0f);
            } else if (!deadGoingUp && gameObject.transform.position.y <= deadMinHeight) {
                Window.setScene(new LevelSceneInitializer());
            }
            return;
        }

        if (hurtInvincibilityTimeLeft > 0) {
            hurtInvincibilityTimeLeft -= dt;
            blinkTime -= dt;

            if (blinkTime <= 0) {
                blinkTime = 0.2f;
                if (spr.getColor().w == 1) {
                    spr.setColor(new Vector4f(1, 1, 1, 0));
                } else {
                    spr.setColor(new Vector4f(1, 1, 1, 1));
                }
            } else {
                if (spr.getColor().w == 0) {
                    spr.setColor(new Vector4f(1, 1, 1, 1));
                }
            }
        }

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

        if (keyBeginPress(GLFW_KEY_E) && isFire() && Fireball.canSpawn()) {
            Vector2f pos = new Vector2f(gameObject.transform.position);
            if (gameObject.transform.scale.x > 0) { // mario is facing to the right
                pos.add(new Vector2f(0.26f, 0.0f));
            } else {
                pos.add(new Vector2f(-0.26f, 0.0f));
            }
            GameObject fireball = Prefabs.generateFireball(pos);
            fireball.getComponent(Fireball.class).goingRight = gameObject.transform.scale.x > 0;
            Window.getScene().addGameObjectToScene(fireball);
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
        } else if (enemyBounce > 0) {
            enemyBounce--;
            velocity.y = ((enemyBounce / 2.2f) * jumpBoost);
        }
        else if (!onGround) {
            if (jumpTime > 0) {
                velocity.y *= 0.35f;
                jumpTime = 0;
            }
            groundDebounce -= dt;
            acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
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
        onGround = Window.getPhysics().checkOnGround(gameObject, innerPlayerWidth, yVal);
    }

    @Override
    public void beginCollision(GameObject go, Contact contact, Vector2f contactNormal) {
        if (isDead) return;

        if (go.getComponent(Ground.class) != null) {
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
                pbc.setHeight(0.42f);
            }
        } else if (isBig()) {
            playerState = PlayerState.FIRE;
            ResourceFlyWeight.getSound("/sounds/powerup.ogg").play();
        }

        stateMachine.trigger("powerup");
    }

    public void die() {
        stateMachine.trigger("die");
        if (isSmall()) {
            velocity.set(0, 0);
            acceleration.set(0, 0);
            rb.setVelocity(new Vector2f());
            rb.setIsSensor();
            isDead = true;
            ResourceFlyWeight.getSound("/sounds/mario_die.ogg").play();
            deadMaxHeight = gameObject.transform.position.y + 0.3f;
            rb.setBodyType(BodyType.STATIC);
            if (gameObject.transform.position.y > 0) {
                deadMinHeight = -0.25f;
            }
        }
        else if (isBig()) {
            playerState = PlayerState.SMALL;
            gameObject.transform.scale.y = 0.25f;
            PillboxCollider pbc = gameObject.getComponent(PillboxCollider.class);
            if (pbc != null) {
                jumpBoost /= bigJumpBoostFactor;
                walkSpeed /= bigJumpBoostFactor;
                pbc.setHeight(0.31f);
            }
            hurtInvincibilityTimeLeft = hurtInvincibilityTime;
            ResourceFlyWeight.getSound("/sounds/pipe.ogg").play();
        } else if (isFire()) {
            playerState = PlayerState.BIG;
            hurtInvincibilityTimeLeft = hurtInvincibilityTime;
            ResourceFlyWeight.getSound("/sounds/pipe.ogg").play();
        }
    }

    public void playWinAnimation(GameObject go) {
        if (!playWinAnimation) {
            playWinAnimation = true;
            velocity.set(0.0f, 0.0f);
            acceleration.set(0.0f, 0.0f);
            rb.setVelocity(velocity);
            rb.setIsSensor();
            rb.setBodyType(BodyType.STATIC);
            gameObject.transform.position.x = go.transform.position.x;
            ResourceFlyWeight.getSound("/sounds/flagpole.ogg").play();
        }
    }

    public void setPosition(Vector2f pos) {
        gameObject.transform.position.set(pos);
        rb.setPosition(pos);
    }

    public void enemyBounce() {
        enemyBounce = 8;
    }

    public boolean isHurtInvincible() {
        return (hurtInvincibilityTimeLeft > 0 || playWinAnimation);
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
        return (playerState == PlayerState.INVINCIBLE || hurtInvincibilityTimeLeft > 0 || playWinAnimation);
    }

    public boolean isDead() {
        return isDead;
    }

    public boolean isAlive() {
        return !isDead;
    }

    public boolean hasWon() {
        return false;
    }
}
