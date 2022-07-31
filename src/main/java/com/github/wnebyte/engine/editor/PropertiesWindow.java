package com.github.wnebyte.engine.editor;

import imgui.ImGui;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.core.event.MouseListener;
import com.github.wnebyte.engine.core.scene.Scene;
import com.github.wnebyte.engine.components.NonPickable;
import com.github.wnebyte.engine.renderer.PickingTexture;
import com.github.wnebyte.engine.physics2d.components.Box2DCollider;
import com.github.wnebyte.engine.physics2d.components.CircleCollider;
import com.github.wnebyte.engine.physics2d.components.RigidBody2D;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {

    private GameObject activeGameObject;

    private final PickingTexture pickingTexture;

    private float debounce = 0.2f;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
    }

    public void update(float dt, Scene scene) {
        debounce -= dt;
        if (!MouseListener.isDragging() && MouseListener.isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            int id = pickingTexture.readPixel(x, y);
            GameObject pickedObject = scene.getGameObject(id);
            if (pickedObject != null && pickedObject.getComponent(NonPickable.class) == null) {
                activeGameObject = pickedObject;
                System.out.printf("(Debug): ID: '%d'%n", id);
            } else if (pickedObject == null && !MouseListener.isDragging()) {
                activeGameObject = null;
            }
            debounce = 0.2f;
        }
    }

    public void imGui() {
        if (activeGameObject != null) {
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
        return activeGameObject;
    }

    public void setActiveGameObject(GameObject go) {
        activeGameObject = go;
    }
}
