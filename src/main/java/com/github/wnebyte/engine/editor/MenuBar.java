package com.github.wnebyte.engine.editor;

import imgui.ImGui;
import com.github.wnebyte.engine.observer.EventSystem;
import com.github.wnebyte.engine.observer.event.LoadLevelEvent;
import com.github.wnebyte.engine.observer.event.SaveLevelEvent;

public class MenuBar {

    public void imGui() {
        ImGui.beginMenuBar();

        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Save", "CTRL+S")) {
                EventSystem.notify(null, new SaveLevelEvent());
            }
            if (ImGui.menuItem("Load", "CTRL+O")) {
                EventSystem.notify(null, new LoadLevelEvent());
            }
            ImGui.endMenu();
        }

        ImGui.endMenuBar();
    }
}
