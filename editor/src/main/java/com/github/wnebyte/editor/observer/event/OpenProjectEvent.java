package com.github.wnebyte.editor.observer.event;

import com.github.wnebyte.sproink.observer.event.Event;

public class OpenProjectEvent extends Event {

    private final String path;

    public OpenProjectEvent(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
