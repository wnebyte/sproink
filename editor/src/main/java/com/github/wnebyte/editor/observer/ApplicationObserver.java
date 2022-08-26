package com.github.wnebyte.editor.observer;

import java.util.List;
import java.util.Collections;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;

import com.github.wnebyte.editor.observer.event.*;
import com.github.wnebyte.editor.util.Logger;
import com.github.wnebyte.editor.project.Context;
import com.github.wnebyte.editor.scenes.LevelEditorSceneInitializer;
import com.github.wnebyte.editor.util.ObjectFlyWeight;
import com.github.wnebyte.editor.util.Settings;
import com.github.wnebyte.sproink.core.scene.Scene;
import com.github.wnebyte.sproink.core.scene.SceneInitializer;
import com.github.wnebyte.sproink.ui.GameViewWindow;
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
        } else if (event instanceof SaveSceneEvent) {
            handleSaveSceneEvent();
        } else if (event instanceof NewProjectEvent) {
            NewProjectEvent e = (NewProjectEvent) event;
            handleNewProjectEvent(e);
        } else if (event instanceof OpenProjectEvent) {
            OpenProjectEvent e = (OpenProjectEvent) event;
            handleOpenProjectEvent(e);
        } else if (event instanceof EditSceneEvent) {
            EditSceneEvent e = (EditSceneEvent) event;
            handleEditSceneEvent(e);
        } else if (event instanceof DeleteSceneEvent) {
            DeleteSceneEvent e = (DeleteSceneEvent) event;
            handleDeleteSceneEvent(e);
        } else if (event instanceof NewSceneEvent) {
            NewSceneEvent e = (NewSceneEvent) event;
            handleNewSceneEvent(e);
        } else if (event instanceof WindowBeginLoopEvent) {
            handleWindowBeginLoopEvent();
        }
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

    private void handleGameEngineStartPlayEvent(GameEngineStartPlayEvent ignoredEvent) {
        Context context = Context.get();
        if (context == null) return;

        Scene scene = Window.getScene();
        scene.save(Settings.GSON);
        Path path = new UriBuilder()
                .setAuthority(context.getProject().getAssetsDir())
                .appendPath("sceneinitializers")
                .appendPath(scene.getName().split("[.]json")[0] + ".txt")
                .toPath();
        String canonicalName = read(path);
        Class<? extends SceneInitializer> cls = context.getSceneInitializer(canonicalName);

        Window.setRuntimePlaying(true);
        Window.setScene(scene.getPath(), ObjectFlyWeight.getSceneInitializer(cls), Settings.GSON);
        for (ImGuiWindow window : Window.getImGuiLayer().getAllWindows()) {
            if (window instanceof GameViewWindow) {
                window.show();
            } else {
                window.hide();
            }
        }
        Logger.log(TAG, "Start Play");
    }

    private void handleGameEngineStopPlayEvent(GameEngineStopPlayEvent ignoredEvent) {
        Context context = Context.get();
        if (context == null) return;

        Window.setRuntimePlaying(false);
        Window.setScene(Window.getScene().getPath(), new LevelEditorSceneInitializer(), Settings.GSON);
        for (ImGuiWindow window : Window.getImGuiLayer().getAllWindows()) {
            if (window.isModal()) {
                window.hide();
            } else {
                window.show();
            }
        }
        Logger.log(TAG, "Stop Play");
    }

    private void handleSaveSceneEvent() {
        Scene scene = Window.getScene();
        scene.save(Settings.GSON);
        Logger.log(TAG, "Scene: '" + scene.getName() + "' was saved");
    }

    private void handleEditSceneEvent(EditSceneEvent event) {
        Context context = Context.get();
        if (context != null) {
            handleSaveSceneEvent();
            String path = event.getPath();
            write(SCENE_PATH, path);
            Window.setScene(path, new LevelEditorSceneInitializer(), Settings.GSON);
            Logger.log(TAG, "Scene: '" + Window.getScene().getName() + "' edit");
        }
    }

    private void handleDeleteSceneEvent(DeleteSceneEvent event) {
        Context context = Context.get();
        if (context != null) {
            File scene = new UriBuilder()
                    .setAuthority(context.getProject().getAssetsDir())
                    .appendPath("scenes")
                    .appendPath(event.getSceneName() + ".json")
                    .toFile();
            File init = new UriBuilder()
                    .setAuthority(context.getProject().getAssetsDir())
                    .appendPath("sceneinitializers")
                    .appendPath(event.getSceneName() + ".txt")
                    .toFile();
            boolean success = delete(scene);
            if (success) {
                success = delete(init);
                if (success) {
                    Logger.log(TAG, "Scene: '" + event.getSceneName() + "' deleted successfully");
                    return;
                }
            }
            Logger.log(TAG, "Scene: '" + event.getSceneName() + "' could not be deleted");
        }
    }

    private void handleNewSceneEvent(NewSceneEvent event) {
        Context context = Context.get();
        if (context != null) {
            File scene = new UriBuilder()
                    .setAuthority(context.getProject().getAssetsDir())
                    .appendPath("scenes")
                    .appendPath(event.getSceneName() + ".json")
                    .toFile();
            File init = new UriBuilder()
                    .setAuthority(context.getProject().getAssetsDir())
                    .appendPath("sceneinitializers")
                    .appendPath(event.getSceneName() + ".txt")
                    .toFile();
            if (!scene.exists() && !init.exists()) {
                boolean success = create(scene);
                if (success) {
                    success = write(init.toPath(), event.getSceneInitializer().getCanonicalName());
                    if (success) {
                        Logger.log(TAG, "Scene: '" + event.getSceneName() + "' successfully created");
                    } else {
                        delete(scene.toPath());
                    }
                }
            }
        }
    }

    private void handleWindowBeginLoopEvent() {
        if (PROJECT_DIR_PATH.toFile().exists()) {
            String path = read(PROJECT_DIR_PATH);
            handleOpenProjectEvent(new OpenProjectEvent(path));
        }
        if (SCENE_PATH.toFile().exists()) {
            String path = read(SCENE_PATH);
            handleEditSceneEvent(new EditSceneEvent(path));
        }
    }

    private boolean write(Path path, String text) {
        try {
            Files.write(path, Collections.singleton(text), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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

    private boolean delete(Path path) {
        try {
            Files.delete(path);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean delete(File file) {
        try {
            return file.delete();
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean create(File file) {
        try {
            return file.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

    private void log() {
        Context context = Context.get();
        if (context != null) {
            String path = context.getProject().getPath();
            String outDir = context.getProject().getOutDir();
            String assetsDir = context.getProject().getAssetsDir();
            Logger.log(TAG, "path: " + path);
            Logger.log(TAG, "outDir: " + outDir);
            Logger.log(TAG, "assetsDir: " + assetsDir);
        }
    }
}
