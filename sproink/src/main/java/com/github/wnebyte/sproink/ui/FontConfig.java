package com.github.wnebyte.sproink.ui;

import java.util.Objects;

public class FontConfig {

    private String path;

    private int fontSize;

    public FontConfig(String path, int pixelSize) {
        this.path = path;
        this.fontSize = pixelSize;
    }

    public String getPath() {
        return path;
    }

    public int getPixelSize() {
        return fontSize;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof FontConfig)) return false;
        FontConfig font = (FontConfig) o;
        return Objects.equals(font.path, this.path) &&
                Objects.equals(font.fontSize, this.fontSize);
    }

    @Override
    public int hashCode() {
        int result = 55;
        return result *
                2 +
                Objects.hash(this.path) +
                Objects.hash(this.fontSize);
    }
}
