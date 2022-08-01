package com.github.wnebyte.engine.core;

import org.joml.Vector2f;
import com.github.wnebyte.engine.components.*;
import com.github.wnebyte.engine.core.window.Window;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.animation.AnimationState;
import com.github.wnebyte.engine.util.ResourceFlyWeight;
import com.github.wnebyte.engine.physics2d.components.Box2DCollider;
import com.github.wnebyte.engine.physics2d.components.PillboxCollider;
import com.github.wnebyte.engine.physics2d.components.RigidBody2D;
import com.github.wnebyte.engine.physics2d.enums.BodyType;

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
        Spritesheet playerSprites = ResourceFlyWeight.getSpritesheet("/images/spritesheet.png");
        Spritesheet bigPlayerSprites = ResourceFlyWeight.getSpritesheet("/images/bigSpritesheet.png");
        GameObject mario = generateSpriteObject(playerSprites.getSprite(0), 0.25f, 0.25f);

        // Little mario animations
        AnimationState run = new AnimationState();
        run.title = "Run";
        float defaultFrameTime = 0.2f;
        run.addFrame(playerSprites.getSprite(0), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(3), defaultFrameTime);
        run.addFrame(playerSprites.getSprite(2), defaultFrameTime);
        run.setLoop(true);

        AnimationState switchDirection = new AnimationState();
        switchDirection.title = "Switch Direction";
        switchDirection.addFrame(playerSprites.getSprite(4), 0.1f);
        switchDirection.setLoop(false);

        AnimationState idle = new AnimationState();
        idle.title = "Idle";
        idle.addFrame(playerSprites.getSprite(0), 0.1f);
        idle.setLoop(false);

        AnimationState jump = new AnimationState();
        jump.title = "Jump";
        jump.addFrame(playerSprites.getSprite(5), 0.1f);
        jump.setLoop(false);

        // Big mario animations
        AnimationState bigRun = new AnimationState();
        bigRun.title = "BigRun";
        bigRun.addFrame(bigPlayerSprites.getSprite(0), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(1), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(2), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(3), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(2), defaultFrameTime);
        bigRun.addFrame(bigPlayerSprites.getSprite(1), defaultFrameTime);
        bigRun.setLoop(true);

        AnimationState bigSwitchDirection = new AnimationState();
        bigSwitchDirection.title = "Big Switch Direction";
        bigSwitchDirection.addFrame(bigPlayerSprites.getSprite(4), 0.1f);
        bigSwitchDirection.setLoop(false);

        AnimationState bigIdle = new AnimationState();
        bigIdle.title = "BigIdle";
        bigIdle.addFrame(bigPlayerSprites.getSprite(0), 0.1f);
        bigIdle.setLoop(false);

        AnimationState bigJump = new AnimationState();
        bigJump.title = "BigJump";
        bigJump.addFrame(bigPlayerSprites.getSprite(5), 0.1f);
        bigJump.setLoop(false);

        // Fire mario animations
        int fireOffset = 21;
        AnimationState fireRun = new AnimationState();
        fireRun.title = "FireRun";
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 1), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 2), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 3), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 2), defaultFrameTime);
        fireRun.addFrame(bigPlayerSprites.getSprite(fireOffset + 1), defaultFrameTime);
        fireRun.setLoop(true);

        AnimationState fireSwitchDirection = new AnimationState();
        fireSwitchDirection.title = "Fire Switch Direction";
        fireSwitchDirection.addFrame(bigPlayerSprites.getSprite(fireOffset + 4), 0.1f);
        fireSwitchDirection.setLoop(false);

        AnimationState fireIdle = new AnimationState();
        fireIdle.title = "FireIdle";
        fireIdle.addFrame(bigPlayerSprites.getSprite(fireOffset), 0.1f);
        fireIdle.setLoop(false);

        AnimationState fireJump = new AnimationState();
        fireJump.title = "FireJump";
        fireJump.addFrame(bigPlayerSprites.getSprite(fireOffset + 5), 0.1f);
        fireJump.setLoop(false);

        AnimationState die = new AnimationState();
        die.title = "Die";
        die.addFrame(playerSprites.getSprite(6), 0.1f);
        die.setLoop(false);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(run);
        stateMachine.addState(idle);
        stateMachine.addState(switchDirection);
        stateMachine.addState(jump);
        stateMachine.addState(die);

        stateMachine.addState(bigRun);
        stateMachine.addState(bigIdle);
        stateMachine.addState(bigSwitchDirection);
        stateMachine.addState(bigJump);

        stateMachine.addState(fireRun);
        stateMachine.addState(fireIdle);
        stateMachine.addState(fireSwitchDirection);
        stateMachine.addState(fireJump);

        stateMachine.setDefaultState(idle.title);
        stateMachine.addStateTrigger(run.title, switchDirection.title, "switchDirection");
        stateMachine.addStateTrigger(run.title, idle.title, "stopRunning");
        stateMachine.addStateTrigger(run.title, jump.title, "jump");
        stateMachine.addStateTrigger(switchDirection.title, idle.title, "stopRunning");
        stateMachine.addStateTrigger(switchDirection.title, run.title, "startRunning");
        stateMachine.addStateTrigger(switchDirection.title, jump.title, "jump");
        stateMachine.addStateTrigger(idle.title, run.title, "startRunning");
        stateMachine.addStateTrigger(idle.title, jump.title, "jump");
        stateMachine.addStateTrigger(jump.title, idle.title, "stopJumping");

        stateMachine.addStateTrigger(bigRun.title, bigSwitchDirection.title, "switchDirection");
        stateMachine.addStateTrigger(bigRun.title, bigIdle.title, "stopRunning");
        stateMachine.addStateTrigger(bigRun.title, bigJump.title, "jump");
        stateMachine.addStateTrigger(bigSwitchDirection.title, bigIdle.title, "stopRunning");
        stateMachine.addStateTrigger(bigSwitchDirection.title, bigRun.title, "startRunning");
        stateMachine.addStateTrigger(bigSwitchDirection.title, bigJump.title, "jump");
        stateMachine.addStateTrigger(bigIdle.title, bigRun.title, "startRunning");
        stateMachine.addStateTrigger(bigIdle.title, bigJump.title, "jump");
        stateMachine.addStateTrigger(bigJump.title, bigIdle.title, "stopJumping");

        stateMachine.addStateTrigger(fireRun.title, fireSwitchDirection.title, "switchDirection");
        stateMachine.addStateTrigger(fireRun.title, fireIdle.title, "stopRunning");
        stateMachine.addStateTrigger(fireRun.title, fireJump.title, "jump");
        stateMachine.addStateTrigger(fireSwitchDirection.title, fireIdle.title, "stopRunning");
        stateMachine.addStateTrigger(fireSwitchDirection.title, fireRun.title, "startRunning");
        stateMachine.addStateTrigger(fireSwitchDirection.title, fireJump.title, "jump");
        stateMachine.addStateTrigger(fireIdle.title, fireRun.title, "startRunning");
        stateMachine.addStateTrigger(fireIdle.title, fireJump.title, "jump");
        stateMachine.addStateTrigger(fireJump.title, fireIdle.title, "stopJumping");

        stateMachine.addStateTrigger(run.title, bigRun.title, "powerup");
        stateMachine.addStateTrigger(idle.title, bigIdle.title, "powerup");
        stateMachine.addStateTrigger(switchDirection.title, bigSwitchDirection.title, "powerup");
        stateMachine.addStateTrigger(jump.title, bigJump.title, "powerup");
        stateMachine.addStateTrigger(bigRun.title, fireRun.title, "powerup");
        stateMachine.addStateTrigger(bigIdle.title, fireIdle.title, "powerup");
        stateMachine.addStateTrigger(bigSwitchDirection.title, fireSwitchDirection.title, "powerup");
        stateMachine.addStateTrigger(bigJump.title, fireJump.title, "powerup");

        stateMachine.addStateTrigger(bigRun.title, run.title, "damage");
        stateMachine.addStateTrigger(bigIdle.title, idle.title, "damage");
        stateMachine.addStateTrigger(bigSwitchDirection.title, switchDirection.title, "damage");
        stateMachine.addStateTrigger(bigJump.title, jump.title, "damage");
        stateMachine.addStateTrigger(fireRun.title, bigRun.title, "damage");
        stateMachine.addStateTrigger(fireIdle.title, bigIdle.title, "damage");
        stateMachine.addStateTrigger(fireSwitchDirection.title, bigSwitchDirection.title, "damage");
        stateMachine.addStateTrigger(fireJump.title, bigJump.title, "damage");

        stateMachine.addStateTrigger(run.title, die.title, "die");
        stateMachine.addStateTrigger(switchDirection.title, die.title, "die");
        stateMachine.addStateTrigger(idle.title, die.title, "die");
        stateMachine.addStateTrigger(jump.title, die.title, "die");
        stateMachine.addStateTrigger(bigRun.title, run.title, "die");
        stateMachine.addStateTrigger(bigSwitchDirection.title, switchDirection.title, "die");
        stateMachine.addStateTrigger(bigIdle.title, idle.title, "die");
        stateMachine.addStateTrigger(bigJump.title, jump.title, "die");
        stateMachine.addStateTrigger(fireRun.title, bigRun.title, "die");
        stateMachine.addStateTrigger(fireSwitchDirection.title, bigSwitchDirection.title, "die");
        stateMachine.addStateTrigger(fireIdle.title, bigIdle.title, "die");
        stateMachine.addStateTrigger(fireJump.title, bigJump.title, "die");
        mario.addComponent(stateMachine);

        PillboxCollider pbc = new PillboxCollider();
        pbc.setWidth(0.39f);
        pbc.setHeight(0.31f);

        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.DYNAMIC);
        rb.setContinuousCollision(false);
        rb.setFixedRotation(true);
        rb.setMass(25.0f);

        mario.addComponent(pbc);
        mario.addComponent(rb);
        mario.addComponent(new PlayerController());

        return mario;
    }

    public static GameObject generateQuestionBlock() {
        Spritesheet items = ResourceFlyWeight.getSpritesheet("/images/items.png");
        GameObject questionBlock = generateSpriteObject(items.getSprite(0), 0.25f, 0.25f);

        AnimationState flicker = new AnimationState();
        flicker.title = "Flicker";
        float defaultFrameTime = 0.23f;
        flicker.addFrame(items.getSprite(0), 0.57f);
        flicker.addFrame(items.getSprite(1), defaultFrameTime);
        flicker.addFrame(items.getSprite(2), defaultFrameTime);
        flicker.setLoop(true);

        AnimationState inactive = new AnimationState();
        inactive.title = "Inactive";
        inactive.addFrame(items.getSprite(3), 0.1f);
        inactive.setLoop(false);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(flicker);
        stateMachine.addState(inactive);
        stateMachine.setDefaultState(flicker.title);
        stateMachine.addStateTrigger(flicker.title, inactive.title, "setInactive");
        questionBlock.addComponent(stateMachine);
        questionBlock.addComponent(new QuestionBlock());

        RigidBody2D rb = new RigidBody2D();
        rb.setBodyType(BodyType.STATIC);
        questionBlock.addComponent(rb);
        Box2DCollider boxCollider = new Box2DCollider();
        boxCollider.setHalfSize(new Vector2f(0.25f, 0.25f));
        questionBlock.addComponent(boxCollider);
        questionBlock.addComponent(new Ground());

        return questionBlock;
    }

    public static GameObject generateBlockCoin() {
        Spritesheet items = ResourceFlyWeight.getSpritesheet("/images/items.png");
        GameObject coin = generateSpriteObject(items.getSprite(7), 0.25f, 0.25f);

        AnimationState coinFlip = new AnimationState();
        coinFlip.title = "CoinFlip";
        float defaultFrameTime = 0.23f;
        coinFlip.addFrame(items.getSprite(7), 0.57f);
        coinFlip.addFrame(items.getSprite(8), defaultFrameTime);
        coinFlip.addFrame(items.getSprite(9), defaultFrameTime);
        coinFlip.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(coinFlip);
        stateMachine.setDefaultState(coinFlip.title);
        coin.addComponent(stateMachine);
        coin.addComponent(new BlockCoin());

        return coin;
    }

}
