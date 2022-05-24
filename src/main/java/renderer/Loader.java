package renderer;

import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.*;

public class Loader {
    public Mesh loadToVAO(float[] positions, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 2, positions);
        unbindVAO();
        return new Mesh(vaoID, indices.length);
    }

    private int createVAO() {
        int vao = -1;
        try (MemoryStack stack = stackPush()) {
            IntBuffer ib = stack.callocInt(1);
            glGenVertexArrays(ib);
            vao = ib.get(0);
            glBindVertexArray(vao);
        }

        return vao;
    }

    private void unbindVAO() {
        glBindVertexArray(0);
    }

    private void storeDataInAttributeList(int attrIndex, int coordCount, float[] data) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer ib = stack.callocInt(1);
            glGenBuffers(ib);
            int vbo = ib.get(0);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);

            FloatBuffer buffer = stack.callocFloat(data.length);
            glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(attrIndex);
            glVertexAttribPointer(attrIndex, coordCount, GL_FLOAT, false, 0, 0);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }
    }

    private void bindIndicesBuffer(int[] indices) {
        int ebo = -1;
        try (MemoryStack stack = stackPush()) {
            IntBuffer eb = stack.callocInt(1);
            glGenBuffers(eb);
            ebo = eb.get(0);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

            IntBuffer indexBuffer = stack.callocInt(indices.length);
            indexBuffer.put(indices);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        }
    }
}
