import renderer.Loader;
import renderer.Mesh;
import renderer.Renderer;
import shaders.StaticShader;

public class Main {
    public static void main(String[] args) {
        Window window = new Window(1280, 720, "Game");
        Input.Init(window.getHandle());

        Loader loader = new Loader();
        Renderer renderer = new Renderer();
        StaticShader shader = new StaticShader();

        float[] vertices = {-0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f};
        int[] indices = {0, 1, 2, 2, 3, 0};
        Mesh mesh = loader.loadToVAO(vertices, indices);

        while (!window.shouldClose()) {
            window.update();
            renderer.beginScene();

            renderer.render(mesh, shader);

            Input.Update();
        }
    }
}
