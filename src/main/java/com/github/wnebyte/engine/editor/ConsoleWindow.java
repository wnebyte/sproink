package com.github.wnebyte.engine.editor;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;
import imgui.ImGui;
import imgui.ImGuiTextFilter;
import imgui.ImVec4;
import imgui.flag.*;
import imgui.type.ImString;
import com.github.wnebyte.engine.core.ui.ImGuiWindow;

public class ConsoleWindow extends ImGuiWindow {

    private ImString inputBuffer;

    private List<String> items;

    private List<String> history;

    private int historyPointer;

    private ImGuiTextFilter filter;

    private boolean autoScroll;

    private boolean scrollToBottom;

    private Consumer<String> callback;

    public ConsoleWindow() {
        items = new ArrayList<>();
        history = new ArrayList<>();
        inputBuffer = new ImString();
        historyPointer = -1;
        autoScroll = true;
        scrollToBottom = false;
        filter = new ImGuiTextFilter();
        clearLog();
    }

    public void setCallback(Consumer<String> callback) {
        this.callback = callback;
    }

    public void addLog(String value) {
        items.add(value);
    }

    public void clearLog() {
        items.clear();
    }

    @Override
    public void imGui() {
        if (!isVisible()) {
            return;
        }
        ImGui.setNextWindowSize(520f, 600, ImGuiCond.FirstUseEver);
        if (!ImGui.begin("Console", visible)) {
            ImGui.end();
            return;
        }

        // Options menu
        if (ImGui.beginPopup("Options")) {
            autoScroll = ImGui.checkbox("Auto-scroll", autoScroll);
            ImGui.endPopup();
        }

        // Options, Filter
        if (ImGui.button("Options")) {
            ImGui.openPopup("Options");
        }
        ImGui.sameLine();
        filter.draw("Filter (\\\"incl-excl\\) (\\\"error\")", 180);
        ImGui.separator();

        float footerHeightToReserve = ImGui.getStyle().getItemSpacingY() + ImGui.getFrameHeightWithSpacing();
        ImGui.beginChild("ScrollingRegion", 0, -footerHeightToReserve, false, ImGuiWindowFlags.HorizontalScrollbar);
        if (ImGui.beginPopupContextWindow()) {
            if (ImGui.selectable("Clear")) {
                clearLog();
            }
            ImGui.endPopup();
        }

        // Display every line as a separate entry, so we can change their color or add custom widgets.
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 4, 1);
        if (false) {
            ImGui.logToClipboard();
        }
        for (int i = 0; i < items.size(); i++) {
            String item = items.get(i);
            if (!filter.passFilter(item)) {
                continue;
            }

            ImVec4 color = null;
            if (strstr(item, "[error]")) {
                color = new ImVec4(1.0f, 0.4f, 0.4f, 1.0f);
            } else if (strncmp(item, "# ", 2) == 0) {
                color = new ImVec4(1.0f, 0.8f, 0.6f, 1.0f);
            }
            if (color != null) {
                ImGui.pushStyleColor(ImGuiCol.Text, color.x, color.y, color.z, color.w);
            }
            ImGui.textUnformatted(item);
            if (color != null) {
                ImGui.popStyleColor();
            }
        }

        if (false) {
            ImGui.logFinish();
        }

        if (scrollToBottom || (autoScroll && ImGui.getScrollY() >= ImGui.getScrollMaxY())) {
            ImGui.setScrollHereY(1.0f);
        }
        scrollToBottom = false;

        ImGui.popStyleVar();
        ImGui.endChild();
        ImGui.separator();

        boolean reclaimFocus = false;
        int inputTextFlags = ImGuiInputTextFlags.EnterReturnsTrue | ImGuiInputTextFlags.CallbackCompletion |
                ImGuiInputTextFlags.CallbackHistory;
        if (ImGui.inputText("##inputtext", inputBuffer, inputTextFlags)) {
            reclaimFocus = true;
            String input = inputBuffer.get();
            inputBuffer.clear();
            addLog(input);
            if (input.equals("clear") || input.equals("cls")) {
                clearLog();
            } else if (callback != null) {
                callback.accept(input);
            }
        }

        // Autofocus on window apparition
        ImGui.setItemDefaultFocus();
        if (reclaimFocus) {
            ImGui.setKeyboardFocusHere(-1); // Autofocus previous widget
        }

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
