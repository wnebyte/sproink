package com.github.wnebyte.sproink.util;

import com.github.wnebyte.util.ILogger;
import com.github.wnebyte.util.LogLevel;

public class Log {

    public static void setLogger(ILogger logger) {
        Log.logger = logger;
    }

    private static ILogger logger;

    public static void i(String tag, String format, Object... args) {
        if (logger != null) {
            logger.log(LogLevel.INFO, tag, format, args);
        }
    }

    public static void w(String tag, String format, Object... args) {
        if (logger != null) {
            logger.log(LogLevel.WARN, tag, format, args);
        }
    }

    public static void d(String tag, String format, Object... args) {
        if (logger != null) {
            logger.log(LogLevel.DEBUG, tag, format, args);
        }
    }

    public static void e(String tag, String format, Object... args) {
        if (logger != null) {
            logger.log(LogLevel.ERROR, tag, format, args);
        }
    }
}
