package com.github.wnebyte.engine.editor;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Constructor;
import imgui.ImGui;
import org.joml.Vector4f;
import com.github.wnebyte.engine.core.ecs.GameObject;
import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.core.ui.ImGuiWindow;
import com.github.wnebyte.engine.renderer.PickingTexture;
import com.github.wnebyte.engine.components.SpriteRenderer;
import com.github.wnebyte.engine.util.ReflectionUtil;
import com.github.wnebyte.engine.util.Runtime;

public class PropertiesWindow extends ImGuiWindow {

    private final List<GameObject> activeGameObjects;

    private final List<Vector4f> activeGameObjectsOgColor;

    private final PickingTexture pickingTexture;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
        this.activeGameObjects = new ArrayList<>();
        this.activeGameObjectsOgColor = new ArrayList<>();
    }

    @Override
    public void imGui() {
        if (!isVisible()) return;

        if (activeGameObjects.size() == 1 && activeGameObjects.get(0) != null) {
            GameObject activeGameObject = activeGameObjects.get(0);
            ImGui.begin("Properties", visible);

            if (ImGui.beginPopupContextWindow("ComponentAdder")) {
                for (Class<? extends Component> cls : Runtime.getComponentClasses()) {
                    if (ImGui.menuItem("Add " + cls.getSimpleName())) {
                        if (activeGameObject.getComponent(cls) == null) {
                            try {
                                Constructor<?> constructor = ReflectionUtil.getDefaultConstructor(cls);
                                if (constructor != null) {
                                    boolean accessible = constructor.isAccessible();
                                    if (!accessible) {
                                        constructor.setAccessible(true);
                                    }
                                    Object c = constructor.newInstance();
                                    activeGameObject.addComponent((Component)c);
                                    if (!accessible) {
                                        constructor.setAccessible(false);
                                    }
                                }
                            } catch (Exception e) {
                                System.err.println("Could  not instantiate Component class: " + cls);
                            }
                        }
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

    public void clearSelected() {
        if (!activeGameObjectsOgColor.isEmpty()) {
            int i = 0;
            for (GameObject go : activeGameObjects) {
                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
                if (spr != null) {
                    spr.setColor(activeGameObjectsOgColor.get(i));
                }
                i++;
            }
        }
        activeGameObjects.clear();
        activeGameObjectsOgColor.clear();
    }

    public void setActiveGameObject(GameObject go) {
        if (go != null) {
            clearSelected();
            activeGameObjects.add(go);
        }
    }

    public List<GameObject> getActiveGameObjects() {
        return activeGameObjects;
    }

    public void addActiveGameObject(GameObject go) {
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if (spr != null) {
            activeGameObjectsOgColor.add(new Vector4f(spr.getColor()));
            spr.setColor(new Vector4f(0.8f, 0.8f, 0.0f, 0.8f));
        } else {
            activeGameObjectsOgColor.add(new Vector4f());
        }
        activeGameObjects.add(go);
    }

    public PickingTexture getPickingTexture() {
        return pickingTexture;
    }
}
