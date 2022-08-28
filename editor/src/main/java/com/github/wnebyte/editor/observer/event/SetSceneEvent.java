package com.github.wnebyte.editor.observer.event;

import com.github.wnebyte.sproink.observer.event.Event;

public class SetSceneEvent extends Event {

    private final String path;

    public SetSceneEvent(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
