package com.github.wnebyte.sproink.core;

import com.github.wnebyte.sproink.core.window.Window;
import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.components.Sprite;
import com.github.wnebyte.sproink.components.SpriteRenderer;

public class DefaultPrefab implements Prefab {

    public DefaultPrefab() {}

    @Override
    public GameObject generate(Sprite sprite) {
        GameObject go = Window.getScene().createGameObject("Sprite_Object_Gen");
        go.transform.scale.x = 0.25f;
        go.transform.scale.y = 0.25f;
        SpriteRenderer spr = new SpriteRenderer();
        spr.setSprite(sprite);
        go.addComponent(spr);
        return go;
    }
}
