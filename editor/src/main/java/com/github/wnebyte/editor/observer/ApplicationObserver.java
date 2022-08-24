package com.github.wnebyte.editor.observer;

import java.util.List;
import java.util.Collections;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import com.github.wnebyte.editor.observer.event.NewProjectEvent;
import com.github.wnebyte.editor.observer.event.OpenProjectEvent;
import com.github.wnebyte.editor.observer.event.SaveLevelEvent;
import com.github.wnebyte.editor.ui.LogWindow;
import com.github.wnebyte.editor.project.Context;
import com.github.wnebyte.editor.scenes.LevelEditorSceneInitializer;
import com.github.wnebyte.editor.ui.SceneChangeEvent;
import com.github.wnebyte.sproink.ui.GameViewWindow;
import com.github.wnebyte.sproink.scenes.LevelSceneInitializer;
import com.github.wnebyte.sproink.observer.event.GameEngineStartPlayEvent;
import com.github.wnebyte.sproink.observer.event.GameEngineStopPlayEvent;
import com.github.wnebyte.sproink.observer.Observer;
import com.github.wnebyte.sproink.observer.event.*;
import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.ui.ImGuiWindow;
import com.github.wnebyte.sproink.core.window.Window;
import com.github.wnebyte.util.UriBuilder;

public class ApplicationObserver implements Observer {

    private static final String TAG = "ApplicationObserver";

    private static final Path PROJECT_DIR_PATH = new UriBuilder()
            .setAuthority(System.getProperty("java.io.tmpdir"))
            .appendPath("sproink_editor_project_dir")
            .toPath();

    private static final Path SCENE_PATH = new UriBuilder()
            .setAuthority(System.getProperty("java.io.tmpdir"))
            .appendPath("sproink_editor_scene")
            .toPath();

    @Override
    public void notify(GameObject go, Event event) {
        if (event instanceof GameEngineStartPlayEvent) {
            GameEngineStartPlayEvent e = (GameEngineStartPlayEvent) event;
            handleGameEngineStartPlayEvent(e);
        } else if (event instanceof GameEngineStopPlayEvent) {
            GameEngineStopPlayEvent e = (GameEngineStopPlayEvent) event;
            handleGameEngineStopPlayEvent(e);
        } else if (event instanceof LoadLevelEvent) {
            Window.setScene(new LevelEditorSceneInitializer());
        } else if (event instanceof SaveLevelEvent) {
            Window.getScene().save();
        } else if (event instanceof NewProjectEvent) {
            NewProjectEvent e = (NewProjectEvent) event;
            handleNewProjectEvent(e);
        } else if (event instanceof OpenProjectEvent) {
            OpenProjectEvent e = (OpenProjectEvent) event;
            handleOpenProjectEvent(e);
        } else if (event instanceof SceneChangeEvent) {
            SceneChangeEvent e = (SceneChangeEvent) event;
            handleSceneChangeEvent(e);
        } else if (event instanceof WindowBeginLoopEvent) {
            handleWindowBeginLoopEvent();
        } else if (event instanceof WindowCloseEvent) {
            Window window = Window.get();
            window.destroy();
        }
    }

    private void handleGameEngineStartPlayEvent(GameEngineStartPlayEvent ignoredEvent) {
        Window.setRuntimePlaying(true);
        Window.getScene().save();
        Window.setScene(Window.getScene().getPath(), new LevelSceneInitializer());
        for (ImGuiWindow window : Window.getImGuiLayer().getAllWindows()) {
            if (window instanceof GameViewWindow) {
                window.show();
            } else {
                window.hide();
            }
        }
        LogWindow logWindow = Window.getImGuiLayer().getWindow(LogWindow.class);
        logWindow.log(TAG, "Start Play");
    }

    private void handleGameEngineStopPlayEvent(GameEngineStopPlayEvent ignoredEvent) {
        Window.setRuntimePlaying(false);
        Window.setScene(Window.getScene().getPath(), new LevelEditorSceneInitializer());
        for (ImGuiWindow window : Window.getImGuiLayer().getAllWindows()) {
            if (window.isModal()) {
                window.hide();
            } else {
                window.show();
            }
        }
        LogWindow logWindow = Window.getImGuiLayer().getWindow(LogWindow.class);
        logWindow.log(TAG, "Stop Play");
    }

    private void handleNewProjectEvent(NewProjectEvent event) {
        Context context = Context.newInstance(event.getName(), event.getPath());
        String name = context.getProject().getName();
        Window.setTitle(name);
        write(PROJECT_DIR_PATH, context.getProject().getPath());
        delete(SCENE_PATH);
    }

    private void handleOpenProjectEvent(OpenProjectEvent event) {
        Context context = Context.open(event.getPath());
        String name = context.getProject().getName();
        for (ImGuiWindow window : Window.getImGuiLayer().getAllWindows()) {
            if (!window.isModal()) {
                window.show();
            }
        }
        Window.setTitle(name);
        write(PROJECT_DIR_PATH, context.getProject().getPath());
    }

    private void handleSceneChangeEvent(SceneChangeEvent event) {
        Context context = Context.get();
        if (context != null) {
            String path = event.getPath();
            write(SCENE_PATH, path);
            Window.setScene(path, new LevelEditorSceneInitializer());
        }
    }

    private void handleWindowBeginLoopEvent() {
        if (PROJECT_DIR_PATH.toFile().exists()) {
            String path = read(PROJECT_DIR_PATH);
            handleOpenProjectEvent(new OpenProjectEvent(path));
        }
        if (SCENE_PATH.toFile().exists()) {
            String path = read(SCENE_PATH);
            handleSceneChangeEvent(new SceneChangeEvent(path));
        }
    }

    private void write(Path path, String text) {
        try {
            Files.write(path, Collections.singleton(text), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String read(Path path) {
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            String text = String.join(System.lineSeparator(), lines);
            return text;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void delete(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
