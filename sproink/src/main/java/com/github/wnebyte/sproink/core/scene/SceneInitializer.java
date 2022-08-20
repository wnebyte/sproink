package com.github.wnebyte.sproink.core.scene;

public interface SceneInitializer {

    void init(Scene scene);

    void loadResources(Scene scene);

    void imGui();
}
