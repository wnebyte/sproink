package com.github.wnebyte.editor.project;

import javax.xml.bind.annotation.*;

@XmlType(name = "spritesheet")
@XmlAccessorType(XmlAccessType.FIELD)
public final class SpritesheetAsset {

    @XmlAttribute
    private String src;

    @XmlAttribute
    private int size;

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

    @XmlAttribute(name = "scale-x")
    private float scaleX;

    @XmlAttribute(name = "scale-y")
    private float scaleY;

    @XmlAttribute
    private String prefab;

    public SpritesheetAsset() {}

    public SpritesheetAsset(String src, int size, int width, int height, int spacing, int from, int to,
                            float scaleX, float scaleY, String prefab) {
        this.src = src;
        this.size = size;
        this.width = width;
        this.height = height;
        this.spacing = spacing;
        this.from = from;
        this.to = to;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.prefab = prefab;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
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

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleXOrDefaultValue(float defaultValue) {
        return (scaleX == 0) ? defaultValue : scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public float getScaleYOrDefaultValue(float defaultValue) {
        return (scaleY == 0) ? defaultValue : scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public String getPrefab() {
        return prefab;
    }

    public void setPrefab(String prefab) {
        this.prefab = prefab;
    }
}
