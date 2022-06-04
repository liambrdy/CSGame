package renderer;

import org.joml.Matrix2f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import shaders.SpriteShader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.sql.Array;
import java.util.*;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.opengl.GL40.*;

public class SpriteRenderer {
    public class SpriteEntry {
        private Vector2f position;
        private float texX, texY;
        private float size;
        private float height;

        public SpriteEntry(Vector2f p, float h, float s, float x, float y) {
            position = p;
            height = h;
            size = s;
            texX = x;
            texY = y;
        }
    };

    public class SpriteComparator implements Comparator<SpriteEntry> {
        @Override
        public int compare(SpriteEntry o1, SpriteEntry o2) {
            Float first = o1.position.y;
            Float second = o2.position.y;
            return first.compareTo(second);
        }
    }
    private static final int MAX_INSTANCES = 1024;
    private static final int INSTANCE_DATA_LENGTH = 16;

    private SpriteShader shader;
    private SpriteSheet sheet;
    private Matrix4f projection;

    private static final FloatBuffer spriteBuffer = BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH);
    private int vao, vbo;
    private int pointer;

    List<SpriteEntry> sprites;

    public SpriteRenderer(SpriteSheet s, Matrix4f ortho) {
        shader = new SpriteShader();
        sheet = s;
        projection = ortho;

        sprites = new ArrayList<>();

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

            enableVertexAttribute(0, 2, 0); // v0
            enableVertexAttribute(1, 2, 2); // v1
            enableVertexAttribute(2, 2, 4); // v2
            enableVertexAttribute(3, 2, 6); // v3
            enableVertexAttribute(4, 2, 8); // t0
            enableVertexAttribute(5, 2, 10); // t1
            enableVertexAttribute(6, 2, 12); // t2
            enableVertexAttribute(7, 2, 14); // t3
        }
    }

    private void enableVertexAttribute(int index, int size, int offset) {
        glEnableVertexAttribArray(index);
        glVertexAttribPointer(index, size, GL_FLOAT, false, INSTANCE_DATA_LENGTH * Float.BYTES, (long) offset * Float.BYTES);
        glVertexAttribDivisor(index, 1);
    }

    public void beginScene() {
        sprites.clear();
    }

    public void render(Vector2f pos, float height, float size, float x, float y) {
//        Matrix2f m = new Matrix2f(0.5f * 32.0f, 0.25f * 32.0f, -0.5f * 32.0f, 0.25f * 32.0f);
//        Vector2f screen = pos.mul(m);
        Vector2f screen = new Vector2f(pos.x * 0.5f * sheet.getSpriteWidth() * size + pos.y * -0.5f * sheet.getSpriteHeight() * size,
                                       pos.x * 0.25f * sheet.getSpriteWidth() * size + pos.y * 0.25f * sheet.getSpriteHeight() * size);
        screen.x -= sheet.getSpriteWidth() * size / 2.0f;
        screen.x += MasterRenderer.getWidth() / 2.0f;

        sprites.add(new SpriteEntry(screen, height, size, x, y));
    }

    public void endScene() {
        pointer = 0;
        float[] data = new float[sprites.size() * INSTANCE_DATA_LENGTH];

        float sheetWidth = sheet.getSheet().getWidth();
        float sheetHeight = sheet.getSheet().getHeight();

        Collections.sort(sprites, new SpriteComparator());
        for (SpriteEntry s : sprites) {
            float width = sheet.getSpriteWidth();
            float height = sheet.getSpriteHeight();
            Vector2f pos = s.position;
            pos.y -= s.height;

            data[pointer++] = pos.x;
            data[pointer++] = pos.y;
            data[pointer++] = pos.x;
            data[pointer++] = pos.y + height * s.size;
            data[pointer++] = pos.x + width * s.size;
            data[pointer++] = pos.y;
            data[pointer++] = pos.x + width * s.size;
            data[pointer++] = pos.y + height * s.size;
            data[pointer++] = (s.texX * width) / sheetWidth;
            data[pointer++] = ((s.texY + 1) * height) / sheetHeight;
            data[pointer++] = (s.texX * width) / sheetWidth;
            data[pointer++] = (s.texY * height) / sheetHeight;
            data[pointer++] = ((s.texX + 1) * width) / sheetWidth;
            data[pointer++] = ((s.texY + 1) * height) / sheetHeight;
            data[pointer++] = ((s.texX + 1) * width) / sheetWidth;
            data[pointer++] = (s.texY * height) / sheetHeight;
        }
        spriteBuffer.put(data);
        spriteBuffer.flip();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, (long) 0, spriteBuffer);

        shader.bind();
        shader.setSpriteSheet();
        shader.setProjection(projection);

        sheet.getSheet().bind();

//        glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, sprites.size());
        glDrawArraysInstanced(GL_TRIANGLE_STRIP, 0, 4, sprites.size());
    }
}
