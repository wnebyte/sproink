package com.github.wnebyte.editor.observer.event;

import com.github.wnebyte.sproink.observer.event.Event;
import com.github.wnebyte.sproink.core.scene.SceneInitializer;

public class NewSceneEvent extends Event {

    private final String sceneName;

    private final Class<? extends SceneInitializer> sceneInitializer;

    public NewSceneEvent(String sceneName, Class<? extends SceneInitializer> sceneInitializer) {
        this.sceneName = sceneName;
        this.sceneInitializer = sceneInitializer;
    }

    public String getSceneName() {
        return sceneName;
    }

    public Class<? extends SceneInitializer> getSceneInitializer() {
        return sceneInitializer;
    }
}
