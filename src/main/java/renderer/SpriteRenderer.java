package renderer;

import org.joml.Matrix2f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
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
    public static class SpriteEntry {
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

    public class TextureEntry {
        private Texture texture;
        private Vector4f rect;

        public TextureEntry(Texture t, Vector4f r) {
            texture = t;
            rect = r;
        }
    }

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
    List<TextureEntry> textures;

    public SpriteRenderer(SpriteSheet s, Matrix4f ortho) {
        shader = new SpriteShader();
        sheet = s;
        projection = ortho;

        sprites = new ArrayList<>();
        textures = new ArrayList<>();

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
        sprites.clear();
        textures.clear();
        count = 0;
    }

    public void render(Vector2f pos, float height, float x, float y) {
//        Matrix2f m = new Matrix2f(0.5f * 32.0f, 0.25f * 32.0f, -0.5f * 32.0f, 0.25f * 32.0f);
//        Vector2f screen = pos.mul(m);
//        Vector2f screen = new Vector2f(pos.x * 0.5f * sheet.getSpriteWidth() * sheet.getScale() + pos.y * -0.5f * sheet.getSpriteHeight() * sheet.getScale(),
//                                       pos.x * 0.25f * sheet.getSpriteWidth() * sheet.getScale() + pos.y * 0.25f * sheet.getSpriteHeight() * sheet.getScale());
        Vector2f screen = sheet.toScreen(pos);

        sprites.add(new SpriteEntry(screen, height, x, y));
        count++;
    }

    public void renderTexture(Vector4f rect, Texture txt) {
        textures.add(new TextureEntry(txt, rect));
    }


    public void endScene() {
        float sheetWidth = sheet.getSheet().getWidth();
        float sheetHeight = sheet.getSheet().getHeight();

        sprites.sort(new SpriteComparator());
        int entryCount = sprites.size() > MAX_INSTANCES ? sprites.size() / MAX_INSTANCES : 1;
        for (int i = 0; i < entryCount; i++) {
            ArrayList<SpriteEntry> entries = new ArrayList<>();
            for (int j = 0; j < MAX_INSTANCES; j++) {
                if (i * MAX_INSTANCES + j < sprites.size())
                    entries.add(sprites.get(i * MAX_INSTANCES + j));
            }
            pointer = 0;
            float[] data = new float[entries.size() * INSTANCE_DATA_LENGTH];
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

        for (TextureEntry e : textures) {
            pointer = 0;
            float[] data = new float[INSTANCE_DATA_LENGTH];

            data[pointer++] = e.rect.x;
            data[pointer++] = e.rect.y;
            data[pointer++] = e.rect.x;
            data[pointer++] = e.rect.y + e.rect.w;
            data[pointer++] = e.rect.x + e.rect.z;
            data[pointer++] = e.rect.y;
            data[pointer++] = e.rect.x + e.rect.z;
            data[pointer++] = e.rect.y + e.rect.w;
            data[pointer++] = 0.0f;
            data[pointer++] = 1.0f;
            data[pointer++] = 0.0f;
            data[pointer++] = 0.0f;
            data[pointer++] = 1.0f;
            data[pointer++] = 1.0f;
            data[pointer++] = 1.0f;
            data[pointer++] = 0.0f;

            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferSubData(GL_ARRAY_BUFFER, (long) 0, data);

            shader.bind();
            shader.setSpriteSheet();
            shader.setProjection(projection);

            e.texture.bind();

//        glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, sprites.size());
            glDrawArraysInstanced(GL_TRIANGLE_STRIP, 0, 4, textures.size());
        }
    }
}
