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

        Scene sc = new Scene();

        while (!window.shouldClose()) {
            window.update();

            MasterRenderer.beginScene();

            sc.drawEditor();
            sc.render();
            sc.update();

            MasterRenderer.endScene();

            Input.Update();
        }
    }

    private static float map(float in, float inMin, float inMax, float outMin, float outMax) {
        return outMin + ((outMax - outMin) / (inMax - inMin)) * (in - inMin);
    }
}
