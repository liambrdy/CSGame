package renderer;

import shaders.StaticShader;

import static org.lwjgl.opengl.GL33.*;

public class Renderer {
    public Renderer() {

    }

    public void beginScene() {
        glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Mesh mesh, StaticShader shader) {
        shader.bind();
        glBindVertexArray(mesh.getVertexArray());
        glDrawElements(GL_TRIANGLES, mesh.getCount(), GL_UNSIGNED_INT, 0);
    }
}
