package com.github.wnebyte.sproink.core;

import com.github.wnebyte.sproink.core.scene.Scene;
import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.components.Sprite;
import com.github.wnebyte.sproink.components.SpriteRenderer;

public class SpritePrefab implements Prefab {

    @Override
    public GameObject generate(Sprite sprite) {
        GameObject go = Scene.createGameObject("Sprite_Object_Gen");
        go.transform.scale.x = 0.25f;
        go.transform.scale.y = 0.25f;
        SpriteRenderer spr = new SpriteRenderer();
        spr.setSprite(sprite);
        go.addComponent(spr);
        return go;
    }
}
