package com.github.wnebyte.sproink.core;

import java.util.List;
import java.util.ArrayList;
import com.github.wnebyte.sproink.ui.ImGuiWindow;
import com.github.wnebyte.sproink.ui.FontConfig;

public class WindowArgs {

    public String title;

    public List<ImGuiWindow> windows;

    public boolean docking;

    public String iniFileName;

    public List<FontConfig> fonts;

    public String scene;

    public SceneInitializer sceneInitializer;

    public String assetsDir;

    public WindowArgs(
            String title,
            List<ImGuiWindow> windows,
            boolean docking,
            String iniFileName,
            List<FontConfig> fonts,
            String scene,
            SceneInitializer sceneInitializer,
            String assetsDir
    ) {
        this.title = title;
        this.windows = windows;
        this.docking = docking;
        this.iniFileName = iniFileName;
        this.fonts = fonts;
        this.scene = scene;
        this.sceneInitializer = sceneInitializer;
        this.assetsDir = assetsDir;
    }

    public String getTitle() {
        return title;
    }

    public List<ImGuiWindow> getWindows() {
        return windows;
    }

    public boolean enableDocking() {
        return docking;
    }

    public String getIniFileName() {
        return iniFileName;
    }

    public List<FontConfig> getFonts() {
        return fonts;
    }

    public String getScene() {
        return scene;
    }

    public SceneInitializer getSceneInitializer() {
        return sceneInitializer;
    }

    public String getAssetsDir() {
        return assetsDir;
    }

    public static class Builder {

        private String title = "Window";

        private List<ImGuiWindow> windows = null;

        private boolean docking = true;

        private String iniFileName = null;

        private List<FontConfig> fonts = null;

        private String scene = null;

        private SceneInitializer sceneInitializer = null;

        private String assetsDir = null;

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setWindows(List<ImGuiWindow> windows) {
            this.windows = windows;
            return this;
        }

        public Builder addWindow(ImGuiWindow window) {
            if (windows == null) {
                windows = new ArrayList<>();
            }
            windows.add(window);
            return this;
        }

        public Builder setEnableDocking(boolean value) {
            this.docking = value;
            return this;
        }

        public Builder setIniFileName(String iniFileName) {
            this.iniFileName = iniFileName;
            return this;
        }

        public Builder setFonts(List<FontConfig> fonts) {
            this.fonts = fonts;
            return this;
        }

        public Builder addFont(FontConfig font) {
            if (fonts == null) {
                fonts = new ArrayList<>();
            }
            fonts.add(font);
            return this;
        }

        public Builder setScene(String scene) {
            this.scene = scene;
            return this;
        }

        public Builder setSceneInitializer(SceneInitializer sceneInitializer) {
            this.sceneInitializer = sceneInitializer;
            return this;
        }

        public Builder setAssetsDir(String assetsDir) {
            this.assetsDir = assetsDir;
            return this;
        }

        public WindowArgs build() {
            return new WindowArgs(title, windows, docking, iniFileName, fonts,
                    scene, sceneInitializer, assetsDir);
        }
    }

}
