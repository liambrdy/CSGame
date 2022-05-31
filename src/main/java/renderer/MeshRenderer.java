package renderer;

import game.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import shaders.StaticShader;

import static org.lwjgl.opengl.GL33.*;

public class MeshRenderer implements IRenderer {
    private final Matrix4f projection = new Matrix4f();
    private float width, height;
    private StaticShader shaderHandle;
    private Camera cameraHandle;

    static private MeshRenderer handle;

    public MeshRenderer(float w, float h, StaticShader shader, Camera camera) {
        if (handle != null)
            throw new RuntimeException("This is not the first renderer to be created");

        handle = this;

        shaderHandle = shader;
        cameraHandle = camera;

        width = w;
        height = h;

        projection.perspective((float)Math.toRadians(45.0f), width / height, 0.01f, 1000.0f);
    }

    public void beginScene() {
        glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shaderHandle.bind();
        shaderHandle.setView(cameraHandle.getViewMatrix());
        shaderHandle.setViewPos(cameraHandle.getPos());
    }

    public void render(Mesh mesh, StaticShader shader) {
        shader.bind();
        shader.setProjection(projection);
        glBindVertexArray(mesh.getVertexArray());
        glDrawElements(GL_TRIANGLES, mesh.getCount(), GL_UNSIGNED_INT, 0);
    }

    public void render(Model model, Matrix4f transform) {
        shaderHandle.bind();
        shaderHandle.setProjection(projection);
        shaderHandle.setTransform(transform);
        for (int i = 0; i < model.getMeshes().length; i++) {
            Mesh m = model.getMeshes()[i];
            glBindVertexArray(m.getVertexArray());
            Material mat = model.getMaterial(i);
            mat.setUniforms(shaderHandle);
            glDrawElements(GL_TRIANGLES, m.getCount(), GL_UNSIGNED_INT, 0);
        }
    }

    public void render(Entity entity, StaticShader shader) {
        shader.bind();
        shader.setProjection(projection);
        shader.setTransform(entity.getTransform());
        Model model = entity.getModel();
        for (int i = 0; i < model.getMeshes().length; i++) {
            Mesh m = model.getMeshes()[i];
            glBindVertexArray(m.getVertexArray());
            Material mat = model.getMaterial(i);
            mat.setUniforms(shader);
            glDrawElements(GL_TRIANGLES, m.getCount(), GL_UNSIGNED_INT, 0);
        }
    }

    @Override
    public void endScene() {
    }

    public void onWindowResize(float w, float h) {
        width = w;
        height = h;

        projection.perspective((float)Math.toRadians(45.0f), width / height, 0.01f, 1000.0f);
    }

    public static MeshRenderer getHandle() { return handle; }
}
