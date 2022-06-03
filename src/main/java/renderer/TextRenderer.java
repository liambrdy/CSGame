package renderer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.system.MemoryStack;
import org.w3c.dom.Text;
import shaders.TextShader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.opengl.GL40.*;

public class TextRenderer {
    private static final int MAX_GLYPHS = 1024;

    private Matrix4f projection;
    private TextShader shader;
    private int glyphsVAO;
    private int glyphsVBO;
    private int glyphsEBO;
    private FloatBuffer glyphData;
    private int glyphCount;
    private int glyphBytes;
    private Font font;

    public TextRenderer(Matrix4f projection, TextShader shader, Font f) {
        this.projection = projection;
        this.shader = shader;
        font = f;

        glyphData = BufferUtils.createFloatBuffer((4*3 + 4*2 + 4*4) * MAX_GLYPHS);
        glyphCount = 0;
        glyphBytes = 0;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer vaoBuffer = stack.callocInt(1);
            IntBuffer vboBuffer = stack.callocInt(1);
            IntBuffer eboBuffer = stack.callocInt(1);

            glGenVertexArrays(vaoBuffer);
            glyphsVAO = vaoBuffer.get(0);
            glBindVertexArray(glyphsVAO);

            glGenBuffers(vboBuffer);
            glyphsVBO = vboBuffer.get(0);
            glBindBuffer(GL_ARRAY_BUFFER, glyphsVBO);
            glBufferData(GL_ARRAY_BUFFER, (long) (4*3 + 4*2 + 4*4) * MAX_GLYPHS * Float.BYTES, GL_DYNAMIC_DRAW);

            IntBuffer indices = stack.callocInt(6*MAX_GLYPHS);
            int index = 0;
            for (int i = 0; i < MAX_GLYPHS * 6; i += 6) {
                indices.put(index);
                indices.put(index + 1);
                indices.put(index + 2);

                indices.put(index + 2);
                indices.put(index + 3);
                indices.put(index);

                index += 4;
            }
            indices.flip();

            glGenBuffers(eboBuffer);
            glyphsEBO = eboBuffer.get(0);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, glyphsEBO);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 2, GL_FLOAT, false, 8 * Float.BYTES, (long)0);

            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 8 * Float.BYTES, (long)2 * Float.BYTES);

            glEnableVertexAttribArray(2);
            glVertexAttribPointer(2, 4, GL_FLOAT, false, 8 * Float.BYTES, (long)4 * Float.BYTES);
        }

        shader.bind();
        shader.setProjection(projection);
        shader.setFontTextureSlot(0);
    }

    public void beginScene() {
        glyphData.position(0);
        glyphCount = 0;
        glyphBytes = 0;
    }

    public void render(String text, Vector2f pos, Vector4f color) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer x = stack.callocFloat(1);
            FloatBuffer y = stack.callocFloat(1);
            x.put(0, pos.x);
            y.put(0, pos.y);
            while (text.length() > 0) {
                if (glyphCount >= MAX_GLYPHS)
                    resetScene();

                STBTTAlignedQuad q = font.getQuad(text.charAt(0), stack, x, y);

                pushVertex(new Vector2f(q.x0(), q.y0()), new Vector2f(q.s0(), q.t0()), color);
                pushVertex(new Vector2f(q.x1(), q.y0()), new Vector2f(q.s1(), q.t0()), color);
                pushVertex(new Vector2f(q.x1(), q.y1()), new Vector2f(q.s1(), q.t1()), color);
                pushVertex(new Vector2f(q.x0(), q.y1()), new Vector2f(q.s0(), q.t1()), color);

                glyphCount++;

                text = text.substring(1);
            }
        }
    }

    private void pushVertex(Vector2f pos, Vector2f uv, Vector4f color) {
        glyphData.put(pos.x);
        glyphData.put(pos.y);
        glyphData.put(uv.x);
        glyphData.put(uv.y);
        glyphData.put(color.x);
        glyphData.put(color.y);
        glyphData.put(color.z);
        glyphData.put(color.w);

        glyphBytes += (2+2+4) * Float.BYTES;
    }

    public void endScene() {
        if (glyphCount > 0) {
            glBindVertexArray(glyphsVAO);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, glyphsEBO);

            glBindBuffer(GL_ARRAY_BUFFER, glyphsVBO);
            glyphData.flip();
            glBufferData(GL_ARRAY_BUFFER, glyphData, GL_DYNAMIC_DRAW);

            shader.bind();

            font.getTexture().bind();

            glDrawElements(GL_TRIANGLES, glyphCount*6, GL_UNSIGNED_INT, 0);
        }
    }

    private void resetScene() {
        if (glyphCount > 0) {
            glBindVertexArray(glyphsVAO);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, glyphsEBO);

            glBindBuffer(GL_ARRAY_BUFFER, glyphsVBO);
            glyphData.flip();
            glBufferData(GL_ARRAY_BUFFER, glyphData, GL_DYNAMIC_DRAW);

            shader.bind();

            font.getTexture().bind();

            glDrawElements(GL_TRIANGLES, glyphCount*6, GL_UNSIGNED_INT, 0);
        }

        glyphData.position(0);
        glyphCount = 0;
        glyphBytes = 0;
    }
}
