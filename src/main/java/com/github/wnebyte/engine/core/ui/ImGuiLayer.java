package com.github.wnebyte.engine.core.ui;

import java.util.List;
import imgui.*;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import com.github.wnebyte.engine.core.window.Window;
import com.github.wnebyte.engine.core.scene.Scene;
import com.github.wnebyte.engine.core.event.KeyListener;
import com.github.wnebyte.engine.core.event.MouseListener;
import com.github.wnebyte.engine.renderer.PickingTexture;
import com.github.wnebyte.engine.editor.*;
import com.github.wnebyte.engine.util.ResourceUtil;
import com.github.wnebyte.engine.util.WindowRegistry;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30C.GL_FRAMEBUFFER;

@SuppressWarnings("resource")
public class ImGuiLayer {

    private final long glfwWindow;

    // LWJGL3 renderer (SHOULD be initialized)
    private final ImGuiImplGl3 imGuiGl3;

    private final ImGuiImplGlfw imGuiGlfw;

    private final MenuBar menuBar;

    private final WindowRegistry windows;

    public ImGuiLayer(long glfwWindow, PickingTexture pickingTexture) {
        this.glfwWindow = glfwWindow;
        this.imGuiGl3 = new ImGuiImplGl3();
        this.imGuiGlfw = new ImGuiImplGlfw();
        this.menuBar = new MenuBar();
        this.windows = new WindowRegistry();
        this.windows.addWindow(new GameViewWindow());
        this.windows.addWindow(new ConsoleWindow());
        this.windows.addWindow(new PropertiesWindow(pickingTexture));
        this.windows.addWindow(new SceneHierarchyWindow());
        ImGuiWindow w = new NewProjectWindow();
        w.hide();
        this.windows.addWindow(w);
    }

    public void init() {
        // IMPORTANT!!
        // This line is critical for Dear ImGui to work.
        ImGui.createContext();

        // ------------------------------------------------------------
        // Initialize ImGuiIO config
        final ImGuiIO io = ImGui.getIO();

        io.setIniFilename("imgui.ini");
        io.setConfigFlags(ImGuiConfigFlags.DockingEnable);
        /*
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
         */
        io.setBackendPlatformName("imgui_java_impl_glfw");

        // ------------------------------------------------------------
        // GLFW callbacks to handle user input

        glfwSetKeyCallback(glfwWindow, (w, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                io.setKeysDown(key, true);
            } else if (action == GLFW_RELEASE) {
                io.setKeysDown(key, false);
            }

            io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
            io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
            io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
            io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));

            if (!io.getWantCaptureKeyboard()) {
                KeyListener.keyCallback(w, key, scancode, action, mods);
            }
        });

        glfwSetCharCallback(glfwWindow, (w, c) -> {
            if (c != GLFW_KEY_DELETE) {
                io.addInputCharacter(c);
            }
        });

        glfwSetMouseButtonCallback(glfwWindow, (w, button, action, mods) -> {
            final boolean[] mouseDown = new boolean[5];

            mouseDown[0] = button == GLFW_MOUSE_BUTTON_1 && action != GLFW_RELEASE;
            mouseDown[1] = button == GLFW_MOUSE_BUTTON_2 && action != GLFW_RELEASE;
            mouseDown[2] = button == GLFW_MOUSE_BUTTON_3 && action != GLFW_RELEASE;
            mouseDown[3] = button == GLFW_MOUSE_BUTTON_4 && action != GLFW_RELEASE;
            mouseDown[4] = button == GLFW_MOUSE_BUTTON_5 && action != GLFW_RELEASE;

            io.setMouseDown(mouseDown);

            if (!io.getWantCaptureMouse() && mouseDown[1]) {
                ImGui.setWindowFocus(null);
            }

            if (!io.getWantCaptureMouse() || getWindow(GameViewWindow.class).getWantCaptureMouse()) {
                MouseListener.mouseButtonCallback(w, button, action, mods);
            }
        });

        glfwSetScrollCallback(glfwWindow, (w, xOffset, yOffset) -> {
            io.setMouseWheelH(io.getMouseWheelH() + (float) xOffset);
            io.setMouseWheel(io.getMouseWheel() + (float) yOffset);
            if (!io.getWantCaptureMouse() || getWindow(GameViewWindow.class).getWantCaptureMouse()) {
                MouseListener.mouseScrollCallback(w, xOffset, yOffset);
            } else {
                MouseListener.clear();
            }
        });

        io.setSetClipboardTextFn(new ImStrConsumer() {
            @Override
            public void accept(final String s) {
                glfwSetClipboardString(glfwWindow, s);
            }
        });

        io.setGetClipboardTextFn(new ImStrSupplier() {
            @Override
            public String get() {
                final String clipboardString = glfwGetClipboardString(glfwWindow);
                if (clipboardString != null) {
                    return clipboardString;
                } else {
                    return "";
                }
            }
        });

        // ------------------------------------------------------------
        // Fonts configuration
        // Read: https://raw.githubusercontent.com/ocornut/imgui/master/docs/FONTS.txt

        final ImFontAtlas fontAtlas = io.getFonts();
        final ImFontConfig fontConfig = new ImFontConfig(); // Natively allocated object, should be explicitly destroyed

        // Glyphs could be added per-font as well as per config used globally like here
        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());

        fontConfig.setPixelSnapH(true);
        // segoeui
        fontAtlas.addFontFromFileTTF(ResourceUtil.getAbsolutePath("/fonts/segoeui.ttf"),
                18, fontConfig);

        fontConfig.destroy(); // After all fonts were added we don't need this config more

        // Method initializes LWJGL3 renderer.
        // This method SHOULD be called after you've initialized your ImGui configuration (fonts and so on).
        // ImGui context should be created as well.
        imGuiGlfw.init(glfwWindow, false);
        imGuiGl3.init("#version 330 core");
    }

    public void update(float dt, Scene scene) {
        startFrame();
        setupDockSpace();
        scene.imGui();
        getAllWindows().forEach(ImGuiWindow::imGui);
        endFrame();
    }

    private void startFrame() {
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    private void endFrame() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, Window.getWidth(), Window.getHeight());
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        // After Dear ImGui prepared a draw data, we use it in the LWJGL3 renderer.
        // At that moment ImGui will be rendered to the current OpenGL context.
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        /*
        long backupWindowPointer = glfwGetCurrentContext();
        ImGui.updatePlatformWindows();
        ImGui.renderPlatformWindowsDefault();
        glfwMakeContextCurrent(backupWindowPointer);
         */
    }

    // If you want to clean a room after yourself - do it by yourself
    private void destroyImGui() {
        imGuiGl3.dispose();
        ImGui.destroyContext();
    }

    private void setupDockSpace() {
        /*
        ImGuiViewport mainViewport = ImGui.getMainViewport();
        ImGui.setNextWindowPos(mainViewport.getWorkPosX(), mainViewport.getWorkPosY());
        ImGui.setNextWindowSize(mainViewport.getWorkSizeX(), mainViewport.getWorkSizeY());
        ImGui.setNextWindowViewport(mainViewport.getID());
         */

        ImGui.setNextWindowPos(0.0f, 0.0f);
        ImGui.setNextWindowSize(Window.getWidth(), Window.getHeight());
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        int windowFlags = ImGuiWindowFlags.MenuBar |
                ImGuiWindowFlags.NoDocking |
                ImGuiWindowFlags.NoTitleBar |
                ImGuiWindowFlags.NoCollapse |
                ImGuiWindowFlags.NoResize |
                ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoBringToFrontOnFocus |
                ImGuiWindowFlags.NoNavFocus ;

        ImGui.begin("Dockspace Demo", new ImBoolean(true), windowFlags);
        ImGui.popStyleVar(2);

        // Dockspace
        ImGui.dockSpace(ImGui.getID("Dockspace"));
        menuBar.imGui();
        ImGui.end();
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    public <T extends ImGuiWindow> T getWindow(Class<T> windowClass) {
        return windows.getWindow(windowClass);
    }

    public <T extends ImGuiWindow>  void addWindow(T window) {
        windows.addWindow(window);
    }

    public <T extends ImGuiWindow> void removeWindow(Class<T> windowClass) {
        windows.removeWindow(windowClass);
    }

    public List<ImGuiWindow> getAllWindows() {
        return windows.getAllWindows();
    }
}
