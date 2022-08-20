package com.github.wnebyte.sproink.animation;

import com.github.wnebyte.sproink.components.Sprite;

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
