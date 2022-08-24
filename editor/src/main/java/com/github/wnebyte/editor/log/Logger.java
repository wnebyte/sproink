package com.github.wnebyte.editor.log;

import com.github.wnebyte.editor.ui.LogWindow;
import com.github.wnebyte.sproink.ui.ImGuiLayer;
import com.github.wnebyte.sproink.core.window.Window;

public class Logger {

    public static void log(String tag, String logMessage) {
        ImGuiLayer imGuiLayer = Window.getImGuiLayer();
        if (imGuiLayer != null) {
            LogWindow logWindow = imGuiLayer.getWindow(LogWindow.class);
            logWindow.log(tag, logMessage);
        }
    }
}
