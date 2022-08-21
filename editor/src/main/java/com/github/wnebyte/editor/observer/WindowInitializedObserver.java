package com.github.wnebyte.editor.observer;

import com.github.wnebyte.editor.ui.*;
import com.github.wnebyte.editor.scene.LevelEditorSceneInitializer;
import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.core.ui.GameViewWindow;
import com.github.wnebyte.sproink.core.window.Window;
import com.github.wnebyte.sproink.observer.Observer;
import com.github.wnebyte.sproink.observer.event.Event;
import com.github.wnebyte.sproink.observer.event.WindowInitializedEvent;

public class WindowInitializedObserver implements Observer {

    @Override
    public void notify(GameObject go, Event event) {
        if (event instanceof WindowInitializedEvent) {
            Window.getImGuiLayer().addWindow(new GameViewWindow());
            Window.getImGuiLayer().addWindow(new AssetsWindow());
            Window.getImGuiLayer().addWindow(new PropertiesWindow(Window.getPickingTexture()));
            Window.getImGuiLayer().addWindow(new SceneHierarchyWindow());
            Window.getImGuiLayer().addWindow(new ConsoleWindow());
            Window.getImGuiLayer().addWindow(new LogWindow());
            Window.getImGuiLayer().addWindow(new NewProjectWindow(false));
            Window.getImGuiLayer().addWindow(new OpenProjectWindow(false));
            Window.getImGuiLayer().addWindow(new MenuBar());
            Window.setScene(new LevelEditorSceneInitializer());
        }
    }
}
