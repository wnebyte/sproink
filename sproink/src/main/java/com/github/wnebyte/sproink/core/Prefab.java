package com.github.wnebyte.sproink.core;

import com.github.wnebyte.sproink.components.Sprite;
import com.github.wnebyte.sproink.core.ecs.GameObject;

public interface Prefab {

    GameObject generate(Sprite sprite);
}
