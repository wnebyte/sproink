package com.github.wnebyte.util;

public interface ILogger {

    void log(LogLevel logLevel, String tag, String format, Object... args);
}
