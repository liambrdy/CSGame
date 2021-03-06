package renderer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import shaders.LineShader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.opengl.GL40.*;

public class LineRenderer {
    public class LineEntry {
        private Vector2f p0, p1;
        private Vector4f color;
        private Vector2f offset;

        public LineEntry(Vector2f point0, Vector2f point1, Vector4f c, Vector2f o) {
            p0 = point0;
            p1 = point1;
            color = c;
            offset = o;
        }
    };

    LineShader shader;

    private static final int MAX_INSTANCES = 1024;
    private static final int INSTANCE_DATA_LENGTH = 12;

    private static final FloatBuffer buffer = BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH);
    private int pointer;

    private int vao, vbo;

    private List<LineEntry> lines = new ArrayList<>();

    private Matrix4f projection;
    private Vector2f offset;

    public LineRenderer(Matrix4f proj) {
        projection = proj;

        shader = new LineShader();

        offset = new Vector2f();

        glLineWidth(1.5f);

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

            MasterRenderer.enableVertexAttribute(0, 2, INSTANCE_DATA_LENGTH, 0);
            MasterRenderer.enableVertexAttribute(1, 2, INSTANCE_DATA_LENGTH, 2);
            MasterRenderer.enableVertexAttribute(2, 4, INSTANCE_DATA_LENGTH, 4);
            MasterRenderer.enableVertexAttribute(3, 4, INSTANCE_DATA_LENGTH, 8);
        }

        pointer = 0;
    }

    public void render(Vector2f p0, Vector2f p1, Vector4f color) {
        lines.add(new LineEntry(p0, p1, color, new Vector2f(0.0f)));
    }

    public void render(Vector2f p0, Vector2f p1, Vector4f color, Vector2f offset) {
        lines.add(new LineEntry(p0, p1, color, offset));
    }

    public void beginScene() {
        lines.clear();
    }

    public void endScene() {
        pointer = 0;
        float[] data = new float[lines.size() * INSTANCE_DATA_LENGTH];
        for (LineEntry e : lines) {
            Vector2f p0 = new Vector2f(e.p0).add(e.offset);
            Vector2f p1 = new Vector2f(e.p1).add(e.offset);

            data[pointer++] = p0.x;
            data[pointer++] = p0.y;
            data[pointer++] = p1.x;
            data[pointer++] = p1.y;
            data[pointer++] = e.color.x;
            data[pointer++] = e.color.y;
            data[pointer++] = e.color.z;
            data[pointer++] = e.color.w;
            data[pointer++] = e.color.x;
            data[pointer++] = e.color.y;
            data[pointer++] = e.color.z;
            data[pointer++] = e.color.w;
        }
//        buffer.put(data);
//        buffer.flip();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, data);

        shader.bind();
        shader.setProjection(projection);

        glDrawArraysInstanced(GL_LINES, 0, 2, lines.size());
    }

    public void setOffset(Vector2f offset) {
        this.offset = offset;
    }
}
