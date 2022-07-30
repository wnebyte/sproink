package com.github.wnebyte.engine.observer;

import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.observer.event.Event;

public interface Observer {

    void notify(GameObject go, Event event);
}
