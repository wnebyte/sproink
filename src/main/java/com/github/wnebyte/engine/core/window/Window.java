package com.github.wnebyte.engine.core.window;

import com.github.wnebyte.engine.renderer.*;
import com.github.wnebyte.engine.util.ResourceFlyWeight;
import imgui.ImGui;
import org.joml.Vector4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import com.github.wnebyte.engine.core.event.KeyListener;
import com.github.wnebyte.engine.core.event.MouseListener;
import com.github.wnebyte.engine.core.scene.LevelEditorScene;
import com.github.wnebyte.engine.core.scene.LevelScene;
import com.github.wnebyte.engine.core.scene.Scene;
import com.github.wnebyte.engine.core.ui.ImGuiLayer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    public static final int DEFAULT_WIDTH = 1920;

    public static final int DEFAULT_HEIGHT = 1080;

    private static Window window = null;

    private int width, height;

    private String title;

    private long glfwWindow;

    private Scene scene = null;

    private ImGuiLayer imGuiLayer;

    private FrameBuffer frameBuffer;

    private PickingTexture pickingTexture;

    private float r = 1.0f;

    private float g = 1.0f;

    private float b = 1.0f;

    private float a = 1.0f;

    private Window() {
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        this.title = "Engine";
    }

    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }
        return Window.window;
    }

    public static Scene getScene() {
        return get().scene;
    }

    public static void setScene(int newScene) {
        switch (newScene) {
            case 0:
                window.scene = new LevelEditorScene();
                window.scene.load();
                window.scene.init();
                window.scene.start();
                break;
            case 1:
                window.scene = new LevelScene();
                window.scene.load();
                window.scene.init();
                window.scene.start();
                break;
            default:
                assert false : "Unknown scene '" + newScene + "'";
                break;
        }
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion());

        init();
        loop();

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

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
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
            glViewport(0, 0, newWidth, newHeight);
        });

        // Make OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        frameBuffer = new FrameBuffer(width, height); // same di as pickingTexture
        pickingTexture = new PickingTexture(width, height);
        glViewport(0, 0, width, height);

        imGuiLayer = new ImGuiLayer(glfwWindow, pickingTexture);
        imGuiLayer.init();

        setScene(0);
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

            // Render pass 1. Render to picking texture.
            glDisable(GL_BLEND);
            pickingTexture.enableWriting();
            glViewport(0, 0, width, height);
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Renderer.bindShader(pickingShader);
            scene.render();

            pickingTexture.disableWriting();
            glEnable(GL_BLEND);

            // Render pass 2. Render actual game.
            DebugDraw.beginFrame();
            frameBuffer.bind();
            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0) {
                DebugDraw.draw();
                Renderer.bindShader(defaultShader);
                scene.update(dt);
                scene.render();
            }
            frameBuffer.unbind();

            imGuiLayer.update(dt, scene);
            glfwSwapBuffers(glfwWindow);
            MouseListener.endFrame();

            endTime = (float)glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }

        scene.saveExit();

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

    public static FrameBuffer getFrameBuffer() {
        return window.frameBuffer;
    }

    public static float getTargetAspectRatio() {
         return 16.0f / 9.0f;
    }

    public static ImGuiLayer getImGuiLayer() {
        return get().imGuiLayer;
    }
}
