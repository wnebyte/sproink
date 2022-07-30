package com.github.wnebyte.engine.core.scene;

public interface SceneInitializer {

    void init(Scene scene);

    void loadResources(Scene scene);

    void imGui();
}
