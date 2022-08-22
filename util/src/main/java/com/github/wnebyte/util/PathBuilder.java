package com.github.wnebyte.util;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class PathBuilder {

    private String base;

    private final List<String> paths;

    public PathBuilder() {
        this(null);
    }

    public PathBuilder(String base) {
        this.base = base;
        this.paths = new ArrayList<>();
    }

    public PathBuilder setBase(String base) {
        this.base = base;
        return this;
    }

    public PathBuilder addPath(String path) {
        paths.add(path);
        return this;
    }

    public String build() {
        if (base == null)
            throw new IllegalArgumentException(
                    "Base have to be non-null"
            );
        return base + (base.endsWith(File.separator) ? "" : File.separator) + String.join(File.separator, paths);
    }
}
