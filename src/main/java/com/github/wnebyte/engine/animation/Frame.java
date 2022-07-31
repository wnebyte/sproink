package com.github.wnebyte.engine.animation;

import com.github.wnebyte.engine.components.Sprite;

public class Frame {

    public Sprite sprite;

    public float frameTime;

    public Frame() {

    }

    public Frame(Sprite sprite, float frameTime) {
        this.sprite = sprite;
        this.frameTime = frameTime;
    }
}
