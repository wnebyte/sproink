package com.github.wnebyte.util;

import java.util.List;
import java.util.Collections;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;

public class Files {

    public static boolean write(Path path, CharSequence line) {
        try {
            java.nio.file.Files.write(path, Collections.singleton(line), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeAll(Path path, Iterable<? extends CharSequence> lines) {
        try {
            java.nio.file.Files.write(path, lines, StandardCharsets.UTF_8,
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

    public static List<String> readAllLines(Path path) {
        if (!path.toFile().exists()) return null;
        try {
            List<String> lines = java.nio.file.Files.readAllLines(path, StandardCharsets.UTF_8);
            return lines;
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

    public static Path copy(Path src, Path dest, CopyOption... options) {
        try {
            return java.nio.file.Files.copy(src, dest, options);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
