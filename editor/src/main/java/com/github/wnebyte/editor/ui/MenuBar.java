package com.github.wnebyte.editor.ui;

import com.github.wnebyte.editor.project.Context;
import com.github.wnebyte.sproink.core.SceneInitializer;
import imgui.ImGui;
import imgui.type.ImString;
import imgui.flag.ImGuiInputTextFlags;
import com.github.wnebyte.editor.observer.event.CompileEvent;
import com.github.wnebyte.editor.observer.event.SaveSceneEvent;
import com.github.wnebyte.sproink.core.Window;
import com.github.wnebyte.sproink.ui.ImGuiWindow;
import com.github.wnebyte.sproink.ui.GameViewWindow;
import com.github.wnebyte.sproink.observer.EventSystem;

public class MenuBar extends ImGuiWindow {

    @Override
    public void imGui() {
        ImGui.beginMainMenuBar();

        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("New Project", "CTRL+N")) {
                Window.getImGuiLayer().getWindow(NewProjectWindow.class).show();
            }
            if (ImGui.menuItem("Open Project", "CTRL+O")) {
                Window.getImGuiLayer().getWindow(OpenProjectWindow.class).show();
            }
            ImGui.separator();
            if (ImGui.menuItem("Sync")) {

            }
            if (ImGui.menuItem("Compile")) {
                EventSystem.notify(null, new CompileEvent());
            }
            ImGui.endMenu();
        }
        if (ImGui.beginMenu("Edit")) {
            ImGui.endMenu();
        }
        if (ImGui.beginMenu("Assets")) {
            ImGui.endMenu();
        }
        if (ImGui.beginMenu("Scene")) {
            if (ImGui.menuItem("New Scene", "CTRL+ALT+N")) {
                Window.getImGuiLayer().getWindow(NewSceneWindow.class).show();
            }
            if (ImGui.menuItem("Save Scene", "CTRL+ALT+S")) {
                EventSystem.notify(null, new SaveSceneEvent());
            }
            if (ImGui.menuItem("Save Scene As")) {

            }
            ImGui.separator();
            Context context = Context.get();
            if (context != null) {
                if (ImGui.beginMenu("Editor Scene Initializer")) {
                    for (Class<? extends SceneInitializer> cls : context.getSceneInitializers()) {
                        boolean selected = (cls == context.getEditorSceneInitializer());
                        if (ImGui.menuItem(cls.getCanonicalName(), "", selected)) {
                            context.setEditorSceneInitializer(cls);
                        }
                    }
                    ImGui.endMenu();
                }
                if (ImGui.beginMenu("Scene Initializer")) {
                    for (Class<? extends SceneInitializer> cls : context.getSceneInitializers()) {
                        boolean selected = (cls == context.getSceneInitializer());
                        if (ImGui.menuItem(cls.getCanonicalName(), "", selected)) {
                            context.setSceneInitializer(cls);
                        }
                    }
                    ImGui.endMenu();
                }
            }
            ImGui.endMenu();
        }
        if (ImGui.beginMenu("Window")) {
            ImGui.endMenu();
        }
        if (ImGui.beginMenu("Help")) {
            ImGui.endMenu();
        }

        ImGui.sameLine(ImGui.getWindowContentRegionMaxX() - 250);
        inputSearch();

        ImGui.endMainMenuBar();
    }

    public boolean inputSearch() {
        ImString imString = new ImString();
        if (ImGui.inputText("##txt", imString, ImGuiInputTextFlags.EnterReturnsTrue)) {
            String val = imString.get().toLowerCase();
            ImGuiWindow window;

            switch (val) {
                case "scene hierarchy":
                    window = Window.getImGuiLayer().getWindow(SceneHierarchyWindow.class);
                    break;
                case "game view":
                    window = Window.getImGuiLayer().getWindow(GameViewWindow.class);
                    break;
                case "properties":
                    window = Window.getImGuiLayer().getWindow(PropertiesWindow.class);
                    break;
                case "console":
                    window = Window.getImGuiLayer().getWindow(ConsoleWindow.class);
                    break;
                case "scene view":
                    window = Window.getImGuiLayer().getWindow(SceneViewWindow.class);
                    break;
                case "directory view":
                    window = Window.getImGuiLayer().getWindow(DirectoryViewWindow.class);
                    break;
                case "log":
                    window = Window.getImGuiLayer().getWindow(LogWindow.class);
                    break;
                default:
                    return false;
            }

            window.unlock();
            window.show();
        }

        return false;
    }
}
