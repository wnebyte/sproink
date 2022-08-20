package com.github.wnebyte.sproink.components;

import org.joml.Vector2f;
import org.jbox2d.dynamics.contacts.Contact;
import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.core.ecs.Component;
import com.github.wnebyte.sproink.util.ResourceFlyWeight;

public abstract class Block extends Component {

    private transient boolean bopGoingUp = true;

    private transient boolean doBopAnimation = false;

    private transient boolean active = true;

    private transient Vector2f bopStart;

    private transient Vector2f topBopLocation;

    public float bopSpeed = 0.4f;

    @Override
    public void start() {
        bopStart = new Vector2f(gameObject.transform.position);
        topBopLocation = new Vector2f(bopStart).add(0.0f, 0.02f);
    }

    @Override
    public void update(float dt) {
        if (doBopAnimation) {
            if (bopGoingUp) {
                if (gameObject.transform.position.y < topBopLocation.y) {
                    gameObject.transform.position.y += bopSpeed * dt;
                } else {
                    bopGoingUp = false;
                }
            } else {
                if (gameObject.transform.position.y > bopStart.y) {
                    gameObject.transform.position.y -= bopSpeed * dt;
                } else {
                    gameObject.transform.position.y = bopStart.y;
                    bopGoingUp = true;
                    doBopAnimation = false;
                }
            }
        }
    }

    @Override
    public void beginCollision(GameObject go, Contact contact, Vector2f contactNormal) {
        PlayerController playerController = go.getComponent(PlayerController.class);
        if (active && playerController != null && contactNormal.y < -0.8f) {
            doBopAnimation = true;
            // Todo: move play sound to subclass
            ResourceFlyWeight.getSound("/sounds/bump.ogg").play();
            playerHit(playerController);
        }
    }

    public void setInactive() {
        active = false;
    }

    abstract void playerHit(PlayerController playerController);
}
