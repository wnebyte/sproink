package com.github.wnebyte.editor.observer;

import java.io.File;
import java.nio.file.Path;
import com.github.wnebyte.editor.project.Context;
import com.github.wnebyte.editor.observer.event.*;
import com.github.wnebyte.sproink.util.Assets;
import com.github.wnebyte.sproink.util.Settings;
import com.github.wnebyte.util.Files;
import com.github.wnebyte.sproink.core.Window;
import com.github.wnebyte.sproink.core.Scene;
import com.github.wnebyte.sproink.core.GameObject;
import com.github.wnebyte.sproink.ui.ImGuiWindow;
import com.github.wnebyte.sproink.ui.GameViewWindow;
import com.github.wnebyte.sproink.observer.Observer;
import com.github.wnebyte.sproink.observer.event.*;
import com.github.wnebyte.sproink.util.Log;
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
        } else if (event instanceof SetSceneEvent) {
            SetSceneEvent e = (SetSceneEvent) event;
            handleSetSceneEvent(e);
        } else if (event instanceof DeleteSceneEvent) {
            DeleteSceneEvent e = (DeleteSceneEvent) event;
            handleDeleteSceneEvent(e);
        } else if (event instanceof NewSceneEvent) {
            NewSceneEvent e = (NewSceneEvent) event;
            handleNewSceneEvent(e);
        } else if (event instanceof WindowInitEvent) {
            Settings.GSON = com.github.wnebyte.editor.util.Settings.GSON;
           // Assets.getFont("C:/Users/ralle/dev/java/Engine/editor/build/install/editor/assets/fonts/super-mario.ttf");
        } else if (event instanceof WindowBeginLoopEvent) {
            handleWindowBeginLoopEvent();
        } else if (event instanceof CompileEvent) {
            Context context = Context.get();
            if (context != null) {
                context.compile();
            }
        }
    }

    private void handleNewProjectEvent(NewProjectEvent event) {
        Context context = Context.newProject(event.getName(), event.getPath());
        String name = context.getProject().getName();
        Window.setTitle(name);
        Files.write(PROJECT_DIR_PATH, context.getProject().getProjectDir());
        Files.delete(SCENE_PATH);
    }

    private void handleOpenProjectEvent(OpenProjectEvent event) {
        Context context = Context.openProject(event.getPath());
        String name = context.getProject().getName();
        for (ImGuiWindow window : Window.getImGuiLayer().getAllWindows()) {
            if (!window.isModal()) {
                window.show();
            }
        }
        Window.setTitle(name);
        Files.write(PROJECT_DIR_PATH, context.getProject().getProjectDir());
    }

    private void handleGameEngineStartPlayEvent(GameEngineStartPlayEvent ignoredEvent) {
        Context context = Context.get();
        if (context == null) return;

        Scene scene = Window.getScene();
        scene.save();
        Window.setRuntimePlaying(true);
        Window.setScene(scene.getPath(), context.newSceneInitializer());
        for (ImGuiWindow window : Window.getImGuiLayer().getAllWindows()) {
            if (window instanceof GameViewWindow) {
                window.show();
            } else {
                window.hide();
            }
        }
       // Log.i(TAG, "Start Play");
    }

    private void handleGameEngineStopPlayEvent(GameEngineStopPlayEvent ignoredEvent) {
        Context context = Context.get();
        if (context == null) return;

        Scene scene = Window.getScene();
        Window.setRuntimePlaying(false);
        Window.setScene(scene.getPath(), context.newEditorSceneInitializer());
        for (ImGuiWindow window : Window.getImGuiLayer().getAllWindows()) {
            if (window.isModal()) {
                window.hide();
            } else {
                window.show();
            }
        }

       // Log.i(TAG, "Stop Play");
    }

    /**
     * Saves the current scene.
     */
    private void handleSaveSceneEvent() {
        Context context = Context.get();
        if (context != null) {
            Window.getScene().save();
        }
    }

    /**
     * Deletes the specified scene.
     * @param event an event.
     */
    private void handleDeleteSceneEvent(DeleteSceneEvent event) {
        Context context = Context.get();
        if (context != null) {
            File file = new UriBuilder()
                    .setAuthority(context.getProject().getAssetsDir())
                    .appendPath("scenes")
                    .appendPath(event.getSceneName() + ".json")
                    .toFile();
            boolean success = Files.delete(file);
            if (success) {
                if (Window.getScene().getPath().equals(file.getAbsolutePath())) {
                    Window.setScene("non-serial-scene.json", context.newEditorSceneInitializer());
                    Files.delete(SCENE_PATH);
                }
            }
        }
    }

    /**
     * Sets the current scene.
     * @param event an event.
     */
    private void handleSetSceneEvent(SetSceneEvent event) {
        Context context = Context.get();
        if (context != null) {
            Scene scene = Window.getScene();
            scene.save();
            File file = new File(event.getPath());
            if (file.exists()) {
                Window.setScene(event.getPath(), context.newEditorSceneInitializer());
                Files.write(SCENE_PATH, event.getPath());
            }
        }
    }

    /**
     * Creates a new scene.
     * @param event an event.
     */
    private void handleNewSceneEvent(NewSceneEvent event) {
        Context context = Context.get();
        if (context != null) {
            File file = new UriBuilder()
                    .setAuthority(context.getProject().getAssetsDir())
                    .appendPath("scenes")
                    .appendPath(event.getSceneName() + ".json")
                    .toFile();
            if (!file.exists()) {
                boolean success = Files.create(file);
                if (success) {
                    Log.i(TAG, "Scene: '%s' was successfully created", event.getSceneName());
                } else {
                    Log.e(TAG, "Scene: '%s' could not be created", event.getSceneName());
                }
            }
        }
    }

    private void handleWindowBeginLoopEvent() {
        if (PROJECT_DIR_PATH.toFile().exists()) {
            String path = Files.read(PROJECT_DIR_PATH);
            handleOpenProjectEvent(new OpenProjectEvent(path));

            if (SCENE_PATH.toFile().exists()) {
                path = Files.read(SCENE_PATH);
                File file = new File(path);
                if (file.exists()) {
                    handleSetSceneEvent(new SetSceneEvent(file.getAbsolutePath()));
                } else {
                    Files.delete(SCENE_PATH);
                }
            }
        }
    }

    private void log() {
        Context context = Context.get();
        if (context != null) {
            String projectDir = context.getProject().getProjectDir();
            String outDir = context.getProject().getOutDir();
            String assetsDir = context.getProject().getAssetsDir();
            Log.i(TAG, "projectDir: " + projectDir);
            Log.i(TAG, "outDir: " + outDir);
            Log.i(TAG, "assetsDir: " + assetsDir);
        }
    }
}
