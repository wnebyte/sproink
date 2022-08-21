package com.github.wnebyte.editor.ui;

import imgui.ImGui;
import imgui.type.ImString;
import imgui.flag.ImGuiInputTextFlags;
import com.github.wnebyte.sproink.core.window.Window;
import com.github.wnebyte.sproink.core.ui.ImGuiWindow;
import com.github.wnebyte.sproink.core.ui.GameViewWindow;
import com.github.wnebyte.sproink.observer.EventSystem;
import com.github.wnebyte.sproink.observer.event.WindowCloseEvent;

public class MenuBar extends ImGuiWindow {

    @Override
    public void imGui() {
        ImGui.beginMainMenuBar();

        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("New Project")) {
                Window.getImGuiLayer().getWindow(NewProjectWindow.class).show();
            }
            if (ImGui.menuItem("Open Project")) {
                Window.getImGuiLayer().getWindow(OpenProjectWindow.class).show();
            }
            if (ImGui.menuItem("Close Project")) {
                EventSystem.notify(null, new WindowCloseEvent());
            }
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