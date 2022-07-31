package com.github.wnebyte.engine.core;

import com.github.wnebyte.engine.core.window.Window;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.components.Sprite;
import com.github.wnebyte.engine.components.Spritesheet;
import com.github.wnebyte.engine.components.SpriteRenderer;
import com.github.wnebyte.engine.components.StateMachine;
import com.github.wnebyte.engine.animation.AnimationState;
import com.github.wnebyte.engine.util.ResourceFlyWeight;

public class Prefabs {

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject go = Window.getScene().createGameObject("Sprite_Object_Gen");
        go.transform.scale.x = sizeX;
        go.transform.scale.y = sizeY;
        SpriteRenderer spr = new SpriteRenderer();
        spr.setSprite(sprite);
        go.addComponent(spr);
        return go;
    }

    public static GameObject generateMario() {
        Spritesheet sprites = ResourceFlyWeight.getSpritesheet("/images/spritesheet.png");
        GameObject mario = generateSpriteObject(sprites.getSprite(0), 0.25f, 0.25f);

        AnimationState run = new AnimationState();
        run.title = "Run";
        float defaultFrameTime = 0.23f;
        run.addFrame(sprites.getSprite(0), defaultFrameTime);
        run.addFrame(sprites.getSprite(2), defaultFrameTime);
        run.addFrame(sprites.getSprite(3), defaultFrameTime);
        run.addFrame(sprites.getSprite(2), defaultFrameTime);
        run.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(run);
        stateMachine.setDefaultState(run.title);
        mario.addComponent(stateMachine);

        return mario;
    }

    public static GameObject generateQuestionBlock() {
        Spritesheet sprites = ResourceFlyWeight.getSpritesheet("/images/items.png");
        GameObject questionBlock = generateSpriteObject(sprites.getSprite(0), 0.25f, 0.25f);

        AnimationState flicker = new AnimationState();
        flicker.title = "Flicker";
        float defaultFrameTime = 0.23f;
        flicker.addFrame(sprites.getSprite(0), 0.57f);
        flicker.addFrame(sprites.getSprite(1), defaultFrameTime);
        flicker.addFrame(sprites.getSprite(2), defaultFrameTime);
        flicker.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(flicker);
        stateMachine.setDefaultState(flicker.title);
        questionBlock.addComponent(stateMachine);

        return questionBlock;
    }

}
