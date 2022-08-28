package com.github.wnebyte.sproink;

import com.github.wnebyte.sproink.core.SceneInitializer;

public class Configuration {

    protected String title;

    protected int width, height;

    protected String scene;

    protected SceneInitializer sceneInitializer;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public void setSceneInitializer(SceneInitializer scene) {
        this.sceneInitializer = scene;
    }
}
