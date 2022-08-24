package com.github.wnebyte.editor.project;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ProjectInitializer {

    private final File root;

    private final List<String> dirs;

    private final Map<String, String> files;

    public ProjectInitializer(File root) {
        this.root = root;
        this.dirs = new ArrayList<String>() {
            { add("src"); }
            { add("src/main"); }
            { add("src/main/java"); }
            { add("src/main/resources"); }
            { add("src/test"); }
            { add("src/test/java"); }
            { add("src/test/resources"); }
            { add("assets"); }
            { add("assets/fonts"); }
            { add("assets/images"); }
            { add("assets/images/spritesheets"); }
            { add("assets/scenes"); }
            { add("assets/shaders"); }
            { add("assets/sounds"); }
            { add("lib"); }
        };
        this.files = new HashMap<String, String>() {
            { put("/templates/Main.java", "src/main/java/Main.java"); }
            { put("/templates/project.xml", "project.xml"); }
            { put("/templates/build.gradle", "build.gradle"); }
            { put("/templates/settings.gradle", "settings.gradle"); }
            { put("/templates/sproink-1.0.jar", "lib/sproink-1.0.jar"); }
            { put("/templates/jbox2d-library.jar", "lib/jbox2d-library.jar"); }
        };
    }

    public void mkdirs() {
        assert root.mkdir() : String.format("(Error): Failed to mkdir: %s%n", root.getAbsolutePath());
        for (String path : dirs) {
            path = (root.getAbsolutePath() + "/" + path)
                    .replace("/", File.separator);
            File value = new File(path);
            assert value.mkdir() : String.format("(Error): Failed to mkdir: %s%n", value.getAbsolutePath());
        }
    }

    public void copyTemplates() {
        try {
            for (Map.Entry<String, String> entry : files.entrySet()) {
                Path src = Paths.get(this.getClass().getResource(entry.getKey()).toURI());
                Path dest = Paths.get((root.getAbsolutePath() + "/" + entry.getValue())
                        .replace("/", File.separator));
                try {
                    Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
