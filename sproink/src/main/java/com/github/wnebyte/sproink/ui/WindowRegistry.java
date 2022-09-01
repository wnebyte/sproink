package com.github.wnebyte.sproink.ui;

import java.util.List;
import java.util.ArrayList;
import com.github.wnebyte.util.Objects;

public class WindowRegistry {

    private final List<ImGuiWindow> windows;

    public WindowRegistry() {
        this(new ArrayList<>());
    }

    public WindowRegistry(List<ImGuiWindow> windows) {
        this.windows = Objects.requireNonNullElseGet(windows, ArrayList::new);
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
                try {
                    return windowClass.cast(window);
                } catch (ClassCastException e) {
                    assert false : String.format("Error: (WindowRegistry) Casting ImGuiWindow: '%s'", window.getClass());
                }
            }
        }

        return null;
    }

    public List<ImGuiWindow> getAllWindows() {
        return windows;
    }
}
