package com.github.wnebyte.engine;

import com.github.wnebyte.engine.core.window.Window;

/*
Coding a 2D Game Engine #44
 */
public class Main {

    public static void main(String[] args) {
        Window window = Window.get();
        window.run();
    }

    /*
    TEXTURE COORDINATES:
    0,1     1,1
    +---------+
    |         |
    |         |
    +---------+
    0,0     1,0
     */
}
