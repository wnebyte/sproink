package com.github.wnebyte.sproink;

import com.github.wnebyte.sproink.core.scene.SceneInitializer;

public class Configuration {

    protected String title;

    protected int width, height;

    protected SceneInitializer scene;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setScene(SceneInitializer scene) {
        this.scene = scene;
    }
}
