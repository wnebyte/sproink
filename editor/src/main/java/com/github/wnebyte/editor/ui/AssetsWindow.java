package com.github.wnebyte.editor.ui;

import java.io.File;
import org.joml.Vector2f;
import imgui.ImGui;
import imgui.ImVec2;
import com.github.wnebyte.editor.project.*;
import com.github.wnebyte.editor.components.MouseControls;
import com.github.wnebyte.sproink.core.Window;
import com.github.wnebyte.sproink.ui.ImGuiWindow;
import com.github.wnebyte.sproink.renderer.Texture;
import com.github.wnebyte.sproink.components.Spritesheet;
import com.github.wnebyte.sproink.core.Prefab;
import com.github.wnebyte.sproink.components.Sprite;
import com.github.wnebyte.sproink.core.GameObject;
import com.github.wnebyte.sproink.util.Assets;
import com.github.wnebyte.sproink.core.SpritePrefab;
import com.github.wnebyte.sproink.core.Sound;
import com.github.wnebyte.util.Objects;

public class AssetsWindow extends ImGuiWindow {

    private static final String TAG = "AssetsWindow";

    private static final String TITLE = "Assets";

    private static final int WINDOW_FLAGS = 0;

    public AssetsWindow() {
        this(true);
    }

    public AssetsWindow(boolean visible) {
        this.visible.set(visible);
    }

    @Override
    public void imGui() {
        if (!isVisible()) return;
        Context context = Context.get();
        if (context == null) return;

        ImGui.begin(TITLE, visible, WINDOW_FLAGS);
        int idCounter = 5000;
        if (ImGui.beginTabBar("AssetsTabBar")) {
            com.github.wnebyte.editor.project.Assets assets = context.getProject().getEditor().getAssets();
            ImVec2 windowPos = new ImVec2();
            ImGui.getWindowPos(windowPos);
            ImVec2 windowSize = new ImVec2();
            ImGui.getWindowSize(windowSize);
            ImVec2 itemSpacing = new ImVec2();
            ImGui.getStyle().getItemSpacing(itemSpacing);
            float windowX2 = windowPos.x + windowSize.x;

            for (Tab tab : assets.getTabs()) {
                if (ImGui.beginTabItem(tab.getName())) {
                    if (tab.getSpritesheets() != null) {
                        for (SpritesheetAsset asset : tab.getSpritesheets()) {
                            Texture texture = Assets.getTexture(asset.getSrc());
                            Assets.addSpritesheet(asset.getSrc(),
                                    () -> new Spritesheet(texture,
                                            asset.getWidth(), asset.getHeight(),
                                            asset.getSize(), asset.getSpacing()));
                            Spritesheet spritesheet = Assets.getSpritesheet(asset.getSrc());

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
                                        Prefab prefab = context.newPrefab(
                                                Objects.requireNonNullElseGet(asset.getPrefab(), SpritePrefab.class::getCanonicalName));
                                        if (prefab != null) {
                                            GameObject go = prefab.generate(sprite);
                                            levelEditorStuff.getComponent(MouseControls.class).drag(go);
                                        }
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
                    }
                    imGuiSounds(tab);
                    ImGui.endTabItem();
                }
            }
            ImGui.endTabBar();
        }
        ImGui.end();
    }

    private void imGuiSounds(Tab tab) {
        if (tab == null || tab.getSounds() == null) {
            return;
        }
        for (SoundAsset asset : tab.getSounds()) {
            Sound sound = com.github.wnebyte.sproink.util.Assets.addSound(asset.getSrc(), asset.loops());
            File tmp = new File(sound.getPath());
            if (ImGui.button(tmp.getName())) {
                if (!sound.isPlaying()) {
                    sound.play();
                } else {
                    sound.stop();
                }
            }
            if (ImGui.getContentRegionAvailX() > 100) {
                ImGui.sameLine();
            }
        }
    }
}
