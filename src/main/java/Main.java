import assets.AssetManager;
import assets.Packer;
import core.Input;
import core.Window;
import game.Entity;
import org.joml.Matrix2f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import renderer.*;
import shaders.StaticShader;
import shaders.TextShader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

public class Main {
    public static void main(String[] args) throws IOException {
        float width = 1280.0f;
        float height = 720.0f;
        Window window = new Window(1280, 720, "Game");
        Input.Init(window.getHandle());

        boolean inJar = false;

        String protocol = Main.class.getResource(Main.class.getName() + ".class").getProtocol();
        if (Objects.equals(protocol, "jar"))
            inJar = true;

        if (!inJar)
            Packer.pack("src/main/resources", "assets.bin");
        AssetManager.init("assets.bin", inJar);
//        AssetManager.init();

        MasterRenderer.init(width, height);

//        Camera camera = new Camera(new Vector3f(0.0f, 0.0f, 0.0f));

//        Texture grass = new Texture("textures/grass.jpg");

//        Model tree = new Model("Tree");
//        Entity tree = new Entity(new Model("dungeon_norm"), new Vector3f(0.0f, 0.0f, 0.0f), 5.0f);

        Vector2f pos = new Vector2f(100.0f, 100.0f);
        Vector4f color = new Vector4f(0.8f, 0.2f, 0.2f, 1.0f);

        Vector2f p = new Vector2f(0.0f, 0.0f);

        screenToTile = new Matrix2f();
        screenToTile.m00 = 0.5f * 32.0f * 1.0f;
        screenToTile.m01 = 0.25f * 32.0f * 1.0f;
        screenToTile.m10 = -0.5f * 32.0f * 1.0f;
        screenToTile.m11 = 0.25f * 32.0f * 1.0f;
        screenToTile.invert();

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
//            MasterRenderer.drawSprite(new Vector2f(1.0f, 1.0f), 32.0f*2, 2.0f, 0.0f, 9.0f);
//            MasterRenderer.drawSprite(new Vector2f(1.0f, 10.0f), 32.0f * 2, 2.0f, 0.0f, 9.0f);
            Vector2f selected = getTileCoordinate();
//            System.out.println("(" + (int)selected.x + ", " + (int)selected.y + ")");
            for (int y = 30; y >= 0; y--) {
                for (int x = 30; x >= 0; x--) {
                    float h = 0.0f;
                    if ((int)selected.x == x && (int)selected.y == y) {
                        h = 5.0f;
                    }
//                    MasterRenderer.drawSprite(new Vector2f((float) x, (float) y), (float)Math.sin(x + y + glfwGetTime() * 10.0f) * 10.0f, 1.0f, 0.0f, 9.0f);
                    MasterRenderer.drawSprite(new Vector2f((float) x, (float) y), h, 1.0f, 0.0f, 9.0f);
                }
//                h += 2.0f;
            }
//            MasterRenderer.drawScene(entities, lights, camera);

            MasterRenderer.endScene();

            Input.Update();
        }
    }

    private static Matrix2f screenToTile;

    private static Vector2f getTileCoordinate() {
        Vector2f pos = Input.getMousePos();
        pos.x -= MasterRenderer.getWidth() / 2.0f;
//        pos.x += 32.0f / 2.0f;
        return pos.mul(screenToTile);
    }

    private static float map(float in, float inMin, float inMax, float outMin, float outMax) {
        return outMin + ((outMax - outMin) / (inMax - inMin)) * (in - inMin);
    }
}
