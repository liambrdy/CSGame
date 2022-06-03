package renderer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import shaders.SpriteShader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.sql.Array;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.opengl.GL40.*;

public class SpriteRenderer {
    private static final int MAX_INSTANCES = 1024;
    private static final int INSTANCE_DATA_LENGTH = 9;

    private SpriteShader shader;
    private SpriteSheet sheet;
    private final Vector2f isoX = new Vector2f(), isoY = new Vector2f();
    private Matrix4f projection;

    private static final FloatBuffer spriteBuffer = BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH);
    private int vao, vbo;
    private int pointer;

    List<Map.Entry<Vector2f, Integer>> sprites;

    public SpriteRenderer(SpriteSheet s, Matrix4f ortho) {
        shader = new SpriteShader();
        sheet = s;
        projection = ortho;

        isoX.set(sheet.getSpriteWidth(), 0.5f * sheet.getSpriteHeight());
        isoY.set(-1.0f * sheet.getSpriteWidth(), 0.5f * sheet.getSpriteHeight());

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

            enableVertexAttribute(0, 2, 0);
            enableVertexAttribute(1, 2, 2);
            enableVertexAttribute(2, 2, 4);
            enableVertexAttribute(3, 2, 6);
            enableVertexAttribute(4, 1, 8);
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

    public void render(Vector2f pos, int spriteIndex) {
        Vector2f screen = new Vector2f(pos.x * 0.5f * sheet.getSpriteWidth() + pos.y * -0.5f * sheet.getSpriteHeight(),
                                       pos.x * 0.25f * sheet.getSpriteWidth() + pos.y * 0.25f * sheet.getSpriteHeight());
        sprites.add(new AbstractMap.SimpleEntry<>(screen, spriteIndex));
    }

    public void endScene() {
        pointer = 0;
        float[] data = new float[sprites.size() * INSTANCE_DATA_LENGTH];
        float width = sheet.getSpriteWidth() / 4.0f;
        float height = sheet.getSpriteHeight() / 4.0f;
        for (Map.Entry<Vector2f, Integer> e : sprites) {
            Vector2f pos = e.getKey();
            int index = e.getValue();
            data[pointer++] = pos.x;
            data[pointer++] = pos.y;
            data[pointer++] = pos.x;
            data[pointer++] = pos.y + height;
            data[pointer++] = pos.x + width;
            data[pointer++] = pos.y;
            data[pointer++] = pos.x + width;
            data[pointer++] = pos.y + height;
            data[pointer++] = index;
        }
        spriteBuffer.put(data);
        spriteBuffer.flip();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, (long) 0, spriteBuffer);

        shader.bind();
        shader.setSpriteSheet(sheet);
        shader.setProjection(projection);

        sheet.getSheet().bind();

//        glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, sprites.size());
        glDrawArraysInstanced(GL_TRIANGLE_STRIP, 0, 4, sprites.size());
    }
}
