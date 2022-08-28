package com.github.wnebyte.sproink;

import java.util.List;
import java.util.ArrayList;
import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.ui.ImGuiLayer;
import com.github.wnebyte.sproink.ui.ImGuiWindow;
import com.github.wnebyte.sproink.core.window.Window;
import com.github.wnebyte.sproink.observer.EventSystem;
import com.github.wnebyte.sproink.observer.Observer;
import com.github.wnebyte.sproink.observer.event.Event;
import com.github.wnebyte.sproink.observer.event.WindowInitEvent;

public class Application implements Observer {

    public static void launch(Application app) {
        app.conf = new Configuration();
        app.configure(app.conf);
        List<Observer> observers = new ArrayList<>();
        observers.add(app);
        app.addObservers(observers);
        observers.forEach(EventSystem::addObserver);
        Window window = Window.get();
        window.run();
    }

    private Configuration conf;

    protected void configure(Configuration conf) {}

    protected void addWindows(List<ImGuiWindow> windows) {}

    protected void addObservers(List<Observer> observers) {}

    @Override
    public void notify(GameObject go, Event event) {
        if (event instanceof WindowInitEvent) {
            ImGuiLayer imGuiLayer = Window.getImGuiLayer();
            List<ImGuiWindow> windows = new ArrayList<>();
            addWindows(windows);
            windows.forEach(imGuiLayer::addWindow);
            Window.setScene(conf.scene, conf.sceneInitializer);
        }
    }
}
