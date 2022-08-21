package com.github.wnebyte.editor.ui;

import java.util.Map;
import java.util.Arrays;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;
import com.github.wnebyte.editor.observer.event.OpenProjectEvent;
import com.github.wnebyte.sproink.observer.EventSystem;
import com.github.wnebyte.sproink.core.ui.ImGuiWindow;

public class OpenProjectWindow extends ImGuiWindow {

    private static final int IMGUI_FLAGS = ImGuiFileDialogFlags.DisableCreateDirectoryButton |
            ImGuiFileDialogFlags.DontShowHiddenFiles |
            ImGuiFileDialogFlags.HideColumnDate |
            ImGuiFileDialogFlags.HideColumnSize |
            ImGuiFileDialogFlags.HideColumnType;

    private Map<String, String> selection;

    public OpenProjectWindow() {
        this(false);
    }

    public OpenProjectWindow(boolean visible) {
        this.visible.set(visible);
    }

    @Override
    public void imGui() {
        if (!isVisible()) return;

        ImGui.setNextWindowSize(800, 200, ImGuiCond.Once);
        ImGui.setNextWindowPos(
                ImGui.getMainViewport().getPosX() + 100, ImGui.getMainViewport().getPosY() + 100, ImGuiCond.Once);
        if (ImGui.begin("Open Project Window", visible)) {
            ImGui.text("Location:");
            ImGui.sameLine(80f);

            if (ImGui.button("Browse Folder")) {
                ImGuiFileDialog.openDialog("browse-folder-key", "Choose Folder", null, ".", "",
                        1, 7, IMGUI_FLAGS);
            }

            if (ImGuiFileDialog.display("browse-folder-key", ImGuiFileDialogFlags.None, 200, 400, 800, 600)) {
                if (ImGuiFileDialog.isOk()) {
                    selection = ImGuiFileDialog.getSelection();
                }
                ImGuiFileDialog.close();
            }

            if (ImGui.button("Open")) {
                if (hasPath()) {
                    String path = getPath();
                    EventSystem.notify(null, new OpenProjectEvent(path));
                    hide();
                }
            }
        }

        if (hasPath()) {
            ImGui.text(getPath());
        }

        ImGui.end();
    }

    public boolean hasPath() {
        return (selection != null) && !(selection.isEmpty());
    }

    public String getPath() {
        String path = selection.values().stream().findFirst().orElse(null);
        if (path != null) {
            String[] array = path.split("\\\\");
            if (array.length >= 2) {
                String s = String.join("\\", Arrays.asList(array).subList(0, array.length - 1));
                return s;
            }
            return path;
        }
        return null;
    }
}
