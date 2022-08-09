package com.github.wnebyte.engine.editor;

import java.awt.*;
import java.net.URI;
import java.util.Map;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.callback.ImGuiFileDialogPaneFun;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;
import com.github.wnebyte.engine.core.ui.ImGuiWindow;

public class FileDialogWindow extends ImGuiWindow {

    private final String URL = "C:\\";

    private Map<String, String> selection = null;

    private long userData = 0;

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
        if (ImGui.begin("FileDialogWindow", visible)) {
            ImGui.text("This is a demo");

            ImGui.alignTextToFramePadding();
            ImGui.text("Repo:");
            ImGui.sameLine();

            if (ImGui.button(URL)) {
                try {
                    Desktop.getDesktop().browse(new URI(URL));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (ImGui.button("Browse File")) {
                ImGuiFileDialog.openModal("browse-key", "Choose File", ".java", ".", callback,
                        250, 1, 42, ImGuiFileDialogFlags.None);
            }

            if (ImGuiFileDialog.display("browse-key", ImGuiFileDialogFlags.None, 200, 400, 800, 600)) {
                if (ImGuiFileDialog.isOk()) {
                    selection = ImGuiFileDialog.getSelection();
                    userData = ImGuiFileDialog.getUserDatas();
                }
                ImGuiFileDialog.close();
            }

            if (ImGui.button("Browse Folder")) {
                ImGuiFileDialog.openDialog("browse-folder-key", "Choose Folder", null, ".", "",
                        1, 7, ImGuiFileDialogFlags.None);
            }

            if (ImGuiFileDialog.display("browse-folder-key", ImGuiFileDialogFlags.None, 200, 400, 800, 600)) {
                if (ImGuiFileDialog.isOk()) {
                    selection = ImGuiFileDialog.getSelection();
                    userData = ImGuiFileDialog.getUserDatas();
                }
                ImGuiFileDialog.close();
            }
        }

        if (selection != null && !selection.isEmpty()) {
            ImGui.text("Selected: " + selection.values().stream().findFirst().get());
            ImGui.text("User Data: " + userData);
        }

        selection = null;
        userData = 0;

        ImGui.end();
    }
}

