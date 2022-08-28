package com.github.wnebyte.sproink.core.window;

import com.github.wnebyte.sproink.observer.event.WindowBeginLoopEvent;
import com.google.gson.Gson;
import org.joml.Vector4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import com.github.wnebyte.sproink.core.event.KeyListener;
import com.github.wnebyte.sproink.core.event.MouseListener;
import com.github.wnebyte.sproink.core.scene.Scene;
import com.github.wnebyte.sproink.core.scene.SceneInitializer;
import com.github.wnebyte.sproink.ui.ImGuiLayer;
import com.github.wnebyte.sproink.util.ResourceFlyWeight;
import com.github.wnebyte.sproink.renderer.*;
import com.github.wnebyte.sproink.observer.EventSystem;
import com.github.wnebyte.sproink.observer.event.WindowInitEvent;
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

    private Window() {
        this.title = "Engine";
    }

    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
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
        glfwSetErrorCallback(null).free();
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

        Resolution resolution = getMonitorResolution();
        width = resolution.width;
        height = resolution.height;

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

        imGuiLayer = new ImGuiLayer(glfwWindow);
        imGuiLayer.init();
    }

    private void loop() {
        float beginTime = (float)glfwGetTime();
        float endTime;
        float dt = -1.0f;

        Shader defaultShader = ResourceFlyWeight.getShader("/shaders/default.glsl");
        Shader pickingShader = ResourceFlyWeight.getShader("/shaders/picking.glsl");

        while (!glfwWindowShouldClose(glfwWindow)) {
            // Poll events
            glfwPollEvents();

            // Render pass 1: Render to picking texture
            glDisable(GL_BLEND);
            pickingTexture.bind();
            glViewport(0, 0, width, height);
            glClearColor(0, 0, 0, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            scene.setShader(pickingShader);
            scene.render();
            pickingTexture.unbind();
            glEnable(GL_BLEND);

            // Render pass 2: Render actual game.
            DebugDraw.beginFrame();
            frameBuffer.bind();
            Vector4f color = scene.getCamera().getClearColor();
            glClearColor(color.x, color.y, color.z, color.w);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0) {
                scene.setShader(defaultShader);
                if (runtimePlaying) {
                    scene.update(dt);
                } else {
                    scene.editorUpdate(dt);
                }
                scene.render();
                DebugDraw.draw();
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

    public static Resolution getMonitorResolution() {
        int width = 0;
        int height = 0;
        int size = width * height;
        int index = -1;

        int i = 0;
        GLFWVidMode.Buffer buffer = glfwGetVideoModes(glfwGetPrimaryMonitor());
        for (GLFWVidMode mode : buffer) {
            int tmpWidth = mode.width();
            int tmpHeight = mode.height();
            int tmpSize = tmpWidth * tmpHeight;
            if (tmpSize > size) {
                width = tmpWidth;
                height = tmpHeight;
                size = tmpSize;
                index = i;
            }
            i++;
        }

        return (index == -1) ?
                new Resolution(DEFAULT_WIDTH, DEFAULT_HEIGHT) :
                new Resolution(width, height);
    }
}
