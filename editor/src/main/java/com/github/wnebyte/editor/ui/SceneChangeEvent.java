package com.github.wnebyte.editor.ui;

import com.github.wnebyte.sproink.observer.event.Event;

public class SceneChangeEvent extends Event {

    private final String path;

    public SceneChangeEvent(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
