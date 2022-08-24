package com.github.wnebyte.editor.project;

import java.util.List;
import javax.xml.bind.annotation.*;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public final class Tab {

    @XmlAttribute
    private String name;

    @XmlElement(name = "spritesheet")
    private List<SpritesheetAsset> spritesheets;

    @XmlElement(name = "sound")
    private List<SoundAsset> sounds;

    public Tab() {}

    public Tab(String name, List<SpritesheetAsset> spritesheets, List<SoundAsset> sounds) {
        this.name = name;
        this.spritesheets = spritesheets;
        this.sounds = sounds;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSpritesheets(List<SpritesheetAsset> spritesheets) {
        this.spritesheets = spritesheets;
    }

    public List<SpritesheetAsset> getSpritesheets() {
        return spritesheets;
    }

    public void addSpritesheet(SpritesheetAsset spritesheet) {
        spritesheets.add(spritesheet);
    }

    public List<SoundAsset> getSounds() {
        return sounds;
    }

    public void setSounds(List<SoundAsset> sounds) {
        this.sounds = sounds;
    }

    public void addSound(SoundAsset sound) {
        sounds.add(sound);
    }
}
