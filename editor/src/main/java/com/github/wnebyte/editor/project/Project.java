package com.github.wnebyte.editor.project;

import javax.xml.bind.annotation.*;

@XmlRootElement
@XmlType(propOrder = { "editor" })
@XmlAccessorType(XmlAccessType.FIELD)
public final class Project {

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String path;

    @XmlAttribute(name = "out-dir")
    private String outDir;

    @XmlElement
    private Editor editor;

    public Project() {}

    public Project(String name, String path, String outDir, Editor editor) {
        this.path = path;
        this.name = name;
        this.outDir = outDir;
        this.editor = editor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setOutDir(String outDir) {
        this.outDir = outDir;
    }

    public String getOutDir() {
        return outDir;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
    }

    public Editor getEditor() {
        return editor;
    }
}
