package com.github.wnebyte.editor.ui;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import imgui.ImGui;
import imgui.type.ImString;
import imgui.flag.ImGuiCond;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.callback.ImGuiFileDialogPaneFun;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;
import com.github.wnebyte.editor.observer.event.NewProjectEvent;
import com.github.wnebyte.sproink.observer.EventSystem;
import com.github.wnebyte.sproink.ui.ImGuiWindow;

public class NewProjectWindow extends ImGuiWindow {

    private static final int WINDOW_FLAGS =
            ImGuiFileDialogFlags.DisableCreateDirectoryButton |
            ImGuiFileDialogFlags.DontShowHiddenFiles |
            ImGuiFileDialogFlags.HideColumnDate |
            ImGuiFileDialogFlags.HideColumnSize |
            ImGuiFileDialogFlags.HideColumnType;

    private Map<String, String> selection = new HashMap<>();

    private long userData = 0;

    private ImString name = new ImString();

    private ImGuiFileDialogPaneFun callback = new ImGuiFileDialogPaneFun() {
        @Override
        public void paneFun(String filter, long userData, boolean canCont) {
            ImGui.text("Filter: " + filter);
        }
    };

    public NewProjectWindow() {
        this(true);
    }

    public NewProjectWindow(boolean visible) {
        this.visible.set(visible);
    }

    @Override
    public void imGui() {
        if (!isVisible()) return;

        ImGui.setNextWindowSize(800, 200, ImGuiCond.Once);
        ImGui.setNextWindowPos(
                ImGui.getMainViewport().getPosX() + 100, ImGui.getMainViewport().getPosY() + 100, ImGuiCond.Once);
        if (ImGui.begin("New Project Window", visible)) {
            ImGui.text("Name:");
            ImGui.sameLine(80f);
            ImGui.inputText("##lbl", name);

            ImGui.alignTextToFramePadding();
            ImGui.text("Location:");
            ImGui.sameLine(80f);

            if (ImGui.button("Browse Folder")) {
                ImGuiFileDialog.openDialog("browse-folder-key", "Choose Folder", null, ".", "",
                        1, 7, WINDOW_FLAGS);
            }

            if (ImGuiFileDialog.display("browse-folder-key", ImGuiFileDialogFlags.None, 200, 400, 800, 600)) {
                if (ImGuiFileDialog.isOk()) {
                    selection = ImGuiFileDialog.getSelection();
                    userData = ImGuiFileDialog.getUserDatas();
                }
                ImGuiFileDialog.close();
            }

            if (ImGui.button("Create")) {
                if (hasName() && hasPath()) {
                    String name = getName();
                    String path = getPath();
                    EventSystem.notify(null, new NewProjectEvent(name, path));
                    hide();
                }
            }

            ImGui.sameLine();
            ImGui.button("Cancel");
        }

        if (hasName()) {
            ImGui.text(getName());
        }

        if (hasPath()) {
            ImGui.text(getPath());
        }

        userData = 0;
        ImGui.end();
    }

    @Override
    public boolean isModal() {
        return true;
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

    public boolean hasName() {
        return name.isNotEmpty();
    }

    public String getName() {
        return name.get();
    }
}
