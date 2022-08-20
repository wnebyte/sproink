package com.github.wnebyte.sproink.components;

import org.joml.Vector2f;
import com.github.wnebyte.sproink.core.ecs.Component;
import com.github.wnebyte.sproink.util.ResourceFlyWeight;

public class BlockCoin extends Component {

    private Vector2f topY;

    private float coinSpeed = 1.4f;

    @Override
    public void start() {
        topY = new Vector2f(gameObject.transform.position.y).add(0, 0.5f);
        ResourceFlyWeight.getSound("/sounds/coin.ogg").play();
    }

    @Override
    public void update(float dt) {
        if (gameObject.transform.position.y < topY.y) {
            gameObject.transform.position.y += dt * coinSpeed;
            gameObject.transform.scale.x -= (0.5f * dt) % -1.0f;
        } else {
            gameObject.destroy();
        }
    }
}
