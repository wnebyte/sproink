package com.github.wnebyte.util;

import java.util.Date;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;

public class FsLogger implements ILogger {

    private final Path infoLog;

    private final Path warnLog;

    private final Path debugLog;

    private final Path errorLog;

    public FsLogger(String dir) {
        this.infoLog = new UriBuilder(dir)
                .appendPath("info.txt")
                .toPath();
        this.warnLog = new UriBuilder(dir)
                .appendPath("warn.txt")
                .toPath();
        this.debugLog = new UriBuilder(dir)
                .appendPath("debug.txt")
                .toPath();
        this.errorLog = new UriBuilder(dir)
                .appendPath("error.txt")
                .toPath();
    }

    @Override
    public void log(LogLevel logLevel, String tag, String format, Object... args) {
        String logMessage = "(" + tag + ") " + new Date() + " " + String.format(format, args);
        switch (logLevel) {
            case INFO:
                append(infoLog, logMessage);
                break;
            case WARN:
                append(warnLog, logMessage);
                break;
            case DEBUG:
                append(debugLog, logMessage);
                break;
            case ERROR:
                append(errorLog, logMessage);
                break;
        }
    }

    protected void append(Path path, String line) {
        try {
            Files.write(path, (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
