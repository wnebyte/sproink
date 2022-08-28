package com.github.wnebyte.editor.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Collections;

public class Files {

    public static boolean write(Path path, String text) {
        try {
            java.nio.file.Files.write(path, Collections.singleton(text), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String read(Path path) {
        if (!path.toFile().exists()) return null;
        try {
            List<String> lines = java.nio.file.Files.readAllLines(path, StandardCharsets.UTF_8);
            String text = String.join(System.lineSeparator(), lines);
            return text;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean delete(Path path) {
        try {
            java.nio.file.Files.delete(path);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean delete(File file) {
        try {
            return file.delete();
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean create(File file) {
        try {
            return file.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }
}
