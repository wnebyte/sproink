package com.github.wnebyte.engine.observer.event;

public class NewProjectEvent extends Event {

    private final String name;

    private final String location;

    public NewProjectEvent(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }
}
