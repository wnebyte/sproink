package com.github.wnebyte.engine.editor;

import java.util.List;
import imgui.ImGui;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.core.window.Window;
import imgui.flag.ImGuiTreeNodeFlags;

public class SceneHierarchyWindow {

    public void imGui() {
        ImGui.begin("Scene Hierarchy");

        List<GameObject> gameObjects = Window.getScene().getGameObjects();
        int index = 0;
        for (GameObject go : gameObjects) {
            if (!go.isSerialize()) {
                continue;
            }

            ImGui.pushID(index++);
            boolean treeNodeOpen = ImGui.treeNodeEx(
                    go.getName(),
                    ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.FramePadding |
                            ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth,
                    go.getName()
            );
            ImGui.popID();

            if (treeNodeOpen) {
                ImGui.treePop();
            }
        }

        ImGui.end();
    }
}
