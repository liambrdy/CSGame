package renderer;

import game.Entity;
import org.joml.*;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.system.MemoryStack;
import shaders.StaticShader;
import shaders.TextShader;

import java.lang.Math;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL44.*;
import static org.lwjgl.system.MemoryStack.*;

public class MasterRenderer {
    private static MeshRenderer meshRenderer;
    private static TextRenderer textRenderer;

    private static StaticShader staticShader;
    private static TextShader textShader;

    private static Matrix4f projection;
    private static Matrix4f textOrtho;

    public static void init(float width, float height) {
        projection = new Matrix4f().perspective((float)Math.toRadians(45.0f), width / height, 0.01f, 1000.0f);
        textOrtho = new Matrix4f().ortho(0.0f, width, height, 0.0f, 0.0f, 10.0f);

        staticShader = new StaticShader();
        meshRenderer = new MeshRenderer(projection, staticShader);

        textShader = new TextShader();
        textRenderer = new TextRenderer(textOrtho, textShader, new Font("c:/windows/fonts/times.ttf"));

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
        textRenderer.beginScene();
    }

    public static void endScene() {
        textRenderer.endScene();
    }

    public static void onWindowResize(float width, float height) {
        projection.perspective((float)Math.toRadians(45.0f), width / height, 0.01f, 1000.0f);
        textOrtho.ortho(0.0f, width, height, 0.0f, 0.0f, 10.0f);
    }

    public static void drawScene(List<Entity> entities, List<Light> lights) {
        staticShader.bind();
        staticShader.setLights(lights);

    }

//    public static void drawModel(Model model, Matrix4f transform) {
//        MeshRenderer r = (MeshRenderer) renderers.get(RendererType.Mesh.ordinal());
//        r.render(model, transform);
//    }
//
//    public static void drawText(String text, Vector2f pos, Vector4f color) {
//        if (!renderers.containsKey(RendererType.Text.ordinal()))
//            throw new RuntimeException("Master Renderer does not contain a text renderer");
//
//        TextRenderer r = (TextRenderer) renderers.get(RendererType.Text.ordinal());
//        r.render(text, pos, color);
//    }
}
