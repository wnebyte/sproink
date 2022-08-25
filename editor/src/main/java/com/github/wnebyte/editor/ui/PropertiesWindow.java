package com.github.wnebyte.editor.ui;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Constructor;

import com.github.wnebyte.editor.util.Logger;
import imgui.ImGui;
import org.joml.Vector4f;
import com.github.wnebyte.editor.project.Context;
import com.github.wnebyte.sproink.components.SpriteRenderer;
import com.github.wnebyte.sproink.renderer.PickingTexture;
import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.core.ecs.Component;
import com.github.wnebyte.sproink.ui.ImGuiWindow;
import static com.github.wnebyte.util.Reflections.newInstance;
import static com.github.wnebyte.util.Reflections.getDefaultConstructor;

public class PropertiesWindow extends ImGuiWindow {

    private static final String TAG = "PropertiesWindow";

    private static final int WINDOW_FLAGS = 0;

    private static final String TITLE = "Inspector";

    private final List<GameObject> activeGameObjects;

    private final List<Vector4f> activeGameObjectsOgColor;

    private final PickingTexture pickingTexture;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this(true, pickingTexture);
    }

    public PropertiesWindow(boolean visible, PickingTexture pickingTexture) {
        this.visible.set(visible);
        this.pickingTexture = pickingTexture;
        this.activeGameObjects = new ArrayList<>();
        this.activeGameObjectsOgColor = new ArrayList<>();
    }

    @Override
    public void imGui() {
        if (!isVisible()) return;
        Context context = Context.get();
        if (context == null) return;

        if (activeGameObjects.size() == 1 && activeGameObjects.get(0) != null) {
            GameObject activeGameObject = activeGameObjects.get(0);
            ImGui.begin(TITLE, visible, WINDOW_FLAGS);

            if (ImGui.beginPopupContextWindow("ComponentAdder")) {
                for (Class<? extends Component> cls : context.getComponents()) {
                    if (ImGui.menuItem("Add " + cls.getSimpleName())) {
                        if (activeGameObject.getComponent(cls) == null) {
                            Constructor<?> cons = getDefaultConstructor(cls);
                            if (cons != null) {
                                Object obj = newInstance(cons);
                                if (obj != null) {
                                    activeGameObject.addComponent((Component) obj);
                                } else {
                                    Logger.log(TAG, "Could not instantiate component: '" + cls + "'");
                                }
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
