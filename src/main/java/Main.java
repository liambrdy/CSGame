import assets.AssetManager;
import assets.Packer;
import core.Input;
import core.Window;
import game.Entity;
import game.Scene;
import org.joml.Matrix2f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import renderer.*;
import shaders.StaticShader;
import shaders.TextShader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.NumberFormat;
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

        while (!window.shouldClose()) {
            window.update();

            MasterRenderer.beginScene();

//            MasterRenderer.drawText("Hello world this is really great", pos, color);

//            MasterRenderer.drawSprite(new Vector2f(0.0f, 1.0f), 0);
//            MasterRenderer.drawSprite(new Vector2f(1.0f, 0.0f), 0);
//            MasterRenderer.drawSprite(new Vector2f(1.0f, 1.0f), 32.0f*2, 2.0f, 0.0f, 9.0f);
            Scene sc = new Scene();
//            Scene.drawEditor();
            Vector2f selected = getTileCoordinate();
//            System.out.println("(" + (int)selected.x + ", " + (int)selected.y + ")");
            for (int y = 30; y >= 0; y--) {
                for (int x = 30; x >= 0; x--) {
                    float h = 0.0f;
                    if ((int)selected.x == x && (int)selected.y == y) {
                        MasterRenderer.drawIsoTile(new Vector2f((float)x, (float)y), new Vector4f(1.0f, 0.2f, 0.2f, 0.8f));
                    }
//                    MasterRenderer.drawSprite(new Vector2f((float) x, (float) y), (float)Math.sin((30 - x) + y + glfwGetTime() * 10.0f) * 5.0f + (float)Math.sin(x + y + glfwGetTime() * 10.0f) * 5.0f, 4.0f, 9.0f);
                    MasterRenderer.drawSprite(new Vector2f((float) x, (float) y), (float)Math.sin((30 - x) + y + glfwGetTime() * 2.0f) * 7.0f, 4.0f, 9.0f);
//                    MasterRenderer.drawSprite(new Vector2f((float) x, (float) y), 0.0f, 1.0f, 9.0f);
//                    MasterRenderer.drawSprite(new Vector2f((float) x, (float) y), -32.0f * 1.0f, 0.0f, 9.0f);
                }
//                h += 2.0f;
            }
            MasterRenderer.drawIsoTile(new Vector2f(10.0f, 10.0f), new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));

//            MasterRenderer.drawLine(new Vector2f(0.0f, 0.0f), new Vector2f(width, height), new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));
//            MasterRenderer.drawCoordinateSystem();
//            MasterRenderer.drawIsoLine(new Vector2f(10.0f, 10.0f), new Vector2f(10.0f, 11.0f), new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));

            MasterRenderer.endScene();

            Input.Update();
        }
    }

    private static Vector2f getTileCoordinate() {
        Vector2f pos = Input.getMousePos();
//        pos.x -= MasterRenderer.getWidth() / 2.0f;
//        pos.x += 32.0f / 2.0f;
        return MasterRenderer.getCurrentSheet().toTile(pos);
    }

    private static float map(float in, float inMin, float inMax, float outMin, float outMax) {
        return outMin + ((outMax - outMin) / (inMax - inMin)) * (in - inMin);
    }
}
