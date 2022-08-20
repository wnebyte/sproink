package com.github.wnebyte.editor.observer;

import java.util.List;
import java.util.Collections;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import com.github.wnebyte.editor.observer.event.NewProjectEvent;
import com.github.wnebyte.editor.observer.event.OpenProjectEvent;
import com.github.wnebyte.editor.observer.event.SaveLevelEvent;
import com.github.wnebyte.sproink.observer.event.WindowInitializedEvent;
import com.github.wnebyte.editor.project.ProjectContext;
import com.github.wnebyte.editor.scene.LevelEditorSceneInitializer;
import com.github.wnebyte.sproink.core.scene.LevelSceneInitializer;
import com.github.wnebyte.sproink.observer.event.GameEngineStartPlayEvent;
import com.github.wnebyte.sproink.observer.event.GameEngineStopPlayEvent;
import com.github.wnebyte.sproink.observer.Observer;
import com.github.wnebyte.sproink.observer.event.*;
import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.core.ui.ImGuiWindow;
import com.github.wnebyte.sproink.core.window.Window;

public class WindowObserver implements Observer {

    private static final Path PATH
            = Paths.get(System.getProperty("java.io.tmpdir") + File.separator + "sproink_editor_tmp_sym_link");

    @Override
    public void notify(GameObject go, Event event) {
        if (event instanceof GameEngineStartPlayEvent) {
            Window.setRuntimePlaying(true);
            Window.getScene().save();
            Window.setScene(new LevelSceneInitializer());
            System.out.println("(Debug): Start Play");
        } else if (event instanceof GameEngineStopPlayEvent) {
            Window.setRuntimePlaying(false);
            Window.setScene(new LevelEditorSceneInitializer());
            Window.getImGuiLayer().getAllWindows().forEach(ImGuiWindow::show);
            System.out.println("(Debug): Stop Play");
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
        } else if (event instanceof WindowInitializedEvent) {
            WindowInitializedEvent e = (WindowInitializedEvent) event;
            handleWindowInitializedEvent(e);
        } else if (event instanceof WindowCloseEvent) {
            Window window = Window.get();
            window.destroy();
        }
    }

    private void handleNewProjectEvent(NewProjectEvent event) {
        ProjectContext context = ProjectContext.newInstance(event.getName(), event.getPath());
        Window.setTitle(context.getProject().getName());
        writeSymLink(context.getProject().getPath());
    }

    private void handleOpenProjectEvent(OpenProjectEvent event) {
        ProjectContext context = ProjectContext.open(event.getPath());
        Window.setTitle(context.getProject().getName());
        writeSymLink(context.getProject().getPath());
    }

    private void handleWindowInitializedEvent(WindowInitializedEvent event) {
        if (PATH.toFile().exists()) {
            String path = readSymLink();
            handleOpenProjectEvent(new OpenProjectEvent(path));
        }
    }

    private void writeSymLink(String link) {
        try {
            Files.write(PATH, Collections.singleton(link), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readSymLink() {
        try {
            List<String> lines = Files.readAllLines(PATH, StandardCharsets.UTF_8);
            String link = String.join(System.lineSeparator(), lines);
            return link;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
