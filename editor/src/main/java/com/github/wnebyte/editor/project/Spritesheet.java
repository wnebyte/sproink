package com.github.wnebyte.editor.project;

import javax.xml.bind.annotation.*;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public final class Spritesheet {

    @XmlAttribute
    private String src;

    @XmlAttribute
    private int width;

    @XmlAttribute
    private int height;

    @XmlAttribute
    private int spacing;

    @XmlAttribute
    private int from;

    @XmlAttribute
    private int to;

    @XmlAttribute
    private String prefab;

    public Spritesheet() {}

    public Spritesheet(String src, int width, int height, int spacing, int from, int to, String prefab) {
        this.src = src;
        this.width = width;
        this.height = height;
        this.spacing = spacing;
        this.from = from;
        this.to = to;
        this.prefab = prefab;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getSpacing() {
        return spacing;
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public String getPrefab() {
        return prefab;
    }

    public void setPrefab(String prefab) {
        this.prefab = prefab;
    }
}
