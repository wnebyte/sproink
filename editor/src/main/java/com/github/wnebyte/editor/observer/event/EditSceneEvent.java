package com.github.wnebyte.editor.observer.event;

import com.github.wnebyte.sproink.observer.event.Event;

public class EditSceneEvent extends Event {

    private final String path;

    public EditSceneEvent(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
