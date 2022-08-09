package com.github.wnebyte.engine.editor;

import imgui.ImGui;
import imgui.type.ImString;
import imgui.flag.ImGuiInputTextFlags;
import com.github.wnebyte.engine.core.window.Window;
import com.github.wnebyte.engine.core.ui.ImGuiWindow;

public class MenuBar extends ImGuiWindow {

    @Override
    public void imGui() {
        ImGui.beginMainMenuBar();

        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("New Project")) {
                Window.getImGuiLayer().getWindow(NewProjectWindow.class).show();
            }
            ImGui.menuItem("Open...");
            ImGui.menuItem("Close Project");
            ImGui.endMenu();
        }
        if (ImGui.beginMenu("Edit")) {
            ImGui.endMenu();
        }
        if (ImGui.beginMenu("Assets")) {
            ImGui.endMenu();
        }
        if (ImGui.beginMenu("Window")) {
            ImGui.endMenu();
        }
        if (ImGui.beginMenu("Help")) {
            ImGui.endMenu();
        }

        ImGui.sameLine(ImGui.getWindowContentRegionMaxX() - 250);
        search();

        ImGui.endMainMenuBar();
    }

    public boolean search() {
        ImString imString = new ImString();
        if (ImGui.inputText("##txt", imString, ImGuiInputTextFlags.EnterReturnsTrue)) {
            String val = imString.get().toLowerCase();
            switch (val) {
                case "scene hierarchy window":
                    Window.getImGuiLayer().getWindow(SceneHierarchyWindow.class).show();
                    break;
                case "game view window":
                    Window.getImGuiLayer().getWindow(GameViewWindow.class).show();
                    break;
                case "properties window":
                    Window.getImGuiLayer().getWindow(PropertiesWindow.class).show();
                    break;
                case "console window":
                    Window.getImGuiLayer().getWindow(ConsoleWindow.class).show();
                    break;
                case "new project window":
                    Window.getImGuiLayer().getWindow(NewProjectWindow.class).show();
                    break;
            }
            return true;
        }

        return false;
    }
}
