package com.github.wnebyte.sproink;

import com.github.wnebyte.sproink.core.window.Window;

public class Main {

    public static void main(String[] args) {
        Window window = Window.get();
        window.destroy();
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
