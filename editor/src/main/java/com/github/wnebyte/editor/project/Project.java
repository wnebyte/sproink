package com.github.wnebyte.editor.project;

import java.io.File;
import javax.xml.bind.annotation.*;

@XmlRootElement
@XmlType(propOrder = { "editor" })
@XmlAccessorType(XmlAccessType.FIELD)
public final class Project {

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String projectDir;

    @XmlAttribute
    private String outDir;

    @XmlAttribute
    private String assetsDir;

    @XmlElement
    private Editor editor;

    public Project() {}

    public Project(String name, String projectDir, String outDir, String assetsDir, Editor editor) {
        this.projectDir = projectDir;
        this.name = name;
        this.outDir = outDir;
        this.assetsDir = assetsDir;
        this.editor = editor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setProjectDir(String projectDir) {
        this.projectDir = projectDir;
    }

    public String getProjectDir() {
        return projectDir;
    }

    public void setOutDir(String outDir) {
        this.outDir = outDir;
    }

    public String getOutDir() {
        return outDir;
    }

    public String getAssetsDir() {
        return assetsDir;
    }

    public void setAssetsDir(String assetsDir) {
        this.assetsDir = assetsDir;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
    }

    public Editor getEditor() {
        return editor;
    }

    public void format() {
        outDir = format(outDir);
        assetsDir = format(assetsDir);
        if (editor != null && editor.getAssets() != null && editor.getAssets().getTabs() != null) {
            for (Tab tab : editor.getAssets().getTabs()) {
                if (tab.getSpritesheets() != null) {
                    for (SpritesheetAsset spritesheet : tab.getSpritesheets()) {
                        String src = spritesheet.getSrc();
                        src = format(src);
                        spritesheet.setSrc(src);
                    }
                }
                if (tab.getSounds() != null) {
                    for (SoundAsset sound : tab.getSounds()) {
                        String src = sound.getSrc();
                        src = format(src);
                        sound.setSrc(src);
                    }
                }
            }
        }
    }

    private String format(String value) {
        if (value == null) {
            return null;
        }
        if (value.startsWith(File.separator)) {
            value = projectDir + File.separator + value;
        }
        if (value.contains("$PROJECTDIR")) {
            value = value.replace("$PROJECTDIR", projectDir);
        }
        if (value.contains("$OUTDIR")) {
            value = value.replace("$OUTDIR", outDir);
        }
        if (value.contains("$ASSETSDIR")) {
            value = value.replace("$ASSETSDIR", assetsDir);
        }
        return value;
    }
}
