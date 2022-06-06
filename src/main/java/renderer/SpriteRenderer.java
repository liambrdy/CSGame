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
        private float height;

        public SpriteEntry(Vector2f p, float h, float x, float y) {
            position = p;
            height = h;
            texX = x;
            texY = y;
        }

        public Vector2f getPosition() {
            return position;
        }

        public float getTexX() {
            return texX;
        }

        public float getTexY() {
            return texY;
        }

        public float getHeight() {
            return height;
        }
        
        public void setHeight(float h) {
            height = h;
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
    private int pointer, count;

    List<SpriteEntry> sprites;
    List<ArrayList<SpriteEntry>> renderList;

    public SpriteRenderer(SpriteSheet s, Matrix4f ortho) {
        shader = new SpriteShader();
        sheet = s;
        projection = ortho;

        sprites = new ArrayList<>();
        renderList = new ArrayList<>();

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
            MasterRenderer.enableVertexAttribute(4, 2, INSTANCE_DATA_LENGTH, 8); // t0
            MasterRenderer.enableVertexAttribute(5, 2, INSTANCE_DATA_LENGTH,10); // t1
            MasterRenderer.enableVertexAttribute(6, 2, INSTANCE_DATA_LENGTH,12); // t2
            MasterRenderer.enableVertexAttribute(7, 2, INSTANCE_DATA_LENGTH,14); // t3
        }
    }

    public void beginScene() {
        renderList.clear();
        sprites.clear();
        count = 0;
    }

    public void render(Vector2f pos, float height, float x, float y) {
//        Matrix2f m = new Matrix2f(0.5f * 32.0f, 0.25f * 32.0f, -0.5f * 32.0f, 0.25f * 32.0f);
//        Vector2f screen = pos.mul(m);
//        Vector2f screen = new Vector2f(pos.x * 0.5f * sheet.getSpriteWidth() * sheet.getScale() + pos.y * -0.5f * sheet.getSpriteHeight() * sheet.getScale(),
//                                       pos.x * 0.25f * sheet.getSpriteWidth() * sheet.getScale() + pos.y * 0.25f * sheet.getSpriteHeight() * sheet.getScale());
        if (count >= MAX_INSTANCES)
            beginNewList();
        Vector2f screen = sheet.toScreen(pos);

        sprites.add(new SpriteEntry(screen, height, x, y));
        count++;
    }

    private void beginNewList() {
        count = 0;
        renderList.add(new ArrayList<>(sprites));
        sprites.clear();
    }

    public void endScene() {
        float sheetWidth = sheet.getSheet().getWidth();
        float sheetHeight = sheet.getSheet().getHeight();

        if (!sprites.isEmpty())
            renderList.add(new ArrayList<>(sprites));

        for (ArrayList<SpriteEntry> entries : renderList) {
            pointer = 0;
            System.out.println(entries.size());
            float[] data = new float[entries.size() * INSTANCE_DATA_LENGTH];
            entries.sort(new SpriteComparator());
            for (SpriteEntry s : entries) {
                float width = sheet.getSpriteWidth();
                float height = sheet.getSpriteHeight();
                float scale = sheet.getScale();
                Vector2f pos = s.position;
                pos.y -= s.height;

                data[pointer++] = pos.x;
                data[pointer++] = pos.y;
                data[pointer++] = pos.x;
                data[pointer++] = pos.y + height * scale;
                data[pointer++] = pos.x + width * scale;
                data[pointer++] = pos.y;
                data[pointer++] = pos.x + width * scale;
                data[pointer++] = pos.y + height * scale;
                data[pointer++] = (s.texX * width) / sheetWidth;
                data[pointer++] = ((s.texY + 1) * height) / sheetHeight;
                data[pointer++] = (s.texX * width) / sheetWidth;
                data[pointer++] = (s.texY * height) / sheetHeight;
                data[pointer++] = ((s.texX + 1) * width) / sheetWidth;
                data[pointer++] = ((s.texY + 1) * height) / sheetHeight;
                data[pointer++] = ((s.texX + 1) * width) / sheetWidth;
                data[pointer++] = (s.texY * height) / sheetHeight;
            }
//            spriteBuffer.put(data);
//            spriteBuffer.flip();

            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferSubData(GL_ARRAY_BUFFER, (long) 0, data);

            shader.bind();
            shader.setSpriteSheet();
            shader.setProjection(projection);

            sheet.getSheet().bind();

//        glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, sprites.size());
            glDrawArraysInstanced(GL_TRIANGLE_STRIP, 0, 4, entries.size());
        }
    }
}
