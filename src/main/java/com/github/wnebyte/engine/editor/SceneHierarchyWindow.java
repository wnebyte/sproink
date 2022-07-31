package com.github.wnebyte.engine.editor;

import java.util.List;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.core.window.Window;

public class SceneHierarchyWindow {

    private static final String PAYLOAD_DRAG_DROP_TYPE = "SceneHierarchy";

    public void imGui() {
        ImGui.begin("Scene Hierarchy");

        List<GameObject> gameObjects = Window.getScene().getGameObjects();
        int index = 0;
        for (GameObject go : gameObjects) {
            if (!go.isSerialize()) {
                continue;
            }

            boolean treeNodeOpen = doTreeNode(index++, go);
            if (treeNodeOpen) {
                ImGui.treePop();
            }
        }

        ImGui.end();
    }

    private boolean doTreeNode(int index, GameObject go) {
        ImGui.pushID(index);
        boolean doTreeNode = ImGui.treeNodeEx(
                go.getName(),
                ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.FramePadding |
                        ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth,
                go.getName()
        );
        ImGui.popID();

        if (ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload(PAYLOAD_DRAG_DROP_TYPE, go);
            ImGui.text(go.getName());
            ImGui.endDragDropSource();
        }

        if (ImGui.beginDragDropTarget()) {
            Object payload = ImGui.acceptDragDropPayload(PAYLOAD_DRAG_DROP_TYPE);
            if (payload != null) {
                if (payload.getClass().isAssignableFrom(GameObject.class)) {
                    GameObject gameObject = (GameObject) payload;
                    System.out.printf("(Debug): Payload Accepted: '%d:%s'%n", gameObject.getId(), gameObject.getName());
                }
            }
            ImGui.endDragDropTarget();
        }

        return doTreeNode;
    }
}
