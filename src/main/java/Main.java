import assets.AssetManager;
import assets.Packer;
import core.Input;
import core.Window;
import org.joml.Vector3f;
import renderer.Camera;
import renderer.Mesh;
import renderer.Model;
import renderer.Renderer;
import shaders.StaticShader;

import static org.lwjgl.glfw.GLFW.*;

public class Main {
    public static void main(String[] args) {
        Window window = new Window(1280, 720, "Game");
        Input.Init(window.getHandle());

        Packer.pack("src/main/resources", "assets.bin");
        AssetManager.init("assets.bin");

        Renderer renderer = new Renderer();
        StaticShader shader = new StaticShader();
        Camera camera = new Camera(new Vector3f(0.0f, 50.0f, 0.0f));

//        Texture grass = new Texture("textures/grass.jpg");

        Model tree = new Model("Tree");

        float[] vertices = {-0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f};
        float[] uvs = {0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
        int[] indices = {0, 1, 2, 2, 3, 0};
        Mesh mesh = Mesh.create(vertices, indices, uvs);

        Vector3f lightPos = new Vector3f(-80.0f, 10.0f, 0.0f);

        while (!window.shouldClose()) {
            window.update();
            renderer.beginScene();

//            grass.bind();

            camera.move();

            shader.setView(camera.getViewMatrix());
            shader.setLightPos(lightPos);
            shader.setViewPos(camera.getPos());

//            renderer.render(mesh, shader);
            renderer.render(tree, shader);

            Input.Update();
        }
    }
}
