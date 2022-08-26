package com.github.wnebyte.editor.ui;

import java.io.File;
import imgui.ImGui;
import com.github.wnebyte.editor.project.Context;
import com.github.wnebyte.editor.observer.event.EditSceneEvent;
import com.github.wnebyte.editor.observer.event.DeleteSceneEvent;
import com.github.wnebyte.sproink.core.window.Window;
import com.github.wnebyte.sproink.observer.EventSystem;
import com.github.wnebyte.sproink.ui.ImGuiWindow;

public class SceneViewWindow extends ImGuiWindow {

    private static final String TAG = "SceneViewWindow";

    private static final String TITLE = "Scenes";

    private static final int WINDOW_FLAGS = 0;

    public SceneViewWindow() {
        this(true);
    }

    public SceneViewWindow(boolean visible) {
        this.visible.set(visible);
    }

    @Override
    public void imGui() {
        if (!isVisible()) return;

        Context context = Context.get();
        if (context == null) return;
        String path = context.getProject().getAssetsDir() + File.separator + "scenes";
        File root = new File(path);
        if (!root.exists()) return;

        ImGui.begin(TITLE, visible, WINDOW_FLAGS);
        for (File file : root.listFiles()) {
            if (ImGui.collapsingHeader(file.getName())) {
                if (ImGui.button("Edit")) {
                    EventSystem.notify(null, new EditSceneEvent(file.getAbsolutePath()));
                }
                ImGui.sameLine();
                if (ImGui.button("Delete")) {
                    String name = file.getName().split("[.](txt|json)")[0];
                    EventSystem.notify(null, new DeleteSceneEvent(name));
                }
            }
        }
        if (ImGui.beginPopupContextWindow("SceneAdder")) {
            if (ImGui.menuItem("New Scene")) {
                Window.getImGuiLayer().getWindow(NewSceneWindow.class).show();
            }
            ImGui.endPopup();
        }
        ImGui.end();
    }
}
