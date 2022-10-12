package com.github.wnebyte.sproink.core;

import org.joml.Vector4f;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWErrorCallback;
import com.github.wnebyte.sproink.ui.ImGuiLayer;
import com.github.wnebyte.sproink.renderer.*;
import com.github.wnebyte.sproink.observer.EventSystem;
import com.github.wnebyte.sproink.observer.event.WindowInitEvent;
import com.github.wnebyte.sproink.observer.event.WindowBeginLoopEvent;
import com.github.wnebyte.sproink.util.Assets;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

@SuppressWarnings("resource")
public class Window {

    public static class Resolution {

        public final int width, height;

        public Resolution(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    public static final int DEFAULT_WIDTH = 1920;

    public static final int DEFAULT_HEIGHT = 1080;

    private static Window window;

    private int width, height;

    private String title;

    private long glfwWindow;

    private Scene scene;

    private ImGuiLayer imGuiLayer;

    private FrameBuffer frameBuffer;

    private PickingTexture pickingTexture;

    private boolean runtimePlaying;

    private long audioContext;

    private long audioDevice;

    private WindowArgs args;

    private Window(WindowArgs args) {
        this.title = args.title;
        this.args = args;
    }

    public static void initialize(WindowArgs args) {
        if (Window.window != null) {
            throw new IllegalStateException(
                    "Window has already been initialized"
            );
        }
        Window.window = new Window(args);
    }

    public static Window get() {
        if (Window.window == null) {
            throw new IllegalStateException(
                    "Window has not yet been initialized"
            );
        }
        return Window.window;
    }

    public static Scene getScene() {
        return window.scene;
    }

    public static void setScene(String path, SceneInitializer sceneInitializer) {
        if (window.scene != null) {
            window.scene.destroy();
        }
        window.scene = new Scene(path, sceneInitializer);
        window.scene.load();
        window.scene.init();
        window.scene.start();
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion());
        init();
        EventSystem.notify(null, new WindowInitEvent());
        Assets.getFont("C:/Users/ralle/dev/java/Engine/editor/build/install/editor/assets/fonts/super-mario.ttf");
        Window.setScene(args.getScene(), args.getSceneInitializer());
        EventSystem.notify(null, new WindowBeginLoopEvent());
        loop();
        destroy();
    }

    public void destroy() {
        // Destroy the audio context
        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);

        // Free the allocated memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        try (GLFWErrorCallback callback = glfwSetErrorCallback(null)) {
            callback.free();
        }
    }

    private void init() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException(
                    "Unable to initialize GLFW."
            );
        }

        Resolution res = Window.getResolution(glfwGetPrimaryMonitor());
        width = res.width;
        height = res.height;

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the window
        glfwWindow = glfwCreateWindow(width, height, title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException(
                    "Failed to create GLFW window."
            );
        }

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });

        // Make OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // Initialize audio device
        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        audioContext = alcCreateContext(audioDevice, attributes);
        alcMakeContextCurrent(audioContext);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        assert alCapabilities.OpenAL10 : "Audio library not supported";

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        frameBuffer = new FrameBuffer(width, height);
        pickingTexture = new PickingTexture(width, height);
        glViewport(0, 0, width, height);

        imGuiLayer = new ImGuiLayer(glfwWindow, args);
        imGuiLayer.init();
    }

    private void loop() {
        float beginTime = (float)glfwGetTime();
        float endTime;
        float dt = -1.0f;

        String assets = args.getAssetsDir();
        Shader defaultShader = Assets.getShader(assets + "/shaders/default.glsl");
        Shader pickingShader = Assets.getShader(assets + "/shaders/picking.glsl");
        Shader debugShader   = Assets.getShader(assets + "/shaders/debugLine2D.glsl");

        while (!glfwWindowShouldClose(glfwWindow)) {
            // Poll events
            glfwPollEvents();

            // Render pass 1: Render to picking texture
            glDisable(GL_BLEND);
            pickingTexture.bind();
            glViewport(0, 0, width, height);
            glClearColor(0, 0, 0, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            scene.render(pickingShader);
            pickingTexture.unbind();

            // Render pass 2: Render actual game.
            glEnable(GL_BLEND);
            DebugDraw.beginFrame();
            frameBuffer.bind();
            Vector4f color = scene.getCamera().getClearColor();
            glClearColor(color.x, color.y, color.z, color.w);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0) {
                if (runtimePlaying) {
                    scene.update(dt);
                } else {
                    scene.editorUpdate(dt);
                }
                scene.render(defaultShader);
                DebugDraw.draw(debugShader);
            }

            frameBuffer.unbind();
            imGuiLayer.update(dt, scene);
            MouseListener.endFrame();
            KeyListener.endFrame();
            glfwSwapBuffers(glfwWindow);

            endTime = (float)glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

    public static int getWidth() {
        return window.width;
    }

    public static int getHeight() {
        return window.height;
    }

    public static void setWidth(int width) {
        window.width = width;
    }

    public static void setHeight(int height) {
        window.height = height;
    }

    public static void setTitle(String title) {
        window.title = title;
        glfwSetWindowTitle(window.glfwWindow, title);
    }

    public static FrameBuffer getFrameBuffer() {
        return window.frameBuffer;
    }

    public static PickingTexture getPickingTexture() {
        return window.pickingTexture;
    }

    public static float getTargetAspectRatio() {
         return 16.0f / 9.0f;
    }

    public static ImGuiLayer getImGuiLayer() {
        return window.imGuiLayer;
    }

    public static void setRuntimePlaying(boolean value) {
        window.runtimePlaying = value;
    }

    public static Resolution getResolution() {
        return getResolution(glfwGetPrimaryMonitor());
    }

    public static Resolution getResolution(long monitor) {
        int width = 0;
        int height = 0;
        int size = width * height;
        boolean def = false;

        GLFWVidMode.Buffer buffer = glfwGetVideoModes(monitor);
        for (GLFWVidMode mode : buffer) {
            int tmpWidth = mode.width();
            int tmpHeight = mode.height();
            int tmpSize = tmpWidth * tmpHeight;
            if (tmpSize > size) {
                width = tmpWidth;
                height = tmpHeight;
                size = tmpSize;
                def = true;
            }
        }

        return def ? new Resolution(width, height) : new Resolution(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}
