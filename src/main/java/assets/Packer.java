package assets;

import game.Scene;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Packer {
    public static final String HEADER = "aset";

    public static class PackedAssets {
        private List<LoadedModel> models = new ArrayList<>();
        private List<LoadedTexture> textures = new ArrayList<>();
        private List<LoadedShader> shaders = new ArrayList<>();
        private List<Scene> scenes = new ArrayList<>();

        public void addModel(LoadedModel model) { models.add(model); }
        public void addTexture(LoadedTexture texture) { textures.add(texture); }
        public void addShader(LoadedShader shader) { shaders.add(shader); }
        public void addScene(Scene scene) { scenes.add(scene); }

        public List<LoadedModel> getModels() { return models; }
        public List<LoadedTexture> getTextures() { return textures; }
        public List<LoadedShader> getShaders() { return shaders; }
        public List<Scene> getScenes() { return scenes; }
    }

    public static void writeString(FileOutputStream stream, String str) throws IOException {
        char[] ch = str.toCharArray();
        for (char c : ch) {
            stream.write(c);
        }
    }

    public static void writeInteger(FileOutputStream stream, int i) throws IOException {
        String str = String.format("%d", i);
        stream.write(str.length());
        writeString(stream, str);
    }

    public static void writeFloat(FileOutputStream stream, float f) throws IOException {
        String str = String.format("%f", f);
        stream.write(str.length());
        writeString(stream, str);
    }

    public static PackedAssets getPackedAssets(String assetDir) {
        PackedAssets assets = new PackedAssets();

        long startTime = System.nanoTime();

        File dir = new File(assetDir);
        if (!dir.isDirectory())
            throw new RuntimeException("AssetDir is not a directory");

        getDir(dir, assets);

        long endTime = System.nanoTime();
        System.out.println("Loaded " + assets.models.size() + " models, " + assets.textures.size() + " textures, and " + assets.shaders.size() + " shaders in " +
                ((endTime - startTime) / 1000000000.0) + " seconds");

        return assets;
    }

    public static void pack(String assetDir, String outputPath) {
        long loadStart = System.nanoTime();
        PackedAssets assets = getPackedAssets(assetDir);
        long loadEnd = System.nanoTime();

        System.out.println("Loaded " + assets.models.size() + " models, " + assets.textures.size() + " textures, and " + assets.shaders.size() + " shaders in " +
                ((loadEnd - loadStart) / 1000000000.0) + " seconds");

        long packStart = System.nanoTime();

        try (FileOutputStream fileStream = new FileOutputStream(outputPath)) {
            DataOutputStream stream = new DataOutputStream(fileStream);
//            writeString(stream, "aset");
            stream.writeBytes(HEADER);
            stream.writeInt(assets.shaders.size() + assets.textures.size() + assets.models.size() + assets.scenes.size());
            for (LoadedShader shader : assets.shaders)
                shader.write(stream);

            for (LoadedTexture texture : assets.textures)
                texture.write(stream);

            for (LoadedModel model : assets.models)
                model.write(stream);

            for (Scene s : assets.scenes)
                s.write(stream);

            stream.write(0);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to find asset file: " + outputPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not asset file: " + outputPath);
        }

        long packEnd = System.nanoTime();
        System.out.println("Packed " + assets.models.size() + " models, " + assets.textures.size() + " textures, and " + assets.shaders.size() + " shaders in " +
                ((packEnd - packStart) / 1000000000.0) + " seconds");
    }

    private static void getDir(File dir, PackedAssets assets) {
        File[] dirListing = dir.listFiles();
        if (dirListing != null) {
            for (File child : dirListing) {
                if (child.isDirectory())
                    getDir(child, assets);
                else {
                    String name = child.getName();
                    String ext = name.substring(name.lastIndexOf(".") + 1);
                    AssetType type = AssetType.fromString(ext);
                    switch (type) {
                        case Model -> assets.addModel(new LoadedModel(child));
                        case Texture -> assets.addTexture(new LoadedTexture(child));
                        case Shader -> assets.addShader(new LoadedShader(child));
                        case Scene -> assets.addScene(new Scene(child, false));
                        default -> {
                            if (ext.equals("mtl")) break;
                            throw new RuntimeException("Unhandled Asset type: " + child);
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        pack("src/main/resources", "assets.bin");
        PackedAssets assets = Unpacker.unpack("assets.bin", false);
        System.out.println("UnpackedA " + assets.models.size() + " models, " + assets.textures.size() + " textures, and " + assets.shaders.size() + " shaders");
    }
}
