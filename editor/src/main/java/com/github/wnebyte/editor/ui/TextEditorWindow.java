package com.github.wnebyte.editor.ui;

import imgui.extension.texteditor.TextEditor;
import com.github.wnebyte.editor.project.Context;
import com.github.wnebyte.sproink.ui.ImGuiWindow;

public class TextEditorWindow extends ImGuiWindow {

    private final TextEditor editor;

    /**
     * Constructs a new visible <code>TextEditorWindow</code>.
     */
    public TextEditorWindow() {
        this(true);
    }

    public TextEditorWindow(boolean visible) {
        this.visible.set(visible);
        this.editor = new TextEditor();
    }

    @Override
    public void imGui() {
        if (!isVisible()) return;
        Context context = Context.get();
        if (context == null) return;
    }
}
