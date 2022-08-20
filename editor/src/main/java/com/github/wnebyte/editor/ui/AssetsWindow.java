package com.github.wnebyte.editor.ui;

import imgui.ImGui;
import com.github.wnebyte.sproink.core.ui.ImGuiWindow;

public class AssetsWindow extends ImGuiWindow {

    @Override
    public void imGui() {
        ImGui.begin("Assets");
        if (ImGui.beginTabBar("AssetsTabBar")) {

            // for ...

            ImGui.endTabBar();
        }
        ImGui.end();
    }
}
