package renderer;

import game.Entity;
import org.joml.*;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.system.MemoryStack;
import shaders.StaticShader;
import shaders.TextShader;

import javax.print.attribute.standard.SheetCollate;
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

    private static SpriteRenderer spriteRenderer;
    private static SpriteSheet currentSheet;

    private static LineRenderer lineRenderer;

    private static Matrix4f projection;
    private static Matrix4f ortho;

    private static float width, height;

    public static void init(float w, float h) {
        width = w;
        height = h;

        projection = new Matrix4f().perspective((float)Math.toRadians(45.0f), width / height, 0.01f, 1000.0f);
        ortho = new Matrix4f().ortho(0.0f, width, height, 0.0f, -100.0f, 100.0f);

        staticShader = new StaticShader();
        meshRenderer = new MeshRenderer(projection, staticShader);

        textShader = new TextShader();
        textRenderer = new TextRenderer(ortho, textShader, new Font("c:/windows/fonts/times.ttf"));

        currentSheet = new SpriteSheet("tiles", 10, 11, 110, 3.0f);
        spriteRenderer = new SpriteRenderer(currentSheet, ortho);

        lineRenderer = new LineRenderer(ortho);

//        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
//        glEnable(GL_CULL_FACE);

//        glCullFace(GL_BACK);

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

    public static void enableVertexAttribute(int index, int size, int totalSize, int offset) {
        glEnableVertexAttribArray(index);
        glVertexAttribPointer(index, size, GL_FLOAT, false, totalSize * Float.BYTES, (long) offset * Float.BYTES);
        glVertexAttribDivisor(index, 1);
    }

    public static void beginScene() {
        glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        //meshRenderer.beginScene();
        textRenderer.beginScene();
        spriteRenderer.beginScene();
        lineRenderer.beginScene();
    }

    public static void endScene() {
        spriteRenderer.endScene();
        lineRenderer.endScene();
        textRenderer.endScene();
    }

    public static void onWindowResize(float w, float h) {
        width = w;
        height = h;

        projection.identity();
        ortho.identity();

        projection.perspective((float)Math.toRadians(45.0f), width / height, 0.01f, 1000.0f);
        ortho.ortho(0.0f, width, height, 0.0f, -100.0f, 100.0f);
    }

    public static void drawScene(List<Entity> entities, List<Light> lights, Camera camera) {
        staticShader.bind();
        staticShader.setLights(lights);
        staticShader.setCamera(camera);
        for (Entity e : entities) {
            meshRenderer.render(e);
        }
    }

    public static void drawText(String text, Vector2f pos, Vector4f color) {
        textRenderer.render(text, pos, color);
    }

    public static void drawSprite(Vector2f pos, float height, float x, float y) {
        spriteRenderer.render(pos, height, x, y);
    }

    public static void drawLine(Vector2f p0, Vector2f p1, Vector4f color) {
        lineRenderer.render(p0, p1, color);
    }

    public static void drawIsoLine(Vector2f p0, Vector2f p1, Vector4f color) {
        Vector2f sp0 = currentSheet.toScreen(p0);
        Vector2f sp1 = currentSheet.toScreen(p1);
        sp0.x += currentSheet.getSpriteWidth() * currentSheet.getScale() / 2.0f;
        sp1.x += currentSheet.getSpriteWidth() * currentSheet.getScale() / 2.0f;
        drawLine(sp0, sp1, color);
    }

    public static void drawCoordinateSystem() {
        for (int col = 0; col < 41; col++) {
//            Vector2f p0 = currentSheet.toScreen(new Vector2f(col, 0));
//            Vector2f p1 = currentSheet.toScreen(new Vector2f(col, col));
            drawIsoLine(new Vector2f(col, 0), new Vector2f(col, 40), new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
        }

        for (int row = 0; row < 41; row++) {
//            Vector2f p0 = currentSheet.toScreen(new Vector2f(col, 0));
//            Vector2f p1 = currentSheet.toScreen(new Vector2f(col, col));
            drawIsoLine(new Vector2f(0, row), new Vector2f(40, row), new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
        }
    }

    public static float getWidth() {
        return width;
    }

    public static float getHeight() {
        return height;
    }

    public static SpriteSheet getCurrentSheet() {
        return currentSheet;
    }
}
