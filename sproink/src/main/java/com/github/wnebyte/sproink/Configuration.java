package com.github.wnebyte.sproink;

import com.github.wnebyte.sproink.core.SceneInitializer;
import com.github.wnebyte.util.ILogger;

public class Configuration {

    protected String title;

    protected String scene;

    protected SceneInitializer sceneInitializer;

    protected boolean docking;

    protected String iniFileName;

    protected ILogger logger;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public void setSceneInitializer(SceneInitializer scene) {
        this.sceneInitializer = scene;
    }

    public void setEnableDocking(boolean value) {
        this.docking = value;
    }

    public void setIniFileName(String iniFileName) {
        this.iniFileName = iniFileName;
    }

    public void setLogger(ILogger logger) {
        this.logger = logger;
    }
}
