package com.github.wnebyte.editor.project;

import javax.xml.bind.annotation.*;

@XmlType(name = "sound")
@XmlAccessorType(XmlAccessType.FIELD)
public final class SoundAsset {

    @XmlAttribute
    private String src;

    @XmlAttribute
    private boolean loops;

    public SoundAsset() {}

    public SoundAsset(String src, boolean loops) {
        this.src = src;
        this.loops = loops;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public boolean loops() {
        return loops;
    }

    public void setLoops(boolean loops) {
        this.loops = loops;
    }

}
