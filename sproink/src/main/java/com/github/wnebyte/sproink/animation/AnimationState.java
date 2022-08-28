package com.github.wnebyte.sproink.animation;

import java.util.List;
import java.util.ArrayList;
import com.github.wnebyte.sproink.components.Sprite;
import com.github.wnebyte.sproink.util.Assets;

public class AnimationState {

    private static final Sprite DEFAULT_SPRITE = new Sprite();

    public String title;

    public boolean doesLoop = false;

    public List<Frame> frames = new ArrayList<>();

    private transient float timeTracker = 0.0f;

    private transient int currentSprite = 0;

    public void update(float dt) {
        if (currentSprite < frames.size()) {
            timeTracker -= dt;
            if (timeTracker <= 0.0) {
                if (!(currentSprite == frames.size() - 1 && !doesLoop)) {
                    currentSprite = (currentSprite + 1) % frames.size();
                }
                timeTracker = frames.get(currentSprite).frameTime;
            }
        }
    }

    public void addFrame(Sprite sprite, float frameTime) {
        frames.add(new Frame(sprite, frameTime));
    }

    public void setLoop(boolean value) {
        doesLoop = value;
    }

    public Sprite getCurrentSprite() {
        if (currentSprite < frames.size()) {
            return frames.get(currentSprite).sprite;
        }

        return DEFAULT_SPRITE;
    }

    public void refresh() {
        for (Frame frame : frames) {
            frame.sprite.setTexture(Assets.getTexture(frame.sprite.getTexture().getPath()));
        }
    }
}
