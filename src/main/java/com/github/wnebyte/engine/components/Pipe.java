package com.github.wnebyte.engine.components;

import org.joml.Vector2f;
import org.jbox2d.dynamics.contacts.Contact;
import com.github.wnebyte.engine.core.Direction;
import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.core.event.KeyListener;
import com.github.wnebyte.engine.core.window.Window;
import com.github.wnebyte.engine.util.ResourceFlyWeight;
import static org.lwjgl.glfw.GLFW.*;

public class Pipe extends Component {

    private Direction direction;

    private String connectingPipeName = "";

    private boolean isEntrance = false;

    private transient GameObject connectingPipe = null;

    private transient float entranceVectorThreshold = 0.15f; // 0.6f,

    private transient PlayerController collidingPlayer = null;

    public Pipe(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void start() {
        connectingPipe = Window.getScene().getGameObject(connectingPipeName);
    }

    @Override
    public void update(float dt) {
        if (connectingPipe == null) return;

        if (collidingPlayer != null) {
            boolean entering = false;
            switch (direction) {
                case UP:
                    if ((KeyListener.isKeyPressed(GLFW_KEY_DOWN) || KeyListener.isKeyPressed(GLFW_KEY_S)) && isEntrance) {
                        entering = true;
                    }
                    break;
                case DOWN:
                    if ((KeyListener.isKeyPressed(GLFW_KEY_UP) || KeyListener.isKeyPressed(GLFW_KEY_W)) && isEntrance) {
                        entering = true;
                    }
                    break;
                case LEFT:
                    if ((KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D)) && isEntrance) {
                        entering = true;
                    }
                    break;
                case RIGHT:
                    if ((KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)) && isEntrance) {
                        entering = true;
                    }
                    break;
            }

            if (entering) {
                Vector2f pos = getPlayerPosition(connectingPipe);
                collidingPlayer.setPosition(pos);
                ResourceFlyWeight.getSound("/sounds/pipe.ogg").play();
            }
        }
    }

    @Override
    public void beginCollision(GameObject go, Contact contact, Vector2f contactNormal) {
        PlayerController pc = go.getComponent(PlayerController.class);
        if (pc != null) {
            switch (direction) {
                case UP:
                    // player is pushing up
                    if (contactNormal.y < entranceVectorThreshold) {
                        return;
                    }
                    break;
                case DOWN:
                    if (contactNormal.y > -entranceVectorThreshold) {
                        return;
                    }
                    break;
                case LEFT:
                    if (contactNormal.x > -entranceVectorThreshold) {
                        return;
                    }
                    break;
                case RIGHT:
                    if (contactNormal.x < entranceVectorThreshold) {
                        return;
                    }
                    break;
            }

            collidingPlayer = pc;
        }
    }

    @Override
    public void endCollision(GameObject go, Contact contact, Vector2f contactNormal) {
        PlayerController pc = go.getComponent(PlayerController.class);
        if (pc != null) {
            collidingPlayer = null;
        }
    }

    private Vector2f getPlayerPosition(GameObject pipe) {
        Pipe c = pipe.getComponent(Pipe.class);
        switch (c.direction) {
            case UP:
                return new Vector2f(pipe.transform.position).add(0.5f, 0.5f);
            case DOWN:
                return new Vector2f(pipe.transform.position).add(0.0f, -0.5f);
            case LEFT:
                return new Vector2f(pipe.transform.position).add(-0.5f, 0.0f);
            case RIGHT:
                return new Vector2f(pipe.transform.position).add(0.5f, 0.0f);
            default:
                return new Vector2f();
        }
    }
}
