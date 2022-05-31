package renderer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL44.*;
import static org.lwjgl.system.MemoryStack.*;

public class MasterRenderer {
    private static Map<Integer, IRenderer> renderers;

    public enum RendererType {
        Mesh,
        Text,
        Unknown
    };

    public static void init() {
        renderers = new HashMap<>();

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);

        try (MemoryStack stack = stackPush()) {
            IntBuffer flagBuffer = stack.callocInt(1);
            glGetIntegerv(GL_CONTEXT_FLAGS, flagBuffer);
            int flags = flagBuffer.get(0);

            if ((flags & GL_CONTEXT_FLAG_DEBUG_BIT) == GL_CONTEXT_FLAG_DEBUG_BIT) {
                glEnable(GL_DEBUG_OUTPUT);
                glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
                glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> {
                    String sev = switch (severity) {
                        case GL_DEBUG_SEVERITY_HIGH -> "HIGH";
                        case GL_DEBUG_SEVERITY_MEDIUM -> "MEDIUM";
                        case GL_DEBUG_SEVERITY_LOW -> "LOW";
                        case GL_DEBUG_SEVERITY_NOTIFICATION -> "NOTIFICATION";
                        default -> "UNKNOWN";
                    };
                    System.out.println("OpenGL Error [ " + sev + " ]: " + GLDebugMessageCallback.getMessage(length, message));
                }, 0);
                glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DONT_CARE, (IntBuffer)null, true);
            }
        }

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void beginScene() {
        for (IRenderer r : renderers.values())
            r.beginScene();
    }

    public static void endScene() {
        for (IRenderer r : renderers.values())
            r.endScene();
    }

    public static void onWindowResize(float width, float height) {
        for (IRenderer r : renderers.values())
            r.onWindowResize(width, height);
    }

    public static void addRenderer(IRenderer renderer) {
        renderers.put(getType(renderer), renderer);
    }

    public static void drawModel(Model model, Matrix4f transform) {
        if (!renderers.containsKey(RendererType.Mesh.ordinal()))
            throw new RuntimeException("Master Renderer does not contain a mesh renderer");

        MeshRenderer r = (MeshRenderer) renderers.get(RendererType.Mesh.ordinal());
        r.render(model, transform);
    }

    public static void drawText(String text, Vector2f pos, Vector4f color) {
        if (!renderers.containsKey(RendererType.Text.ordinal()))
            throw new RuntimeException("Master Renderer does not contain a text renderer");

        TextRenderer r = (TextRenderer) renderers.get(RendererType.Text.ordinal());
        r.render(text, pos, color);
    }

    private static int getType(IRenderer renderer) {
        return switch (renderer.getClass().getSimpleName()) {
            case "MeshRenderer" -> RendererType.Mesh.ordinal();
            case "TextRenderer" -> RendererType.Text.ordinal();
            default -> RendererType.Unknown.ordinal();
        };
    }
}
