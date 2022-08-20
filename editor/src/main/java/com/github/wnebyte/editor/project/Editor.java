package com.github.wnebyte.editor.project;

import javax.xml.bind.annotation.*;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public final class Editor {

    @XmlElement
    private Assets assets;

    public Editor() {}

    public Editor(Assets assets) {
        this.assets = assets;
    }

    public void setAssets(Assets assets) {
        this.assets = assets;
    }

    public Assets getAssets() {
        return assets;
    }
}