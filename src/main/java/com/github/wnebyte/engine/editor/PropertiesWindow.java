package com.github.wnebyte.engine.editor;

import java.util.List;
import java.util.ArrayList;
import imgui.ImGui;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.renderer.PickingTexture;
import com.github.wnebyte.engine.physics2d.components.Box2DCollider;
import com.github.wnebyte.engine.physics2d.components.CircleCollider;
import com.github.wnebyte.engine.physics2d.components.RigidBody2D;

public class PropertiesWindow {

    private final List<GameObject> activeGameObjects;

    private GameObject activeGameObject;

    private final PickingTexture pickingTexture;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
        this.activeGameObjects = new ArrayList<>();
    }

    public void imGui() {
        if (activeGameObjects.size() == 1 && activeGameObjects.get(0) != null) {
            activeGameObject = activeGameObjects.get(0);
            ImGui.begin("Properties");

            if (ImGui.beginPopupContextWindow("ComponentAdder")) {
                if (ImGui.menuItem("Add Rigid Body")) {
                    if (activeGameObject.getComponent(RigidBody2D.class) == null) {
                        activeGameObject.addComponent(new RigidBody2D());
                    }
                }

                if (ImGui.menuItem("Add Box Collider")) {
                    if (activeGameObject.getComponent(Box2DCollider.class) == null &&
                            activeGameObject.getComponent(CircleCollider.class) == null) {
                        activeGameObject.addComponent(new Box2DCollider());
                    }
                }

                if (ImGui.menuItem("Add Circle Collider")) {
                    if (activeGameObject.getComponent(CircleCollider.class) == null &&
                            activeGameObject.getComponent(Box2DCollider.class) == null) {
                        activeGameObject.addComponent(new CircleCollider());
                    }
                }

                ImGui.endPopup();
            }

            activeGameObject.imGui();
            ImGui.end();
        }
    }

    public GameObject getActiveGameObject() {
        return (activeGameObjects.size() == 1) ? activeGameObjects.get(0) : null;
    }

    public void setActiveGameObject(GameObject go) {
        if (go != null) {
            clearSelected();
            activeGameObjects.add(go);
        }
    }

    public void clearSelected() {
        activeGameObjects.clear();
    }

    public List<GameObject> getActiveGameObjects() {
        return activeGameObjects;
    }

    public void addActiveGameObject(GameObject go) {
        activeGameObjects.add(go);
    }

    public PickingTexture getPickingTexture() {
        return pickingTexture;
    }
}
