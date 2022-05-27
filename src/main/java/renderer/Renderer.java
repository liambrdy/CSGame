package renderer;

import org.joml.Matrix4f;
import shaders.StaticShader;

import static org.lwjgl.opengl.GL33.*;

public class Renderer {
    private final Matrix4f projection = new Matrix4f();

    public Renderer() {
        glEnable(GL_DEPTH_TEST);

        projection.perspective((float)Math.toRadians(45.0f), 1280.0f / 720.0f, 0.01f, 1000.0f);
    }

    public void beginScene() {
        glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Mesh mesh, StaticShader shader) {
        shader.bind();
        shader.setProjection(projection);
        glBindVertexArray(mesh.getVertexArray());
        glDrawElements(GL_TRIANGLES, mesh.getCount(), GL_UNSIGNED_INT, 0);
    }

    public void render(Model model, StaticShader shader) {
        shader.bind();
        shader.setProjection(projection);
        for (int i = 0; i < model.getMeshes().length; i++) {
            Mesh m = model.getMeshes()[i];
            glBindVertexArray(m.getVertexArray());
            Material mat = model.getMaterial(i);
            mat.setUniforms(shader);
            glDrawElements(GL_TRIANGLES, m.getCount(), GL_UNSIGNED_INT, 0);
        }
    }
}
