package com.github.wnebyte.editor.ui;

import java.util.List;
import java.util.ArrayList;
import imgui.ImGui;
import imgui.ImGuiTextFilter;
import imgui.ImVec4;
import imgui.type.ImBoolean;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import com.github.wnebyte.sproink.ui.ImGuiWindow;

public class LogWindow extends ImGuiWindow {

    private final List<String> buffer = new ArrayList<>();

    private final ImGuiTextFilter filter = new ImGuiTextFilter();

    private final ImBoolean autoScroll = new ImBoolean(false);

    public LogWindow() {
        this(true);
    }

    public LogWindow(boolean visible) {
        this.visible.set(visible);
    }

    public void log(String tag, String logMessage) {
        log("(" + tag + "): " + logMessage);
    }
    public void log(String logMessage) {
        buffer.add(logMessage);
    }

    public void clear() {
        buffer.clear();
    }

    @Override
    public void imGui() {
        if (!isVisible()) return;

        if (!ImGui.begin("Log", visible)) {
            ImGui.end();
            return;
        }

        // Options Menu
        if (ImGui.beginPopup("Options")) {
            ImGui.checkbox("Auto-scroll", autoScroll);
            ImGui.endPopup();
        }

        // Main Window
        if (ImGui.button("Options")) {
            ImGui.openPopup("Options");
        }
        ImGui.sameLine();
        boolean clear = ImGui.button("Clear");
        ImGui.sameLine();
        boolean copy = ImGui.button("Copy");
        ImGui.sameLine();
        filter.draw("Filter", -100.0f);

        ImGui.separator();
        ImGui.beginChild("scrolling", 0, 0, false, ImGuiWindowFlags.HorizontalScrollbar);

        if (clear) {
            clear();
        }
        if (copy) {
            ImGui.logToClipboard();
        }

        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);

        for (int i = 0; i < buffer.size(); i++) {
            String line = buffer.get(i);
            if (!filter.passFilter(line)) {
                continue;
            }

            ImVec4 color = null;
            if (strstr(line, "[error]")) {
                color = new ImVec4(1.0f, 0.4f, 0.4f, 1.0f);
            } else if (strncmp(line, "# ", 2) == 0) {
                color = new ImVec4(1.0f, 0.8f, 0.6f, 1.0f);
            }
            if (color != null) {
                ImGui.pushStyleColor(ImGuiCol.Text, color.x, color.y, color.z, color.w);
            }
            ImGui.textUnformatted(line);
            if (color != null) {
                ImGui.popStyleColor();
            }
        }

        ImGui.popStyleVar();

        if (autoScroll.get() && ImGui.getScrollY() >= ImGui.getScrollMaxY()) {
            ImGui.setScrollHereY(1.0f);
        }

        ImGui.endChild();
        ImGui.end();
    }

    public boolean strstr(String s1, String s2) {
        return s1.contains(s2);
    }

    public int strncmp(String s1, String s2, int num) {
        int count = 0;

        for (int i = 0; i < s1.length(); i++) {
            char c1 = s1.charAt(i);
            if (s2.length() > i) {
                char c2 = s2.charAt(i);
                if (c1 == c2) {
                    count++;
                } else {
                    return count;
                }
            }
            if (i == num) {
                return count;
            }
        }

        return count;
    }
}
