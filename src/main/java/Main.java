import assets.AssetManager;
import assets.Packer;
import core.Input;
import core.Window;
import game.Entity;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import renderer.*;
import shaders.StaticShader;
import shaders.TextShader;

public class Main {
    public static void main(String[] args) {
        float width = 1280.0f;
        float height = 720.0f;
        Window window = new Window(1280, 720, "Game");
        Input.Init(window.getHandle());

//        Packer.pack("src/main/resources", "assets.bin");
//        AssetManager.init("assets.bin");
        AssetManager.init();

        MasterRenderer.init();

        StaticShader shader = new StaticShader();
        Camera camera = new Camera(new Vector3f(0.0f, 50.0f, 0.0f));
        Font font = new Font("c:/windows/fonts/times.ttf");
        MasterRenderer.addRenderer(new MeshRenderer(width, height, shader, camera));
        MasterRenderer.addRenderer(new TextRenderer(new TextShader(), width, height, font));


//        Texture grass = new Texture("textures/grass.jpg");

//        Model tree = new Model("Tree");
        Entity tree = new Entity(new Model("Tree"), new Vector3f(0.0f, 0.0f, 0.0f), 10.0f);

        Vector3f lightPos = new Vector3f(-80.0f, 10.0f, 0.0f);

        Vector2f pos = new Vector2f(100.0f, 100.0f);
        Vector4f color = new Vector4f(0.8f, 0.7f, 0.2f, 1.0f);

        while (!window.shouldClose()) {
            window.update();
            camera.move();

            MasterRenderer.beginScene();

//            grass.bind();


//            renderer.render(mesh, shader);
            MasterRenderer.drawModel(tree.getModel(), tree.getTransform());

            MasterRenderer.drawText("Hello world this is really great", pos, color);

            MasterRenderer.endScene();

            Input.Update();
        }
    }
}
