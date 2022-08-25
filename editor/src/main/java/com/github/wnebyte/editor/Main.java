package com.github.wnebyte.editor;

import java.util.List;
import com.github.wnebyte.editor.ui.*;
import com.github.wnebyte.editor.observer.ApplicationObserver;
import com.github.wnebyte.editor.scenes.LevelEditorSceneInitializer;
import com.github.wnebyte.sproink.Application;
import com.github.wnebyte.sproink.Configuration;
import com.github.wnebyte.sproink.core.window.Window;
import com.github.wnebyte.sproink.observer.Observer;
import com.github.wnebyte.sproink.ui.GameViewWindow;
import com.github.wnebyte.sproink.ui.ImGuiWindow;

public class Main extends Application {

    private static final String TAG = "Main";

    public static void main(String[] args) {
        launch(new Main());
    }

    @Override
    public void configure(final Configuration conf) {
        conf.setTitle("Editor");
        conf.setScene(new LevelEditorSceneInitializer());
    }

    @Override
    public void addWindows(final List<ImGuiWindow> windows) {
        windows.add(new GameViewWindow(false));
        windows.add(new AssetsWindow(false));
        windows.add(new PropertiesWindow(false, Window.getPickingTexture()));
        windows.add(new SceneHierarchyWindow(false));
        windows.add(new SceneViewWindow(false));
        windows.add(new ConsoleWindow(false));
        windows.add(new LogWindow(false));
        windows.add(new DirectoryViewWindow(true));
        windows.add(new NewProjectWindow(false));
        windows.add(new OpenProjectWindow(false));
        windows.add(new NewSceneWindow(false));
        windows.add(new MenuBar());
    }

    @Override
    public void addObservers(final List<Observer> observers) {
        observers.add(new ApplicationObserver());
    }

    /*
    TEXTURE COORDINATES:
    0,1     1,1
    +---------+
    |         |
    |         |
    +---------+
    0,0     1,0
     */
}
