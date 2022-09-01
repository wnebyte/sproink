package com.github.wnebyte.editor.project;

import java.util.List;
import java.util.regex.Pattern;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import com.github.wnebyte.util.Files;

public class Template {

    private final String name;

    private final Path src;

    private final Path dest;

    private List<String> lines;

    public Template(String name, Path src, Path dest) {
        this.name = name;
        this.src = src;
        this.dest = dest;
    }

    public void read() {
        lines = Files.readAllLines(src);
    }

    public void replaceAllLines(String regex, String replacement) {
        Pattern pattern = Pattern.compile(regex);
        lines.replaceAll(line -> pattern.matcher(line).replaceAll(replacement));
    }

    public void write() {
        Files.writeAll(dest, lines);
    }

    public void copy() {
        if (src.toFile().isDirectory()) {
            copyFileTree();
        } else if (src.toFile().isFile()) {
            copyFile();
        }
    }

    private void copyFile() {
        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
    }

    private void copyFileTree() {

    }

    public String getName() {
        return name;
    }

    public Path getSrc() {
        return src;
    }

    public Path getDest() {
        return dest;
    }

    public static class Builder {

        private String name = null;

        private Path src = null;

        private Path dest = null;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setSrc(Path src) {
            this.src = src;
            return this;
        }

        public Builder setDest(Path dest) {
            this.dest = dest;
            return this;
        }

        public Template build() {
            return new Template(name, src, dest);
        }
    }
}
