package com.github.wnebyte.editor.project;

import java.util.List;
import javax.xml.bind.annotation.*;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public final class Tab {

    @XmlAttribute
    private String name;

    @XmlElement(name = "spritesheet")
    private List<Spritesheet> spritesheets;

    public Tab() {}

    public Tab(String name, List<Spritesheet> spritesheets) {
        this.name = name;
        this.spritesheets = spritesheets;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSpritesheets(List<Spritesheet> spritesheets) {
        this.spritesheets = spritesheets;
    }

    public List<Spritesheet> getSpritesheets() {
        return spritesheets;
    }

    public void addSpritesheet(Spritesheet spritesheet) {
        spritesheets.add(spritesheet);
    }
}
