package com.github.wnebyte.editor.project;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import com.github.wnebyte.util.UriBuilder;

public class ProjectInitializer {

    private final List<File> dirs;

    private final List<Template> templates;

    private final List<Replacement> replacements;

    private final List<String> copies;

    public ProjectInitializer(File root, String name) {
        this.dirs = new ArrayList<File>() {
            {
                add(root);
                add(new UriBuilder(root.getAbsolutePath())
                        .appendPath("src")
                        .toFile());
                add(new UriBuilder(root.getAbsolutePath())
                        .appendPath("src")
                        .appendPath("main")
                        .toFile());
                add(new UriBuilder(root.getAbsolutePath())
                        .appendPath("src")
                        .appendPath("main")
                        .appendPath("java")
                        .toFile());
                add(new UriBuilder(root.getAbsolutePath())
                        .appendPath("src")
                        .appendPath("main")
                        .appendPath("resources")
                        .toFile());
                add(new UriBuilder(root.getAbsolutePath())
                        .appendPath("src")
                        .appendPath("test")
                        .toFile());
                add(new UriBuilder(root.getAbsolutePath())
                        .appendPath("src")
                        .appendPath("test")
                        .appendPath("java")
                        .toFile());
                add(new UriBuilder(root.getAbsolutePath())
                        .appendPath("src")
                        .appendPath("test")
                        .appendPath("resources")
                        .toFile());
                add(new UriBuilder(root.getAbsolutePath())
                        .appendPath("logs")
                        .toFile());
            }
        };
        this.templates = new ArrayList<Template>() {
            {
                add(new Template.Builder()
                        .setName("Main.java")
                        .setSrc(new UriBuilder(".." + File.separator + "assets")
                                .appendPath("templates")
                                .appendPath("Main.java")
                                .toPath())
                        .setDest(new UriBuilder(root.getAbsolutePath())
                                .appendPath("src")
                                .appendPath("main")
                                .appendPath("java")
                                .appendPath("Main.java")
                                .toPath())
                        .build());
                add(new Template.Builder()
                        .setName("project.xml")
                        .setSrc(new UriBuilder(".." + File.separator + "assets")
                                .appendPath("templates")
                                .appendPath("project.xml")
                                .toPath())
                        .setDest(new UriBuilder(root.getAbsolutePath())
                                .appendPath("project.xml")
                                .toPath())
                        .build());
                add(new Template.Builder()
                        .setName("build.gradle")
                        .setSrc(new UriBuilder(".." + File.separator + "assets")
                                .appendPath("templates")
                                .appendPath("build.gradle")
                                .toPath())
                        .setDest(new UriBuilder(root.getAbsolutePath())
                                .appendPath("build.gradle")
                                .toPath())
                        .build());
                add(new Template.Builder()
                        .setName("settings.gradle")
                        .setSrc(new UriBuilder(".." + File.separator + "assets")
                                .appendPath("templates")
                                .appendPath("settings.gradle")
                                .toPath())
                        .setDest(new UriBuilder(root.getAbsolutePath())
                                .appendPath("settings.gradle")
                                .toPath())
                        .build());
                add(new Template.Builder()
                        .setName("assets")
                        .setSrc(new UriBuilder(".." + File.separator + "assets")
                                .appendPath("templates")
                                .appendPath("assets")
                                .toPath())
                        .setDest(new UriBuilder(root.getAbsolutePath())
                                .appendPath("assets")
                                .toPath())
                        .build());
                add(new Template.Builder()
                        .setName("lib")
                        .setSrc(new UriBuilder(".." + File.separator + "assets")
                                .appendPath("templates")
                                .appendPath("lib")
                                .toPath())
                        .setDest(new UriBuilder(root.getAbsolutePath())
                                .appendPath("lib")
                                .toPath())
                        .build());
            }
        };
        this.replacements = new ArrayList<Replacement>() {
            {
                add(new Replacement.Builder()
                        .setNames("Main.java", "settings.gradle", "build.gradle")
                        .setRegex("[$]name")
                        .setValue(name)
                        .build());
            }
        };
        this.copies = new ArrayList<String>() {
            { add("lib");}
            { add("assets"); }
        };
    }

    public void init() {
        mkdirs();
        replaceLines();
        copy();
        cleanup();
    }

    private Template getTemplate(String name) {
        return templates.stream()
                .filter(template -> template.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private void mkdirs() {
        for (File dir : dirs) {
            assert dir.mkdir() : String.format("Error: (ProjectInitializer) Could not mkdir: %s", dir.getAbsolutePath());
        }
        dirs.clear();
    }

    private void replaceLines() {
        for (Replacement r : replacements) {
            for (String name : r.getNames()) {
                Template t = getTemplate(name);
                if (t != null) {
                    t.read();
                    t.replaceAllLines(r.getRegex(), r.getValue());
                    t.write();
                }
            }
        }
    }

    private void copy() {
        for (String copy : copies) {
            Template t = getTemplate(copy);
            if (t != null) {
                t.copy();
            }
        }
    }

    private void cleanup() {
        dirs.clear();
        templates.clear();
        replacements.clear();
        copies.clear();
    }
}
