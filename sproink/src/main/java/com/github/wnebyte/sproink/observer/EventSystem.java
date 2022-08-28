package com.github.wnebyte.sproink.observer;

import java.util.List;
import java.util.ArrayList;
import com.github.wnebyte.sproink.observer.event.Event;
import com.github.wnebyte.sproink.core.GameObject;

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
