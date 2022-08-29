import java.util.List;
import com.github.wnebyte.sproink.Application;
import com.github.wnebyte.sproink.Configuration;
import com.github.wnebyte.sproink.observer.Observer;
import com.github.wnebyte.sproink.scenes.LevelSceneInitializer;
import com.github.wnebyte.sproink.ui.FontConfig;
import com.github.wnebyte.sproink.ui.GameViewWindow;
import com.github.wnebyte.sproink.ui.ImGuiWindow;
import com.github.wnebyte.util.FsLogger;

public class Main extends Application {

    public static void main(String[] args) {
        launch(new Main());
    }

    @Override
    public void configure(final Configuration conf) {
        conf.setTitle("MyGame");
        conf.setEnableDocking(false);
        conf.setScene("myScene.json");
        conf.setSceneInitializer(new LevelSceneInitializer());
        conf.setIniFileName("../imgui.ini");
        conf.setLogger(new FsLogger("../logs"));
    }

    @Override
    public void addFonts(final List<FontConfig> fonts) {
        fonts.add(new FontConfig("../fonts/segoeui.ttf", 18));
    }

    @Override
    public void addWindows(final List<ImGuiWindow> windows) {
        windows.add(new GameViewWindow())
    }

    @Override
    public void addObservers(final List<Observer> observers) {

    }
}