import renderer.Loader;
import renderer.Mesh;
import renderer.Renderer;
import renderer.Texture;
import shaders.StaticShader;

public class Main {
    public static void main(String[] args) {
        Window window = new Window(1280, 720, "Game");
        Input.Init(window.getHandle());

        Loader loader = new Loader();
        Renderer renderer = new Renderer();
        StaticShader shader = new StaticShader();

        Texture grass = new Texture("textures/grass.jpg");

        float[] vertices = {-0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f};
        float[] uvs = {0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
        int[] indices = {0, 1, 2, 2, 3, 0};
        Mesh mesh = loader.loadToVAO(vertices, indices, uvs);

        while (!window.shouldClose()) {
            window.update();
            renderer.beginScene();

            grass.bind();

            renderer.render(mesh, shader);

            Input.Update();
        }
    }
}
