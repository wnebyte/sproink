package com.github.wnebyte.sproink;

import java.util.List;
import java.util.ArrayList;
import com.github.wnebyte.sproink.core.Window;
import com.github.wnebyte.sproink.core.WindowArgs;
import com.github.wnebyte.sproink.observer.EventSystem;
import com.github.wnebyte.sproink.observer.Observer;
import com.github.wnebyte.sproink.ui.ImGuiWindow;
import com.github.wnebyte.sproink.ui.FontConfig;
import com.github.wnebyte.sproink.util.Log;

public class Application {

    public static void launch(Application app) {
        // conf
        Configuration conf = new Configuration();
        app.configure(conf);
        // observers
        List<Observer> observers = new ArrayList<>();
        app.addObservers(observers);
        observers.forEach(EventSystem::addObserver);
        // windows
        List<ImGuiWindow> windows = new ArrayList<>();
        app.addWindows(windows);
        // fonts
        List<FontConfig> fonts = new ArrayList<>();
        app.addFonts(fonts);
        // logger
        Log.setLogger(conf.logger);
        // window
        WindowArgs args = new WindowArgs.Builder()
                .setTitle(conf.title)
                .setEnableDocking(conf.docking)
                .setIniFileName(conf.iniFileName)
                .setScene(conf.scene)
                .setSceneInitializer(conf.sceneInitializer)
                .setWindows(windows)
                .setFonts(fonts)
                .build();
        Window.initialize(args);
        Window window = Window.get();
        window.run();
    }

    protected void configure(Configuration conf) {}

    protected void addWindows(List<ImGuiWindow> windows) {}

    protected void addObservers(List<Observer> observers) {}

    protected void addFonts(List<FontConfig> fonts) {}
}
