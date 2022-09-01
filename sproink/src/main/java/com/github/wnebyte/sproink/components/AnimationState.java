package com.github.wnebyte.sproink.components;

import java.util.List;
import java.util.ArrayList;

import com.github.wnebyte.sproink.util.Assets;

public class AnimationState {

    private static final Sprite DEFAULT_SPRITE = new Sprite();

    private String title;

    private boolean doesLoop = false;

    private final List<Frame> frames = new ArrayList<>();

    private transient float timeTracker = 0.0f;

    private transient int currentSprite = 0;

    public void update(float dt) {
        if (currentSprite < frames.size()) {
            timeTracker -= dt;
            if (timeTracker <= 0.0) {
                if (!(currentSprite == frames.size() - 1 && !doesLoop)) {
                    currentSprite = (currentSprite + 1) % frames.size();
                }
                timeTracker = frames.get(currentSprite).getFrameTime();
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addFrame(Sprite sprite, float frameTime) {
        frames.add(new Frame(sprite, frameTime));
    }

    public List<Frame> getFrames() {
        return frames;
    }

    public void setLoops(boolean value) {
        doesLoop = value;
    }

    public boolean doesLoop() {
        return doesLoop;
    }

    public Sprite getCurrentSprite() {
        if (currentSprite < frames.size()) {
            return frames.get(currentSprite).getSprite();
        }

        return DEFAULT_SPRITE;
    }

    public void refresh() {
        for (Frame frame : frames) {
            frame.getSprite().setTexture(Assets.getTexture(frame.getSprite().getTexture().getPath()));
        }
    }
}
