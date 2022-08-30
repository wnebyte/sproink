package com.github.wnebyte.editor.ui;

import java.io.File;
import imgui.ImGui;
import imgui.type.ImInt;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;
import com.github.wnebyte.editor.project.Context;
import com.github.wnebyte.sproink.ui.ImGuiWindow;

public class DirectoryViewWindow extends ImGuiWindow {

    private interface Functor {

        void apply(File file, int depth, int idx);
    }

    private static final String TAG = "DirectoryViewWindow";

    private static final String TITLE = "Directory View";

    private static final int WINDOW_FLAGS = 0;

    public DirectoryViewWindow() {
        this(true);
    }

    public DirectoryViewWindow(boolean visible) {
        this.visible.set(visible);
    }

    @Override
    public void imGui() {
        if (!isVisible()) return;
        Context context = Context.get();
        if (context == null) return;

        ImGui.begin(TITLE, visible, WINDOW_FLAGS);
        ImInt selectionMask = new ImInt((1 << 2));
        ImInt nodeClicked = new ImInt(-1);
        ImGui.pushStyleVar(ImGuiStyleVar.IndentSpacing, ImGui.getFontSize());
        Functor functor = new Functor() {
            @Override
            public void apply(File file, int depth, int idx) {
                for (File child : file.listFiles()) {
                    int flags = ImGuiTreeNodeFlags.OpenOnArrow |
                            ImGuiTreeNodeFlags.OpenOnDoubleClick |
                            ImGuiTreeNodeFlags.SpanAvailWidth |
                            ImGuiTreeNodeFlags.FramePadding |
                            ((selectionMask.get() & (1 << idx)) == 1 ? ImGuiTreeNodeFlags.Selected : 0);
                    String s = child.getName();
                    if (child.isDirectory()) {
                        if (ImGui.treeNodeEx(String.valueOf(idx), flags, s)) {
                            if (ImGui.isItemClicked() || ImGui.isItemFocused()) {
                                nodeClicked.set(idx);
                            }
                            apply(child, depth + 1, ++idx);
                            ImGui.treePop();
                        }
                    } else {
                        flags |= ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.NoTreePushOnOpen;
                        ImGui.treeNodeEx(String.valueOf(idx), flags, s);
                        if (ImGui.isItemClicked() || ImGui.isItemFocused()) {
                            nodeClicked.set(idx);
                        }
                    }
                    ++idx;
                }
                depth -= 1;
            }
        };
        int idx = 0;
        String path = context.getProject().getProjectDir();
        functor.apply(new File(path), 0, idx);
        if (nodeClicked.get() != -1) {
            if (ImGui.getIO().getKeyCtrl()) {
               // selectionMask ^= (1 << nodeClicked.get()); // CTRL+click to toggle
                selectionMask.set(selectionMask.get() ^ (1 << nodeClicked.get()));
            } else {
               // selectionMask = (1 << nodeClicked.get()); // Click to single select
                selectionMask.set((1 << nodeClicked.get()));
            }
        }
        ImGui.popStyleVar();
        ImGui.end();
    }
}
