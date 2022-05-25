package renderer;

import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Mesh {
    private final int vao;
    private int count;

    public Mesh(int vao, int count) {
        this.vao = vao;
        this.count = count;
    }

    public int getVertexArray() {
        return vao;
    }

    public int getCount() {
        return count;
    }

    public static Mesh create(float[] positions, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 2, positions);
        unbindVAO();
        return new Mesh(vaoID, indices.length);
    }

    public static Mesh create(float[] positions, int[] indices, float[] uvs) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, uvs);
        unbindVAO();
        return new Mesh(vaoID, indices.length);
    }

    public static Mesh create(float[] positions, float[] uvs, float[] normals, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, uvs);
        storeDataInAttributeList(2, 3, normals);
        unbindVAO();
        return new Mesh(vaoID, indices.length);
    }

    private static int createVAO() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer ib = stack.callocInt(1);
            glGenVertexArrays(ib);
            int vao = ib.get(0);
            glBindVertexArray(vao);
            return vao;
        }
    }

    private static void unbindVAO() {
        glBindVertexArray(0);
    }

    private static void storeDataInAttributeList(int attrIndex, int coordCount, float[] data) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer ib = stack.callocInt(1);
            glGenBuffers(ib);
            int vbo = ib.get(0);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);

            FloatBuffer buffer = stack.callocFloat(data.length);
            buffer.put(data);
            buffer.flip();
            glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(attrIndex);
            glVertexAttribPointer(attrIndex, coordCount, GL_FLOAT, false, 0, 0);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
    }

    private static void bindIndicesBuffer(int[] indices) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer eb = stack.callocInt(1);
            glGenBuffers(eb);
            int ebo = eb.get(0);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

            IntBuffer indexBuffer = stack.callocInt(indices.length);
            indexBuffer.put(indices);
            indexBuffer.flip();
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        }
    }
}
