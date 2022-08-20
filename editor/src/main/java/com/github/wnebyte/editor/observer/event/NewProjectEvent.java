package com.github.wnebyte.editor.observer.event;

import com.github.wnebyte.sproink.observer.event.Event;

public class NewProjectEvent extends Event {

    private final String name, path;

    public NewProjectEvent(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
