package com.github.wnebyte.sproink.components;

import org.joml.Vector2f;
import org.jbox2d.dynamics.contacts.Contact;
import com.github.wnebyte.sproink.core.ecs.Component;
import com.github.wnebyte.sproink.core.ecs.GameObject;

public class Flagpole extends Component {

    private final boolean isTop;

    public Flagpole(boolean isTop) {
        this.isTop = isTop;
    }

    @Override
    public void beginCollision(GameObject go, Contact contact, Vector2f contactNormal) {
        PlayerController pc = go.getComponent(PlayerController.class);
        if (pc != null) {
            pc.playWinAnimation(gameObject);
        }
    }
}
