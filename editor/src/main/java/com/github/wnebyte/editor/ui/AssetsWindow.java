package com.github.wnebyte.editor.ui;

import java.lang.reflect.Constructor;

import com.github.wnebyte.sproink.util.AssetFlyWeight;
import org.joml.Vector2f;
import imgui.ImGui;
import imgui.ImVec2;
import com.github.wnebyte.editor.components.MouseControls;
import com.github.wnebyte.sproink.core.window.Window;
import com.github.wnebyte.sproink.core.ui.ImGuiWindow;
import com.github.wnebyte.sproink.renderer.Texture;
import com.github.wnebyte.sproink.components.Spritesheet;
import com.github.wnebyte.sproink.core.Prefab;
import com.github.wnebyte.sproink.components.Sprite;
import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.core.DefaultPrefab;
import com.github.wnebyte.editor.project.SpritesheetAsset;
import com.github.wnebyte.editor.project.Context;
import com.github.wnebyte.editor.project.Assets;
import com.github.wnebyte.editor.project.Tab;
import com.github.wnebyte.util.Objects;
import static com.github.wnebyte.util.Reflections.newInstance;
import static com.github.wnebyte.util.Reflections.getDefaultConstructor;

public class AssetsWindow extends ImGuiWindow {

    public AssetsWindow() {
        this(true);
    }

    public AssetsWindow(boolean visible) {
        super.visible.set(visible);
    }

    @Override
    public void imGui() {
        if (!isVisible()) return;

        ImGui.begin("Assets");
        int idCounter = 5000;
        if (ImGui.beginTabBar("AssetsTabBar")) {
            Context context = Context.get();
            if (context == null) return;
            Assets assets = context.getProject().getEditor().getAssets();

            for (Tab tab : assets.getTabs()) {
                if (ImGui.beginTabItem(tab.getName())) { // Todo: need to add a default name
                    ImVec2 windowPos = new ImVec2();
                    ImGui.getWindowPos(windowPos);
                    ImVec2 windowSize = new ImVec2();
                    ImGui.getWindowSize(windowSize);
                    ImVec2 itemSpacing = new ImVec2();
                    ImGui.getStyle().getItemSpacing(itemSpacing);
                    float windowX2 = windowPos.x + windowSize.x;

                    for (SpritesheetAsset asset : tab.getSpritesheets()) {
                        Texture texture = AssetFlyWeight.getTexture(asset.getSrc());
                        Spritesheet spritesheet = new Spritesheet(
                                texture, asset.getWidth(), asset.getHeight(),
                                asset.getSize(), asset.getSpacing());
                        Class<? extends Prefab> cls =
                                Objects.requireNonNullElseGet(context.getPrefab(asset.getPrefab()),
                                        () -> DefaultPrefab.class);
                        Constructor<?> cons = getDefaultConstructor(cls);
                        Prefab prefab;
                        if (cons == null) continue;
                        try {
                            prefab = (Prefab) newInstance(cons);
                        } catch (Exception e) {
                            continue;
                        }
                        int from = asset.getFrom();
                        int to = asset.getTo();
                        float scaleX = asset.getScaleXOrDefaultValue(1.0f);
                        float scaleY = asset.getScaleYOrDefaultValue(1.0f);
                        float width  = asset.getWidth() * scaleX;
                        float height = asset.getHeight() * scaleY;

                        for (int i = from; i < to; i++) {
                            Sprite sprite = spritesheet.getSprite(i);
                            int id = sprite.getTexId();
                            Vector2f[] texCoords = sprite.getTexCoords();

                            ImGui.pushID(idCounter++);
                            if (ImGui.imageButton(id, width, height,
                                    texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                                GameObject levelEditorStuff = Window.getScene().getGameObject(MouseControls.class);
                                if (levelEditorStuff != null) {
                                    GameObject go = prefab.generate(sprite);
                                    levelEditorStuff.getComponent(MouseControls.class).drag(go);
                                }
                            }
                            ImGui.popID();

                            ImVec2 lastButtonPos = new ImVec2();
                            ImGui.getItemRectMax(lastButtonPos);
                            float lastButtonX2 = lastButtonPos.x;
                            float nextButtonX2 = lastButtonX2 + itemSpacing.x + asset.getWidth();
                            if (nextButtonX2 < windowX2) {
                                ImGui.sameLine();
                            }
                        }
                    }
                    ImGui.endTabItem();
                }
            }
            ImGui.endTabBar();
        }
        ImGui.end();
    }
}
