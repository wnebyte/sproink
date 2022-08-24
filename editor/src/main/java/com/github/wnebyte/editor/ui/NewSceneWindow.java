package com.github.wnebyte.editor.ui;

import java.io.File;
import java.io.IOException;
import imgui.ImGui;
import imgui.type.ImString;
import com.github.wnebyte.editor.log.Logger;
import com.github.wnebyte.editor.project.Context;
import com.github.wnebyte.sproink.ui.ImGuiWindow;

public class NewSceneWindow extends ImGuiWindow {

    private static final String TAG = "NewSceneWindow";

    private static final int WINDOW_FLAGS = 0;

    private final ImString name;

    public NewSceneWindow() {
        this(true);
    }

    public NewSceneWindow(boolean visible) {
        this.visible.set(visible);
        this.name = new ImString();
    }

    @Override
    public void imGui() {
        if (!isVisible()) return;

        Context context = Context.get();
        if (context == null) return;

        ImGui.begin("New Scene Window", visible, WINDOW_FLAGS);
        ImGui.text("Name:");
        ImGui.sameLine();
        ImGui.inputText("##sceneName", name);
        ImGui.text("Initializer:");
        ImGui.sameLine();
        ImGui.inputText("##initializer", new ImString());
        if (ImGui.button("Create")) {
            File file = new File(
                    context.getProject().getAssetsDir() + File.separator + "scenes" + File.separator + name + ".json");
            if (!file.exists()) {
                try {
                    boolean success = file.createNewFile();
                    if (success) {
                        name.set("");
                        Logger.log(TAG, "Scene: '" + file.getName() + "' created successfully");
                        hide();
                    } else {
                        Logger.log(TAG, "Scene: '" + file.getName() + "' could not be created");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        ImGui.end();
    }

    @Override
    public boolean isModal() {
        return true;
    }
}
