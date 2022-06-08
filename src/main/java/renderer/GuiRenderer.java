package renderer;

import assets.AssetManager;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import shaders.RectShader;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class GuiRenderer {
    public class RectEntry {
        private Vector4f rect;
        private Vector4f[] colors;
        private Texture texture;

        public RectEntry(Vector4f r, Vector4f c0, Vector4f c1, Vector4f c2, Vector4f c3, Texture t) {
            rect = r;
            colors = new Vector4f[4];
            colors[0] = c0;
            colors[1] = c1;
            colors[2] = c2;
            colors[3] = c3;
            texture = t;
        }

        public RectEntry(Vector4f r, Vector4f c, Texture t) {
            this(r, c, c, c, c, t);
        }

        public RectEntry(Vector4f r, Vector4f c) {
            this(r, c, AssetManager.getDefaultTexture());
        }
    };

    private RectShader shader;

    private Matrix4f projection;

    private List<RectEntry> rects;

    private static final int MAX_INSTANCES = 1024;
    private static final int INSTANCE_DATA_LENGTH = 24;

    private int vao, vbo;
    private int pointer;



    public GuiRenderer(Matrix4f ortho) {
        projection = ortho;

        shader = new RectShader();

        rects = new ArrayList<>();
        pointer = 0;

        try (MemoryStack stack = stackPush()) {
            IntBuffer vaoBuffer = stack.callocInt(1);
            IntBuffer vboBuffer = stack.callocInt(1);

            glGenVertexArrays(vaoBuffer);
            vao = vaoBuffer.get(0);
            glBindVertexArray(vao);

            glGenBuffers(vboBuffer);
            vbo = vboBuffer.get(0);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, MAX_INSTANCES * INSTANCE_DATA_LENGTH * Float.BYTES, GL_DYNAMIC_DRAW);

            MasterRenderer.enableVertexAttribute(0, 2, INSTANCE_DATA_LENGTH,0); // v0
            MasterRenderer.enableVertexAttribute(1, 2, INSTANCE_DATA_LENGTH, 2); // v1
            MasterRenderer.enableVertexAttribute(2, 2, INSTANCE_DATA_LENGTH, 4); // v2
            MasterRenderer.enableVertexAttribute(3, 2, INSTANCE_DATA_LENGTH, 6); // v3
            MasterRenderer.enableVertexAttribute(4, 4, INSTANCE_DATA_LENGTH, 8); // t0
            MasterRenderer.enableVertexAttribute(5, 4, INSTANCE_DATA_LENGTH,12); // t1
            MasterRenderer.enableVertexAttribute(6, 4, INSTANCE_DATA_LENGTH,16); // t2
            MasterRenderer.enableVertexAttribute(7, 4, INSTANCE_DATA_LENGTH,20); // t3
        }
    }

    public void render(Vector4f rect, Vector4f color) {
        rects.add(new RectEntry(rect, color));
    }

    public void render(Vector4f rect, Vector4f color, Texture t) {
        rects.add(new RectEntry(rect, color, t));
    }

    public void beginScene() {
        rects.clear();
    }

    public void endScene() {
        float[] data = new float[INSTANCE_DATA_LENGTH * rects.size()];
        pointer = 0;
        int offset = 0;
        Texture last = null;
        for (int j = offset; j < rects.size(); j++) {
            RectEntry e = rects.get(j);
            if (last == null)
                last = e.texture;

            if (e.texture != last) {
                glBindVertexArray(vao);
                glBindBuffer(GL_ARRAY_BUFFER, vbo);
                glBufferSubData(GL_ARRAY_BUFFER, 0, data);

                shader.bind();
                shader.setProjection(projection);
                shader.setTexture();

                glDrawArraysInstanced(GL_TRIANGLE_STRIP, 0, 6, pointer / INSTANCE_DATA_LENGTH);
                offset += pointer / INSTANCE_DATA_LENGTH;
                pointer = 0;
            }

            data[pointer++] = e.rect.x;
            data[pointer++] = e.rect.y;
            data[pointer++] = e.rect.x;
            data[pointer++] = e.rect.y + e.rect.w;
            data[pointer++] = e.rect.x + e.rect.z;
            data[pointer++] = e.rect.y;
            data[pointer++] = e.rect.x + e.rect.z;
            data[pointer++] = e.rect.y + e.rect.w;
            for (int i = 0; i < 4; i++) {
                data[pointer++] = e.colors[i].x;
                data[pointer++] = e.colors[i].y;
                data[pointer++] = e.colors[i].z;
                data[pointer++] = e.colors[i].w;
            }
        }

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, data);

        shader.bind();
        shader.setProjection(projection);
        shader.setTexture();

        glDrawArraysInstanced(GL_TRIANGLE_STRIP, 0, 6, pointer / INSTANCE_DATA_LENGTH);
    }
}
