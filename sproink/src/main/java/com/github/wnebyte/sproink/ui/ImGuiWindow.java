package com.github.wnebyte.sproink.ui;

import imgui.type.ImBoolean;

public abstract class ImGuiWindow {

    protected final ImBoolean visible;

    protected boolean locked;

    public ImGuiWindow() {
        this.visible = new ImBoolean(true);
        this.locked = false;
    }

    public boolean isVisible() {
        return visible.get();
    }

    public void show() {
        visible.set(true);
    }

    public void hide() {
        visible.set(false);
    }

    public void toggleVisibility() {
        visible.set(!visible.get());
    }

    public boolean isLocked() {
        return locked;
    }

    public void lock() {
        locked = true;
    }

    public void unlock() {
        locked = false;
    }

    public boolean isModal() {
        return false;
    }

    public abstract void imGui();
}
