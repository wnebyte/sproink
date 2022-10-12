package com.github.wnebyte.sproink;

import com.github.wnebyte.sproink.renderer.TextRenderBatch;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL;
import org.lwjgl.glfw.GLFWErrorCallback;
import com.github.wnebyte.sproink.core.Window;
import com.github.wnebyte.sproink.core.Camera;
import com.github.wnebyte.sproink.fonts.SFont;
import com.github.wnebyte.sproink.renderer.Shader;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class FontWindow {

    private long glfwWindow;

    private int width;

    private int height;

    private Camera camera;

    private SFont font;


    public FontWindow() {
        init();
        this.camera = new Camera(new Vector2f(0.0f, 0.0f));
        this.font = new SFont("C:/Windows/Fonts/Arial.ttf", 48);
        this.font.generateBitmap();
    }

    public void run() {
        loop();
        destroy();
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

        Window.Resolution res = Window.getResolution(glfwGetPrimaryMonitor());
        width = res.width;
        height = res.height;

        // Configure GLFW
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the window
        glfwWindow = glfwCreateWindow(width, height, "Font Renderer", NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException(
                    "Failed to create GLFW window."
            );
        }

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
    }

    private void loop() {
        Shader shader = new Shader("C:/Users/Ralle/dev/java/Engine/editor/assets/shaders/font.glsl");
        shader.compile();

        TextRenderBatch renderer = new TextRenderBatch(null);
       // renderer.setShader(shader);
       // renderer.setFont(font);
        renderer.start();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        while (!glfwWindowShouldClose(glfwWindow)) {
            glClear(GL_COLOR_BUFFER_BIT);
            glClearColor(1, 1, 1, 1);

           // renderer.addText("abcdefghiljklmnopqrstuvxy", 0, 100, 1.0f, 0xFF00AB0);
            renderer.flush();

            glfwSwapBuffers(glfwWindow);
            glfwPollEvents();
        }
    }

    private void destroy() {
        // Free the allocated memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        try (GLFWErrorCallback callback = glfwSetErrorCallback(null)) {
            callback.free();
        }
    }

    public Camera getCamera() {
        return camera;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
