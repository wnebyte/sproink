package com.github.wnebyte.sproink.ui;

import com.github.wnebyte.sproink.core.scene.SceneInitializer;
import org.joml.Vector2f;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import com.github.wnebyte.sproink.core.window.Window;
import com.github.wnebyte.sproink.observer.EventSystem;
import com.github.wnebyte.sproink.core.event.MouseListener;
import com.github.wnebyte.sproink.observer.event.GameEngineStartPlayEvent;
import com.github.wnebyte.sproink.observer.event.GameEngineStopPlayEvent;

import java.util.Arrays;

public class GameViewWindow extends ImGuiWindow {

    private static final int WINDOW_FLAGS = ImGuiWindowFlags.NoScrollbar |
            ImGuiWindowFlags.NoScrollWithMouse |
            ImGuiWindowFlags.MenuBar;

    private float leftX, rightX, topY, bottomY;

    private boolean isPlaying = false;

    public GameViewWindow() {
        this(true);
    }

    public GameViewWindow(boolean visible) {
        this.visible.set(visible);
    }

    @Override
    public void imGui() {
        if (!isVisible()) return;

        ImGui.begin(Window.getScene().getName(), visible, WINDOW_FLAGS);
        ImGui.beginMenuBar();
        if (ImGui.menuItem("Play", "", isPlaying, !isPlaying)) {
            isPlaying = true;
            EventSystem.notify(null, new GameEngineStartPlayEvent());
        }
        if (ImGui.menuItem("Stop", "", !isPlaying, isPlaying)) {
            isPlaying = false;
            EventSystem.notify(null, new GameEngineStopPlayEvent());
        }
        ImGui.endMenuBar();

        ImVec2 windowSize = getLargestSizeForViewport();
        ImVec2 windowPos = getCenteredPositionForViewport(windowSize);
        ImGui.setCursorPos(windowPos.x, windowPos.y);

        ImVec2 topLeft = new ImVec2();
        ImGui.getCursorScreenPos(topLeft);
        topLeft.x -= ImGui.getScrollX();
        topLeft.y -= ImGui.getScrollY();
        leftX = topLeft.x;
        bottomY = topLeft.y;
        rightX = topLeft.x + windowSize.x;
        topY = topLeft.y + windowSize.y;

        int textureId = Window.getFrameBuffer().getTextureId();
        ImGui.image(textureId, windowSize.x, windowSize.y, 0, 1, 1, 0);

        MouseListener.setGameViewportPos(new Vector2f(topLeft.x, topLeft.y));
        MouseListener.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));

        ImGui.end();
    }

    private ImVec2 getLargestSizeForViewport() {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / Window.getTargetAspectRatio();
        if (aspectHeight > windowSize.y) {
            // Must switch to pillarbox mode
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * Window.getTargetAspectRatio();
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float viewPortX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewPortY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewPortX + ImGui.getCursorPosX(),
                viewPortY + ImGui.getCursorPosY());
    }

    public boolean getWantCaptureMouse() {
        return MouseListener.getX() >= leftX && MouseListener.getX() <= rightX &&
                MouseListener.getY() >= bottomY && MouseListener.getY() <= topY;
    }
}
