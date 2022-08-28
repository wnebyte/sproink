package com.github.wnebyte.sproink.observer;

import com.github.wnebyte.sproink.observer.event.Event;
import com.github.wnebyte.sproink.core.GameObject;

public interface Observer {

    void notify(GameObject go, Event event);
}
