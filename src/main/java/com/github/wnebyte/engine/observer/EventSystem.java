package com.github.wnebyte.engine.observer;

import java.util.List;
import java.util.ArrayList;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.observer.event.Event;

public class EventSystem {

    public static EventSystem getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EventSystem();
        }
        return INSTANCE;
    }

    private static EventSystem INSTANCE = null;

    private final List<Observer> observers;

    private EventSystem() {
        this.observers = new ArrayList<>();
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void notify(GameObject go, Event event) {
        for (Observer observer : observers) {
            observer.notify(go, event);
        }
    }
}
