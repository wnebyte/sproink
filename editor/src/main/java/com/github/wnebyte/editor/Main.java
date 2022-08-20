package com.github.wnebyte.editor;

import com.github.wnebyte.editor.observer.WindowObserver;
import com.github.wnebyte.editor.observer.WindowInitializedObserver;
import com.github.wnebyte.sproink.core.window.Window;
import com.github.wnebyte.sproink.observer.EventSystem;

public class Main {

    public static void main(String[] args) {
        EventSystem.addObserver(new WindowObserver());
        EventSystem.addObserver(new WindowInitializedObserver());
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
