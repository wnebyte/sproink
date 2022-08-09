package com.github.wnebyte.engine.editor;

import java.util.Map;
import java.util.HashMap;
import imgui.ImGui;
import imgui.type.ImString;
import imgui.flag.ImGuiCond;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.callback.ImGuiFileDialogPaneFun;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;
import com.github.wnebyte.engine.core.ui.ImGuiWindow;

public class NewProjectWindow extends ImGuiWindow {

    private static final int IMGUI_FLAGS =
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
                String text = selection.values().stream().findFirst().orElse("Choose Folder");
                ImGuiFileDialog.openDialog("browse-folder-key", text, null, ".", "",
                        1, 7, IMGUI_FLAGS);
            }

            if (ImGuiFileDialog.display("browse-folder-key", ImGuiFileDialogFlags.None, 200, 400, 800, 600)) {
                if (ImGuiFileDialog.isOk()) {
                    selection = ImGuiFileDialog.getSelection();
                    userData = ImGuiFileDialog.getUserDatas();
                }
                ImGuiFileDialog.close();
            }
        }

        if (!selection.isEmpty()) {
            ImGui.text(selection.values().stream().findFirst().get());
        }

        userData = 0;
        ImGui.end();
    }

    public boolean hasLocation() {
        return (selection != null) && !(selection.isEmpty());
    }

    public String getLocation() {
        return selection.values().stream().findFirst().orElse(null);
    }

    public boolean hasName() {
        return name.isNotEmpty();
    }

    public String getName() {
        return name.get();
    }
}
