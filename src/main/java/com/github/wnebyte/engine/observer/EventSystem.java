package com.github.wnebyte.engine.observer;

import java.util.List;
import java.util.ArrayList;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.observer.event.Event;

public class EventSystem {

    private static final List<Observer> observers = new ArrayList<>();

    public static void addObserver(Observer observer) {
        observers.add(observer);
    }

    public static void notify(GameObject go, Event event) {
        for (Observer observer : observers) {
            observer.notify(go, event);
        }
    }
}
