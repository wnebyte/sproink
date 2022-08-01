package com.github.wnebyte.engine.components;

import com.github.wnebyte.engine.util.ResourceFlyWeight;

public class BreakableBlock extends Block {

    @Override
    void playerHit(PlayerController playerController) {
        if (!playerController.isSmall()) {
            ResourceFlyWeight.getSound("/sounds/break_block.ogg").play();
            gameObject.destroy();
        }
    }
}
