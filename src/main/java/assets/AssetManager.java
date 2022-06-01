package assets;

import org.lwjgl.BufferUtils;
import renderer.Texture;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_RGBA;

public class AssetManager {
    private static Packer.PackedAssets assets;
    private static Map<String, LoadedShader> shaders;
    private static Map<String, LoadedTexture> textures;
    private static Map<String, LoadedModel> models;
    private static Map<String, Texture> glTextures;

    private static Texture defaultTexture;

    public static void init(String path) {
        assets = Unpacker.unpack(path);

        shaders = new HashMap<String, LoadedShader>();
        textures = new HashMap<String, LoadedTexture>();
        models = new HashMap<String, LoadedModel>();
        glTextures = new HashMap<String, Texture>();

        for (LoadedTexture tx : assets.getTextures()) {
            textures.put(tx.getName(), tx);
            glTextures.put(tx.getName(), new Texture(tx));
        }

        for (LoadedShader sh : assets.getShaders())
            shaders.put(sh.getName(), sh);

        for (LoadedModel md : assets.getModels())
            models.put(md.getName(), md);

        ByteBuffer b = BufferUtils.createByteBuffer(Integer.BYTES);
        b.putInt(0xffffffff);
        b.flip();
        defaultTexture = new Texture(1, 1, b, GL_RGBA);
    }

    public static void init() {
        assets = Packer.getPackedAssets("src/main/resources/");

        shaders = new HashMap<String, LoadedShader>();
        textures = new HashMap<String, LoadedTexture>();
        models = new HashMap<String, LoadedModel>();
        glTextures = new HashMap<String, Texture>();

        for (LoadedTexture tx : assets.getTextures()) {
            textures.put(tx.getName(), tx);
            glTextures.put(tx.getName(), new Texture(tx));
        }

        for (LoadedShader sh : assets.getShaders())
            shaders.put(sh.getName(), sh);

        for (LoadedModel md : assets.getModels())
            models.put(md.getName(), md);

        ByteBuffer b = BufferUtils.createByteBuffer(Integer.BYTES);
        b.putInt(0xffffffff);
        b.flip();
        defaultTexture = new Texture(1, 1, b, GL_RGBA);
    }

    public static LoadedShader getLoadedShader(String name) {
        if (shaders.containsKey(name))
            return shaders.get(name);

        throw new RuntimeException("Failed to find shader with name " + name);
    }

    public static LoadedTexture getLoadedTexture(String name) {
        if (textures.containsKey(name))
            return textures.get(name);

        throw new RuntimeException("Failed to find texture with name " + name);
    }

    public static LoadedModel getLoadedModel(String name) {
        if (models.containsKey(name))
            return models.get(name);

        throw new RuntimeException("Failed to find model with name " + name);
    }

    public static Texture getTexture(String name) {
        if (glTextures.containsKey(name))
            return glTextures.get(name);

        throw new RuntimeException("Failed to find opengl texture with name " + name);
    }

    public static Texture getDefaultTexture() {
        return defaultTexture;
    }
}
