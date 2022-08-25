package com.github.wnebyte.editor.ui;

import imgui.ImGui;
import imgui.type.ImString;
import com.github.wnebyte.editor.observer.event.NewSceneEvent;
import com.github.wnebyte.sproink.observer.EventSystem;
import com.github.wnebyte.editor.project.Context;
import com.github.wnebyte.sproink.ui.ImGuiWindow;
import com.github.wnebyte.sproink.core.scene.SceneInitializer;
import com.github.wnebyte.sproink.scenes.LevelSceneInitializer;

public class NewSceneWindow extends ImGuiWindow {

    private static final String TAG = "NewSceneWindow";

    private static final int WINDOW_FLAGS = 0;

    private static final Class<? extends SceneInitializer> DEFAULT_SCENE_INITIALIZER
            = LevelSceneInitializer.class;

    private final ImString name;

    private Class<? extends SceneInitializer> initializer;

    public NewSceneWindow() {
        this(true);
    }

    public NewSceneWindow(boolean visible) {
        this.visible.set(visible);
        this.name = new ImString();
        this.initializer = DEFAULT_SCENE_INITIALIZER;
    }

    @Override
    public void imGui() {
        if (!isVisible()) return;

        Context context = Context.get();
        if (context == null) return;

        ImGui.begin("New Scene Window", visible, WINDOW_FLAGS);
        ImGui.text("Name:");
        ImGui.sameLine();
        ImGui.inputText("##sceneName", name);
        ImGui.text("Initializer:");
        ImGui.sameLine();
        if (ImGui.beginCombo("##initializer", initializer.getCanonicalName())) {
            for (Class<? extends SceneInitializer> cls : context.getSceneInitializers()) {
                boolean isSelected = initializer.equals(cls);
                if (ImGui.selectable(cls.getCanonicalName(), isSelected)) {
                    initializer = cls;
                }
                if (isSelected) {
                    ImGui.setItemDefaultFocus();
                }
            }
            ImGui.endCombo();
        }
        if (ImGui.button("Create")) {
            if (hasName()) {
                EventSystem.notify(null, new NewSceneEvent(name.get(), initializer));
                name.set("");
                initializer = DEFAULT_SCENE_INITIALIZER;
                hide();
            }
        }
        ImGui.end();
    }

    private boolean hasName() {
        return !name.get().equals("");
    }

    @Override
    public boolean isModal() {
        return true;
    }
}
