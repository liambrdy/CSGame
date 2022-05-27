package core;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.opengl.GL11.*;

public class Window {
    private final long handle;

    private final int width, height;

    public Window(int width, int height, String title) {
        this.width = width;
        this.height = height;

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_TRUE);

        handle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (handle == NULL)
            throw new RuntimeException("Failed to create GLFW window");

        glfwMakeContextCurrent(handle);
        glfwSwapInterval(1);

        glfwSetWindowSizeCallback(handle, (window, w, h) -> {
            glViewport(0, 0, w, h);
        });

        GL.createCapabilities();
    }

    public void update() {
        glfwSwapBuffers(handle);
        glfwPollEvents();
    }

    public boolean shouldClose() { return glfwWindowShouldClose(handle); }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public long getHandle() { return handle; }
}
