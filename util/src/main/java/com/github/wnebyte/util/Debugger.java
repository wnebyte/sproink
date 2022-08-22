package com.github.wnebyte.util;

public class Debugger {

    private final long s;

    private float dt;

    public Debugger() {
        this(1);
    }

    public Debugger(long s) {
        this.s = s;
        this.dt = 0.0f;
    }

    public boolean debug(float dt) {
        return debug(this.s, dt);
    }

    public boolean debug(long s, float dt) {
        this.dt += dt;
        if (this.dt >= s) {
            this.dt = 0;
            return true;
        } else {
            return false;
        }
    }
}
