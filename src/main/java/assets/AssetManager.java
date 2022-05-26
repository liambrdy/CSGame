package assets;

import java.util.HashMap;
import java.util.Map;

public class AssetManager {
    private static Packer.PackedAssets assets;
    private static Map<String, LoadedShader> shaders;
    private static Map<String, LoadedTexture> textures;
    private static Map<String, LoadedModel> models;

    public static void init(String path) {
        assets = Unpacker.unpack(path);

        shaders = new HashMap<String, LoadedShader>();
        textures = new HashMap<String, LoadedTexture>();
        models = new HashMap<String, LoadedModel>();

        for (LoadedTexture tx : assets.getTextures())
            textures.put(tx.getName(), tx);

        for (LoadedShader sh : assets.getShaders())
            shaders.put(sh.getName(), sh);

        for (LoadedModel md : assets.getModels())
            models.put(md.getName(), md);
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
}
