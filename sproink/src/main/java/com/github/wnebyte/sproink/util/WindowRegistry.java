package com.github.wnebyte.sproink.util;

import java.util.List;
import java.util.ArrayList;
import com.github.wnebyte.sproink.core.ui.ImGuiWindow;

public class WindowRegistry {

    private final List<ImGuiWindow> windows;

    public WindowRegistry() {
        this.windows = new ArrayList<>();
    }

    public <T extends ImGuiWindow> void addWindow(ImGuiWindow window) {
        windows.add(window);
    }

    public <T extends ImGuiWindow> void removeWindow(Class<T> windowClass) {
        for (int i = 0; i < windows.size(); i++) {
            ImGuiWindow window = windows.get(i);
            if (windowClass.isAssignableFrom(window.getClass())) {
                windows.remove(i);
                return;
            }
        }
    }

    public <T extends ImGuiWindow> T getWindow(Class<T> windowClass) {
        for (ImGuiWindow window : windows) {
            if (windowClass.isAssignableFrom(window.getClass())) {
                return windowClass.cast(window);
            }
        }
        return null;
    }

    public List<ImGuiWindow> getAllWindows() {
        return windows;
    }
}
