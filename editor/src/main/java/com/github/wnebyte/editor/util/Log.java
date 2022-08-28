package com.github.wnebyte.editor.util;

import com.github.wnebyte.editor.ui.LogWindow;
import com.github.wnebyte.sproink.ui.ImGuiLayer;
import com.github.wnebyte.sproink.core.window.Window;

public class Log {

    public static void log(String tag, String logMessage) {
        ImGuiLayer imGuiLayer = Window.getImGuiLayer();
        if (imGuiLayer != null) {
            LogWindow logWindow = imGuiLayer.getWindow(LogWindow.class);
            if (logWindow != null) {
                logWindow.log(tag, logMessage);
            }
        }
    }

    public static void log(String tag, String format, Object... args) {
        log(tag, String.format(format, args));
    }
}
