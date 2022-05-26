import assets.AssetManager;
import assets.Packer;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIScene;
import renderer.Camera;
import renderer.Mesh;
import renderer.Renderer;
import renderer.Texture;
import shaders.StaticShader;

import java.util.Vector;

import static org.lwjgl.assimp.Assimp.*;

public class Main {
    public static void main(String[] args) {
        Window window = new Window(1280, 720, "Game");
        Input.Init(window.getHandle());

        Packer.pack("src/main/resources", "assets.bin");
        AssetManager.init("assets.bin");

        Renderer renderer = new Renderer();
        StaticShader shader = new StaticShader();
        Camera camera = new Camera(new Vector3f(0.0f, 50.0f, -100.0f));

//        Texture grass = new Texture("textures/grass.jpg");

        Mesh[] treeModels = ObjLoader.loadObj("src/main/resources/models/low-poly-mill.obj");

        float[] vertices = {-0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f};
        float[] uvs = {0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
        int[] indices = {0, 1, 2, 2, 3, 0};
        Mesh mesh = Mesh.create(vertices, indices, uvs);

        while (!window.shouldClose()) {
            window.update();
            renderer.beginScene();

//            grass.bind();

            shader.setView(camera.getViewMatrix());

//            renderer.render(mesh, shader);
            for (Mesh m : treeModels)
                renderer.render(m, shader);

            Input.Update();
        }
    }
}
