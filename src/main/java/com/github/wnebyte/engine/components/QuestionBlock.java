package com.github.wnebyte.engine.components;

import com.github.wnebyte.engine.core.Prefabs;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.core.window.Window;

public class QuestionBlock extends Block {

    public enum BlockType {
        COIN,
        POWERUP,
        INVINCIBILITY
    }

    public BlockType blockType = BlockType.COIN;

    @Override
    public void playerHit(PlayerController playerController) {
        switch (blockType) {
            case COIN:
                doCoin(playerController);
                break;
            case POWERUP:
                doPowerup(playerController);
                break;
            case INVINCIBILITY:
                doInvincibility(playerController);
                break;
        }

        StateMachine stateMachine = gameObject.getComponent(StateMachine.class);
        if (stateMachine != null) {
            stateMachine.trigger("setInactive");
            setInactive();
        }
    }

    private void doCoin(PlayerController playerController) {
        GameObject coin = Prefabs.generateBlockCoin();
        coin.transform.position.set(gameObject.transform.position);
        coin.transform.position.y += 0.25f;
        Window.getScene().addGameObjectToScene(coin);
    }

    private void doPowerup(PlayerController playerController) {
        // Todo: impl
    }

    private void doInvincibility(PlayerController playerController) {
        // Todo: impl
    }
}
