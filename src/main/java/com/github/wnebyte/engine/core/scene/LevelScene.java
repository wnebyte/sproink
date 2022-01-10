package com.github.wnebyte.engine.core.scene;

import com.github.wnebyte.engine.core.window.Window;

public class LevelScene extends Scene {

    public LevelScene() {
        System.out.println("Inside LevelScene.");
        Window.get().r = 1;
        Window.get().g = 1;
        Window.get().b = 1;
    }

    @Override
    public void update(float dt) {

    }
}
