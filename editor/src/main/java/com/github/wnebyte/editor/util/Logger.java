package com.github.wnebyte.editor.util;

import com.github.wnebyte.sproink.core.Window;
import com.github.wnebyte.sproink.ui.ImGuiLayer;
import com.github.wnebyte.editor.ui.LogWindow;
import com.github.wnebyte.util.FsLogger;
import com.github.wnebyte.util.LogLevel;

public class Logger extends FsLogger {

    public Logger(String dir) {
        super(dir);
    }

    @Override
    public void log(LogLevel logLevel, String tag, String format, Object... args) {
        ImGuiLayer imGuiLayer = Window.getImGuiLayer();
        if (imGuiLayer != null) {
            LogWindow logWindow = imGuiLayer.getWindow(LogWindow.class);
            if (logWindow != null) {
                logWindow.log(tag, String.format(format, args));
            }
        }
        super.log(logLevel, tag, format, args);
    }
}
