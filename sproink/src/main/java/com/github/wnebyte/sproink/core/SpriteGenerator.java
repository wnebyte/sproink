package com.github.wnebyte.sproink.core;

import com.github.wnebyte.sproink.components.Sprite;

public class SpriteGenerator implements Prefab {

    @Override
    public GameObject generate(Sprite sprite) {
        GameObject go = Scene.createGameObject(sprite, "Sprite_Object_Gen", 0.25f, 0.25f);
        return go;
    }
}
