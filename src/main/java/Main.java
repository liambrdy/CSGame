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

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        float width = 1280.0f;
        float height = 720.0f;
        Window window = new Window(1280, 720, "Game");
        Input.Init(window.getHandle());

//        Packer.pack("src/main/resources", "assets.bin");
        AssetManager.init("assets.bin");
//        AssetManager.init();

        MasterRenderer.init(width, height);

//        Camera camera = new Camera(new Vector3f(0.0f, 0.0f, 0.0f));

//        Texture grass = new Texture("textures/grass.jpg");

//        Model tree = new Model("Tree");
//        Entity tree = new Entity(new Model("dungeon_norm"), new Vector3f(0.0f, 0.0f, 0.0f), 5.0f);

        Vector2f pos = new Vector2f(100.0f, 100.0f);
        Vector4f color = new Vector4f(0.8f, 0.2f, 0.2f, 1.0f);

        Vector2f p = new Vector2f(0.0f, 0.0f);

        while (!window.shouldClose()) {
            window.update();
//            camera.move();

            MasterRenderer.beginScene();

//            grass.bind();


//            renderer.render(mesh, shader);
//            MasterRenderer.drawModel(tree.getModel(), tree.getTransform());
//
//            MasterRenderer.drawText("Hello world this is really great", pos, color);

//            MasterRenderer.drawSprite(new Vector2f(0.0f, 1.0f), 0);
//            MasterRenderer.drawSprite(new Vector2f(1.0f, 0.0f), 0);
//            MasterRenderer.drawSprite(new Vector2f(0.0f, 0.0f), 0);
            for (int y = 20; y >= 0; y--) {
                for (int x = 20; x >= 0; x--) {
                    MasterRenderer.drawSprite(new Vector2f((float)x, (float)y), 5.0f,0);
                }
            }
//            MasterRenderer.drawScene(entities, lights, camera);

            MasterRenderer.endScene();

            Input.Update();
        }
    }
}
