package com.github.wnebyte.sproink;

import java.util.List;
import com.github.wnebyte.sproink.ui.ImGuiWindow;
import com.github.wnebyte.sproink.ui.GameViewWindow;
import com.github.wnebyte.sproink.scenes.LevelSceneInitializer;

public class Main extends Application {

    public static void main(String[] args) {
        launch(new Main());
    }

    @Override
    public void configure(Configuration conf) {
        conf.setTitle("Engine");
        conf.setScene(new LevelSceneInitializer());
    }

    @Override
    public void addWindows(List<ImGuiWindow> windows) {
        windows.add(new GameViewWindow());
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
