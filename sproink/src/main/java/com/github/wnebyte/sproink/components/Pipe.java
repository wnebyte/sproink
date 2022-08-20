package com.github.wnebyte.sproink.components;

import com.github.wnebyte.sproink.core.Direction;
import org.joml.Vector2f;
import org.jbox2d.dynamics.contacts.Contact;
import com.github.wnebyte.sproink.core.ecs.Component;
import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.core.event.KeyListener;
import com.github.wnebyte.sproink.core.window.Window;
import com.github.wnebyte.sproink.util.ResourceFlyWeight;

import static org.lwjgl.glfw.GLFW.*;

public class Pipe extends Component {

    private Direction direction;

    private String connectingPipeName = "";

    private boolean isEntrance = false;

    private transient GameObject connectingPipe = null;

    private transient float entranceVectorThreshold = 0.6f; // 0.6f,

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
            boolean playerEntering = false;
            switch (direction) {
                case UP:
                    if ((KeyListener.isKeyPressed(GLFW_KEY_DOWN) || KeyListener.isKeyPressed(GLFW_KEY_S)) &&
                            isEntrance && playerAtEntrance()) {
                        playerEntering = true;
                    }
                    break;
                case DOWN:
                    if ((KeyListener.isKeyPressed(GLFW_KEY_UP) || KeyListener.isKeyPressed(GLFW_KEY_W)) &&
                            isEntrance && playerAtEntrance()) {
                        playerEntering = true;
                    }
                    break;
                case LEFT:
                    if ((KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D)) &&
                            isEntrance && playerAtEntrance()) {
                        playerEntering = true;
                    }
                    break;
                case RIGHT:
                    if ((KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)) &&
                            isEntrance && playerAtEntrance()) {
                        playerEntering = true;
                    }
                    break;
            }

            if (playerEntering) {
                Vector2f pos = getPlayerPosition(connectingPipe);
                collidingPlayer.setPosition(pos);
                ResourceFlyWeight.getSound("/sounds/pipe.ogg").play();
            }
        }
    }

    private boolean playerAtEntrance() {
        if (collidingPlayer == null) {
            return false;
        }

        Vector2f min = new Vector2f(gameObject.transform.position)
                .sub(new Vector2f(gameObject.transform.scale)
                        .mul(0.5f));
        Vector2f max = new Vector2f(gameObject.transform.position)
                .add(new Vector2f(gameObject.transform.scale)
                        .mul(0.5f));
        Vector2f playerMin = new Vector2f(collidingPlayer.gameObject.transform.position)
                .sub(new Vector2f(collidingPlayer.gameObject.transform.scale)
                        .mul(0.5f));
        Vector2f playerMax = new Vector2f(collidingPlayer.gameObject.transform.position)
                .add(new Vector2f(collidingPlayer.gameObject.transform.scale)
                        .mul(0.5f));

        switch (direction) {
            case UP:
                return playerMin.y >= max.y &&
                        playerMax.x > min.x &&
                        playerMin.x < max.x;
            case DOWN:
                return playerMax.y <= min.y &&
                        playerMax.x > min.x &&
                        playerMin.x < max.x;
            case RIGHT:
                return playerMin.x >= max.x &&
                        playerMax.y > min.y &&
                        playerMin.y < max.y;
            case LEFT:
                return playerMin.x <= min.x &&
                        playerMax.y > min.y &&
                        playerMin.y < max.y;
        }

        return false;
    }

    @Override
    public void beginCollision(GameObject go, Contact contact, Vector2f contactNormal) {
        PlayerController pc = go.getComponent(PlayerController.class);
        if (pc != null) {
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
