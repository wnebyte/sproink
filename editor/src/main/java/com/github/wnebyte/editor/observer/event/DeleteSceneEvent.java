package com.github.wnebyte.editor.observer.event;

import com.github.wnebyte.sproink.observer.event.Event;

public class DeleteSceneEvent extends Event {

    private final String sceneName;

    public DeleteSceneEvent(String sceneName) {
        this.sceneName = sceneName;
    }

    public String getSceneName() {
        return sceneName;
    }
}
