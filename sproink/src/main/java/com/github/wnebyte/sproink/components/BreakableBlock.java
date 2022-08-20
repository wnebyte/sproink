package com.github.wnebyte.sproink.components;

import com.github.wnebyte.sproink.util.ResourceFlyWeight;

public class BreakableBlock extends Block {

    @Override
    void playerHit(PlayerController playerController) {
        if (!playerController.isSmall()) {
            ResourceFlyWeight.getSound("/sounds/break_block.ogg").play();
            gameObject.destroy();
        }
    }
}
