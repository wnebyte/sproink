package com.github.wnebyte.editor.util;

import com.github.wnebyte.sproink.core.Prefab;
import com.github.wnebyte.sproink.core.window.Window;
import com.github.wnebyte.sproink.core.ecs.GameObject;
import com.github.wnebyte.sproink.components.Sprite;
import com.github.wnebyte.sproink.components.SpriteRenderer;

public class SpritePrefab implements Prefab {

    @Override
    public GameObject generate(Sprite sprite) {
        GameObject go = Window.getScene().createGameObject("Sprite_Object_Gen");
        go.transform.scale.x = Settings.GRID_WIDTH;
        go.transform.scale.y = Settings.GRID_HEIGHT;
        SpriteRenderer spr = new SpriteRenderer();
        spr.setSprite(sprite);
        go.addComponent(spr);
        return go;
    }
}
